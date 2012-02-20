package Serial_USB_Android;

import java.util.EventListener;

/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public interface SerialListener extends EventListener{
	void incomingDataEvent(SerialEvent evt);
}
