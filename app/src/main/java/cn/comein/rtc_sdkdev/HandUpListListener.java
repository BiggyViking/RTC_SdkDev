package cn.comein.rtc_sdkdev;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class HandUpListListener extends BroadcastReceiver{

    private List<String> handUpMemberList;

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public List<String> getHandUpMemberList() {
        return handUpMemberList;
    }

    public void setHandUpMemberList(List<String> handUpMemberList) {
        this.handUpMemberList = handUpMemberList;
    }
}
