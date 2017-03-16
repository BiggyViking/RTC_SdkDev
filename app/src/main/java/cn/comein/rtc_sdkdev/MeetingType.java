package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/10.
 */

public enum MeetingType {

    AUDIO(0),       // 音频
    VIDEO(1),       // 视频
    UNKNOWN(2);

    private int value;

    MeetingType(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

}
