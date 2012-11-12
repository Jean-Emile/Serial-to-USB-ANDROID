package eu.powet.android.serialUSB;
import java.util.EventObject;

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


    public  byte readByte() throws InterruptedException
    {
        return dataincomming.remove();
    }
	
}
