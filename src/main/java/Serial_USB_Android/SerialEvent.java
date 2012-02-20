package Serial_USB_Android;
import java.util.EventObject;

import android.app.Activity;
import android.content.Context;

/**
* Created by jed
* User: jedartois@gmail.com
* Date: 11/02/12
*/
public class SerialEvent extends EventObject   {

	private ByteFIFO dataincomming;
	
	public SerialEvent(Context context, ByteFIFO ptr) {
		super(context);
		this.dataincomming =ptr;
	}

	public byte[] read() {
		return dataincomming.removeAll();
	}
	
}
