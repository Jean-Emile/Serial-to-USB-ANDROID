package Ftdi_usb;
import java.util.EventObject;

import android.app.Activity;

/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public class Ftdi_Event  extends EventObject   {

	private ByteFIFO dataincomming;
	
	public Ftdi_Event(Activity root,ByteFIFO ptr) {
		super(root);
		this.dataincomming =ptr;
	}

	public byte[] read() {
		return dataincomming.removeAll();
	}
	
}
