package eu.powet.android.serialUSB;

/**
 * Created by jed
 * User: jedartois@gmail.com
 * Date: 17/02/12
 * Time: 16:31
 */
public class ChipType {

    private String vid;
    private String pid;

    public final static String FTDI="0403:6001";
    public final static String FT232RL="0403:6001";
    public final static String FT232H="0403:6014";
    public final static String FT2232C="0403:6010";
    public final static String FT2232D="0403:6010";
    public final static String FT2232HL="0403:6011";

    public ChipType(String chip)
    {
        vid = chip.split(":")[0];
        pid = chip.split(":")[1];
    }

    public ChipType(String _vid,  String _pid){
        this.vid = _vid;
        this.pid = _pid;
    }


    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
