package eu.powet.android.serialUSB;
/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public interface ISerial {
    public void open() throws SerialError;
    public void open(String usbDeviceID,int baudrate) throws SerialError;
    public void open(String _usbDeviceID) throws SerialError;
    public abstract void open(UsbDeviceID usbDeviceID) throws SerialError;
	public abstract void close();
    public abstract void setBaudrate(int bitrate) throws SerialError;
	public abstract boolean isConnected();
	public abstract void write(byte[] data) throws SerialError;
	public abstract void write(String data) throws SerialError;
	public abstract byte[] read() throws SerialError;
	public abstract void addEventListener (SerialListener listener);
	public abstract void removeEventListener (SerialListener listener);
}
