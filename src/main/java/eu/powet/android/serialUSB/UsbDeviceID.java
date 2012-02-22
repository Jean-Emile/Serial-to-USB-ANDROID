package eu.powet.android.serialUSB;

/**
 * Created by jed
 * User: jedartois@gmail.com
 * Date: 17/02/12
 * Time: 16:33
 */
public class UsbDeviceID extends ChipType {

    public UsbDeviceID(String chip) {
        super(chip);
    }

    public String getVID_PID() {
        return getVid()+":"+getPid();
    }
}
