package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/10.
 */

public enum MemberRole {

    NORMAL(0),     //  普通成员
    CHAIR(1),      //  管理员或嘉宾
    UNKNOWN(2);

    private int value;

    MemberRole(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

}
