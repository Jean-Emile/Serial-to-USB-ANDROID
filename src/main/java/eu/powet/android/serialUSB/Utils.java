package eu.powet.android.serialUSB;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by jed
 * User: jedartois@gmail.com
 * Date: 27/04/12
 * Time: 15:39
 */
public class Utils {


    private static String readFile(String filePath)
    {
        File file = new File(filePath);
        if(!file.exists()){return "";}
        if(file.isDirectory()){return "";}

        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));

            char[] buf = new char[1024];
            int numRead=0;

            while((numRead=reader.read(buf)) != -1)
            {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        String res = fileData.toString();
        if(res == null){
            res = "";
        }
        return res.trim();
    }


    public static HashMap<String, UsbDeviceID> scanning(){
        HashMap<String, UsbDeviceID> liste = new  HashMap<String, UsbDeviceID>();
        UsbDeviceID usb;
        File dir = new File("/sys/bus/usb/devices/");
        if (!dir.isDirectory()){return liste;}
        for (File child : dir.listFiles())
        {

            if (".".equals(child.getName()) || "..".equals(child.getName())) {
                continue; // Ignore the self and parent aliases.
            }
            String parentPath = child.getAbsolutePath() + File.separator;
            usb  = new UsbDeviceID(readFile(parentPath + "idVendor")+":"+readFile(parentPath + "idProduct"));
            liste.put(readFile(parentPath + "idVendor")+" "+readFile(parentPath + "manufacturer")+new Random().nextInt(100),usb);
        }
        return  liste;
    }
}
