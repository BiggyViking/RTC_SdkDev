package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/4/27.
 */

public enum MediaLevel {

    STANDARD(1),    //标清
    HIGH(2),        //高清
    EXTEND(3);      //待扩展

    private int value;

    MediaLevel(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }
}
