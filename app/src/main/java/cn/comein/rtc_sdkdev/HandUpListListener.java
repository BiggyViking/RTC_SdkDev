package cn.comein.rtc_sdkdev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class HandUpListListener extends BroadcastReceiver{

    private static final String TAG = "LibWebrtcMedia_listener";
    private List<String> handUpMemberList;

    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getExtras().getInt("state");
        String idList = intent.getExtras().getString("idList");
        String info = "Listener receive state = " + state + " idList : " + idList;
        Log.d(TAG, info);
    }

    public void onClick(View v){

    }

    public List<String> getHandUpMemberList() {
        return handUpMemberList;
    }

    public void setHandUpMemberList(List<String> handUpMemberList) {
        this.handUpMemberList = handUpMemberList;
    }
}
