package eu.powet.android.serialUSB;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by jed
 * User: jedartois@gmail.com
 * Date: 11/02/12
 */

public class UsbSerial implements ISerial {

    private final int SIZE_SERIALUSB =4096;
    private static final String ACTION_USB_PERMISSION = "eu.powet.SerialToUSBDemo";
    private   UsbDeviceID   usbDeviceID;
    private static UsbDevice sDevice = null;
    private static Context sActivityContext=null;
    private boolean mStop = false;
    private boolean mStopped = true;
    private UsbDeviceConnection conn;
    private ByteFIFO fifo_data_read;
    private EventListenerListSerial listenerList;
    private int baudrate;
    private UsbEndpoint epIN = null;
    private UsbEndpoint epOUT = null;
    private  int TIMEOUT = 500;
    private boolean isconnected=false;


    public void initUsbSerial(){
        listenerList = new EventListenerListSerial();
        fifo_data_read= new ByteFIFO(SIZE_SERIALUSB);
    }

    public UsbSerial(String _usbDeviceID,int baudrate,Context _sActivityContext)
    {
        initUsbSerial();
       this.setBaudrate(baudrate);
       // this.setBaudrate(19200);
        this.usbDeviceID = new UsbDeviceID(_usbDeviceID);
        this.sActivityContext =_sActivityContext;
        l("Openning "+_usbDeviceID+" baudrate "+baudrate);
    }

    public UsbSerial(Context _sActivityContext)
    {
        initUsbSerial();
        this.sActivityContext =_sActivityContext;
    }

    public void open() throws SerialError{

        try
        {
            mStop = false;
            if(mStopped)
                enumerate();
        }catch (Exception e) {
            throw new SerialError("Serial openning : "+e.getCause().toString());
        }
    }

    public void open(String _usbDeviceID) throws SerialError{
        this.usbDeviceID = new UsbDeviceID(_usbDeviceID);
        open();
    }
    public void open(UsbDeviceID _usbDeviceID) throws SerialError
    {
        this.usbDeviceID = _usbDeviceID;
        open();
    }
    public void open(String _usbDeviceID,int baudrate) throws SerialError
    {

        this.setBaudrate(baudrate);
        this.usbDeviceID = new UsbDeviceID(_usbDeviceID);
        open();
    }


