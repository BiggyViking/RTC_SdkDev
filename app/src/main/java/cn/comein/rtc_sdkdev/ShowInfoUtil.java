package cn.comein.rtc_sdkdev;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/4/6.
 */

public class ShowInfoUtil {
    private static final String TAG = "LibWebrtcMedia_info";

    private Context context;

    public ShowInfoUtil(Context context) {
        this.context = context;
    }

    public void show(String info) {
        Toast.makeText(context, info, Toast.LENGTH_LONG).show();
        Log.d(TAG, info);
    }

}
