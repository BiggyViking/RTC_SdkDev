package cn.comein.rtc_sdkdev;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SettingData implements Serializable {

    private String userName;
    private String password;
    private MemberRole memberRole;
    private MeetingType meetingType;

    public SettingData() {
        userName = null;
        password = null;
        memberRole = MemberRole.CHAIR;
        meetingType = MeetingType.VIDEO;
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
}
