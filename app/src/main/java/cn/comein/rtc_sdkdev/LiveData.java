package cn.comein.rtc_sdkdev;

import android.content.Context;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/10.
 */

public class LiveData {

    public String userName;
    public String passWord;
    public String meetingId;
    public MeetingType meetingType;
    public MemberRole memberRole;
    public String host;
    public String port;
    public Context context;
    public SurfaceView sv_local;
    public SurfaceView sv_remote;

    private List<String> handUpMemberList;

    public LiveData(Context ct, SurfaceView local, SurfaceView remote) {
        userName = "10087";
        passWord = "10087";
        meetingId = "conf3001";
        meetingType = MeetingType.VIDEO;
        memberRole = MemberRole.CHAIR;
        host = "112.124.125.66";
        port = "12568";
        context = ct;
        sv_local = local;
        sv_remote = remote;
        handUpMemberList = new ArrayList<>();
        handUpMemberList.clear();
    }

    public LiveData(String userName, String passWord, String meetingId, MeetingType meetingType, MemberRole memberRole, String host, String port, Context context, SurfaceView local, SurfaceView remote) {
        this.userName = userName;
        this.passWord = passWord;
        this.meetingId = meetingId;
        this.meetingType = meetingType;
        this.memberRole = memberRole;
        this.host = host;
        this.port = port;
        this.context = context;
        this.sv_local = local;
        this.sv_remote = remote;
        handUpMemberList = new ArrayList<>();
        handUpMemberList.clear();
    }

    public String toString() {
        return "LiveData: UserName: " + userName + ", MeetingId: " + meetingId + ", MeetingTpye: " + meetingType.getValue() + ", MemberRole: " + memberRole.getValue();
    }

    public List<String> getHandUpMemberList() {
        return handUpMemberList;
    }

    public void setHandUpMemberList(String idList) {
        if (idList == null) {
            return;
        }
        String[] list = idList.split("\\|");
        for (String s : list) {
            handUpMemberList.add(s);
        }
        if (userName != null && handUpMemberList.contains(userName)) {
            handUpMemberList.remove(userName);
        }
    }
}
