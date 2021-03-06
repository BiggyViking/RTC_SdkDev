package cn.comein.rtc_sdkdev;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SettingData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;
    private String meetingID;
    private MemberRole memberRole;
    private MeetingType meetingType;
    private MediaLevel mediaLevel;

    public SettingData() {
        userName = "";
        password = "";
        meetingID = "";
        memberRole = MemberRole.CHAIR;
        meetingType = MeetingType.VIDEO;
        mediaLevel = MediaLevel.STANDARD;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }

    public MediaLevel getMediaLevel() {
        return mediaLevel;
    }

    public void setMediaLevel(MediaLevel mediaLevel) {
        this.mediaLevel = mediaLevel;
    }
}
