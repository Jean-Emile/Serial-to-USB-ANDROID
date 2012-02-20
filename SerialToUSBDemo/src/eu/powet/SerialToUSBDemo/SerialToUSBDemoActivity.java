package eu.powet.SerialToUSBDemo;


import java.util.LinkedList;

import Serial_USB_Android.ISerial;
import Serial_USB_Android.SerialEvent;
import Serial_USB_Android.SerialListener;
import Serial_USB_Android.UsbDeviceID;
import Serial_USB_Android.UsbSerial;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SerialToUSBDemoActivity extends Activity {


	private LinkedList<String> data_raw = new LinkedList<String>();

	private Handler handler=new Handler();
	private ISerial usb_serial=null;
	private Button btsend;
	private TextView text;
	private EditText input_text;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		input_text  = (EditText)findViewById(R.id.text);
		text = (TextView)findViewById(R.id.Incommingdata);
		btsend = (Button)findViewById(R.id.btsend);
		
		usb_serial = new UsbSerial(UsbDeviceID.FT232RL,19200,this);
		usb_serial.open();

		
		usb_serial.addEventListener(new SerialListener() {

			@Override
			public void incomingDataEvent(final SerialEvent evt) {
		
	            handler.post(new Runnable(){

					@Override
					public void run() {
						data_raw.addFirst(new String(evt.read()));

						StringBuilder data = new StringBuilder();
						for(String c : data_raw){
							data.append(c);
						}

						text.setText(data);

						if(data_raw.size() > 20){
							data_raw.removeLast();
						}

					}
                });
			}

		});

		btsend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				usb_serial.write(input_text.getText()+"\n");
				
			}
		});

}
	
	  protected void onDestroy() {
		  usb_serial.close();
	  }
          
}	