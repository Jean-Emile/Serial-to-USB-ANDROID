package Ftdi_usb;

import java.util.EventListener;

/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public interface  Ftid_listener extends EventListener{
	void incomingDataEvent(Ftdi_Event evt);
}