    public void close() {
        try
        {
            l("close USB Serial ");
            /*
            mStop = true;
            if(sActivityContext!=null)
                sActivityContext.unregisterReceiver(mPermissionReceiver);
                */
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     /* 0x2710 300
     * 0x1388 600
     * 0x09C4 1200
     * 0x04E2 2400
     * 0x0271 4800
     * 0x4138 9600
     * 0x809C 19200
     * 0xC04E 38400
     * 0x0034 57600
     * 0x001A 115200
     * 0x000D 230400
     * 0x4006 460800
     * 0x8003 921600
     */
    @Override
    public void setBaudrate(int bitrate) throws SerialError{

        switch(bitrate)
        {
            case 300:
                baudrate=0x2710;
                break;
            case 600:
                baudrate=0x1388;
                break;
            case 1200:
                baudrate=0x09C4;
                break;
            case 2400:
                baudrate=0x0271;
                break;
            case 4800:
                baudrate=0x4138;
                break;
            case 9600:
                baudrate=0x4138;
                break;
            case 19200:
                baudrate=0x809C;
                break;
            case 38400:
                baudrate=0xC04E;
                break;
            case 57600:
                baudrate=0x0034;
                break;
            case 115200:
                baudrate=0x001A;
                break;
            case 460800:
                baudrate=0x4006;
                break;
            case 921600:
                baudrate=0x8003;
                break;
            default :
                throw new SerialError("The baudrate selected is out of scope "+bitrate);
        }
    }

    public void addEventListener (SerialListener listener) {
        listenerList.add(SerialListener.class, listener);
    }

    public void removeEventListener (SerialListener listener) {
        listenerList.remove(SerialListener.class, listener);
    }

    void fireSerialAndroidEvent(SerialEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2)
        {
            if (listeners[i] == SerialListener.class)
            {
                ((SerialListener) listeners[i + 1]).incomingDataEvent(evt);
            }
        }
    }

    private final BroadcastReceiver mPermissionReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {

                if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                {
                    l("Permission not granted :(");
                }
                else
                {
                    l("Permission granted");
                    UsbDevice dev = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (dev != null)
                    {
                        if (String.format("%04X:%04X", dev.getVendorId(),dev.getProductId()).equals(usbDeviceID.getVID_PID()))
                        {
                            mainloop(dev);//has new thread
                        }
                    }
                    else
                    {
                        l("device not present!");
                    }
                }
            }
        }
    };

    public  HashMap<String, UsbDevice> enumerate() {
        l("enumerating");
        UsbManager usbman = (UsbManager) sActivityContext.getSystemService(sActivityContext.USB_SERVICE);

        HashMap<String, UsbDevice> devlist = usbman.getDeviceList();
        Iterator<UsbDevice> deviter = devlist.values().iterator();
        PendingIntent pi = PendingIntent.getBroadcast(sActivityContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

        while (deviter.hasNext())
        {
            UsbDevice d = deviter.next();
            l("Found device: "+ String.format("%04X:%04X", d.getVendorId(),d.getProductId()));

            if (String.format("%04X:%04X", d.getVendorId(), d.getProductId()).equals(usbDeviceID.getVID_PID()) || usbDeviceID.getPid().equals("*") || usbDeviceID.getVid().equals("*")) {

                l("Device under: " + d.getDeviceName());
                sActivityContext.registerReceiver(mPermissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                if (!usbman.hasPermission(d))
                    usbman.requestPermission(d, pi);
                else
                    mainloop(d);
                break;
            }
        }
        l("no more devices found");
        return devlist;
    }

    private void mainloop(UsbDevice d) {
        sDevice = d;
        l("starting...");
        new Thread(mLoop).start();
    }

    private Runnable mLoop = new Runnable() {

        @Override
        public void run() {
            UsbDevice dev = sDevice;
            if (dev == null)
                return;
            UsbManager usbm = (UsbManager)sActivityContext.getSystemService(sActivityContext.USB_SERVICE);
            conn = usbm.openDevice(dev);

            l("Interface Count: "+dev.getInterfaceCount());

            l("Using "+String.format("%04X:%04X", sDevice.getVendorId(), sDevice.getProductId()));

            if(!conn.claimInterface(dev.getInterface(0), true))
                return;

            conn.controlTransfer(0x40, 0, 0, 0, null, 0, 0);//reset
            conn.controlTransfer(0x40, 0, 1, 0, null, 0, 0);//clear Rx
            conn.controlTransfer(0x40, 0, 2, 0, null, 0, 0);//clear Tx
            conn.controlTransfer(0x40, 0x03, baudrate, 0, null, 0, 0);//baudrate
            conn.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);//flow control none
            conn.controlTransfer(0x40, 0x04, 0x0008, 0, null, 0, 0); //data bit 8, parity none, stop bit 1, tx off

            UsbInterface usbIf = dev.getInterface(0);
            for(int i = 0; i < usbIf.getEndpointCount(); i++)
            {
                l("EP: "+String.format("0x%02X", usbIf.getEndpoint(i).getAddress())+" "+usbIf.getEndpoint(i).getType());

               if(usbIf.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK)
                {
                    l("Bulk Direction  "+usbIf.getEndpoint(i).getDirection());
                    if(usbIf.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN)
                        epIN = usbIf.getEndpoint(i);
                    else
                        epOUT = usbIf.getEndpoint(i);
                }
                else
                {
                    l("Not Bulk");
                }
            }

            if(dev.getInterfaceCount() > 0 &&( epIN != null && epOUT !=null))
            {
                isconnected =true;

                for(;;)
                {
                    byte[] buffer = new byte[SIZE_SERIALUSB];
                    boolean data=false;
                    if(conn.bulkTransfer(epIN, buffer, SIZE_SERIALUSB, TIMEOUT)>=0)
                    {
                        if(fifo_data_read.free() < SIZE_SERIALUSB)
                        {
                            ByteFIFO tmp = new ByteFIFO(fifo_data_read.getCapacity()+ SIZE_SERIALUSB);
                            try
                            {
                                tmp.add(fifo_data_read.removeAll());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            fifo_data_read = tmp;
                        }

                        for(int i=0;i< SIZE_SERIALUSB;i++)
                        {
                            if(buffer[i] != 0 && buffer[i] != 17 && buffer[i] !='`'){
                                try
                                {
                                    fifo_data_read.add(buffer[i]);
                                    data =true;
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    if(data){
                        Log.d("SERIAL_USB", ">==< fireSerialAndroidEvent >==<"+fifo_data_read.getSize());
                         fireSerialAndroidEvent(new SerialEvent(sActivityContext, fifo_data_read));
                    }
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(mStop)
                    {
                        mStopped = true;
                        return;
                    }
                }

            }else {
                l("Not Bulk");
                mStopped = true;
                return;

            }
        }
    };


    private static void l(Object s) {
        Log.d("SERIAL_USB", ">==< " + s.toString() + " >==<");
    }

    private static void e(Object s) {
        Log.e("SERIAL_USB", ">==< " + s.toString() + " >==<");
    }


    @Override
    public void write(String data) throws SerialError {
        this.write(data.getBytes());
    }

    @Override
    public void write(byte[] data) throws SerialError {
        // TODO manage if size is bigger than 4096
        int offset= 0;
        int size_toupload=0;
        byte[] buffer = new byte[SIZE_SERIALUSB];
        if(!isConnected())
        {
            open();
        }
        try
        {
            while(offset < data.length) {

                if(offset+SIZE_SERIALUSB > data.length)
                {
                    size_toupload = data.length-offset;
                }
                System.arraycopy(data, offset, buffer, 0, size_toupload);
                int size_uploaded =    conn.bulkTransfer(epOUT, buffer, size_toupload, TIMEOUT);
                if(size_uploaded<0)
                {
                    throw new SerialError(" bulk Transfer fail");
                }
                offset += size_uploaded;
            }

        }catch (Exception e)
        {
            throw new SerialError(e.getMessage());
        }
    }

    @Override
    public byte[] read() throws SerialError {
        return fifo_data_read.removeAll();
    }

    @Override
    public boolean isConnected() {

        return isconnected;
    }


    public void destroy() {
        sActivityContext = null;
        sDevice =null;
    }

}
