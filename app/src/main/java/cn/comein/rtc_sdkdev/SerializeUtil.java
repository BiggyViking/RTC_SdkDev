package cn.comein.rtc_sdkdev;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Administrator on 2017/4/6.
 */

public class SerializeUtil {

    public static String writeObject(Object obj) throws IOException {
        if (obj == null) {
            return "";
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        String str = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
        oos.close();
        baos.close();

        return str;
    }

    public static Object readObject(String str) throws IOException, ClassNotFoundException {
        byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
        ObjectInputStream ois = new ObjectInputStream(bais);

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
