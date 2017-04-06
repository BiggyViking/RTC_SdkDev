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
    public CameraType cameraType;
    public String host;
    public String port;
    public Context context;
    public SurfaceView sv_local;
    public SurfaceView sv_remote;
    public String selectedMemberID;

    private List<String> handUpMemberList;
    private List<String> speakingMemberList;

    public LiveData(Context ct, SurfaceView local, SurfaceView remote) {
        userName = "";
        passWord = "";
        meetingId = "";
        meetingType = MeetingType.VIDEO;
        memberRole = MemberRole.CHAIR;
        cameraType = CameraType.BACK;
//        host = "112.124.122.172";   //无sip加密的
        host = "112.124.125.66";    //带sip加密的
        port = "12568";
        context = ct;
        sv_local = local;
        sv_remote = remote;
        handUpMemberList = new ArrayList<>();
        handUpMemberList.clear();
        speakingMemberList = new ArrayList<>();
        speakingMemberList.clear();
        selectedMemberID = null;
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
        cameraType = CameraType.BACK;
        handUpMemberList = new ArrayList<>();
        handUpMemberList.clear();
        speakingMemberList = new ArrayList<>();
        speakingMemberList.clear();
        selectedMemberID = null;
    }

    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("userName:" + userName + ", ");
        info.append("meetingId:" + meetingId + ", ");
        info.append("meetingType:" + meetingType + ", ");
        info.append("memberRole:" + memberRole);
        return info.toString();
    }

    public List<String> getHandUpMemberList() {
        return handUpMemberList;
    }

    public void setHandUpMemberList(String idList) {
        if (idList == null) {
            return;
        }
        handUpMemberList.clear();
        String[] list = idList.split("\\|");
        for (String s : list) {
            handUpMemberList.add(s);
        }
        if (userName != null && handUpMemberList.contains(userName)) {
            handUpMemberList.remove(userName);
        }
    }

    public void addToHandUpMemberList(String id) {
        if (!handUpMemberList.contains(id))
            handUpMemberList.add(id);
    }

    public void removeFromHandUpMemberList(String id) {
        if (handUpMemberList.contains(id))
            handUpMemberList.remove(id);
    }

    public List<String> getSpeakingMemberList() {
        return speakingMemberList;
    }

    public void setSpeakingMemberList(String idList) {
        if (idList == null) {
            return;
        }
        speakingMemberList.clear();
        String[] list = idList.split("\\|");
        for (String s : list) {
            speakingMemberList.add(s);
        }
        if (memberRole == MemberRole.CHAIR && userName != null && speakingMemberList.contains(userName)) {
            speakingMemberList.remove(userName);
        }
    }
}
