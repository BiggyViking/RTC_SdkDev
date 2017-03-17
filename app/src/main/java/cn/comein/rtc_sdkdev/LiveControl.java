package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/15.
 */

public class LiveControl {
    public boolean bJoined;
    public boolean bSpeaking;
    public boolean bHandUp;
    public boolean bHasSomeoneHandUp;

    public LiveControl() {
        bJoined = false;
        bSpeaking = false;
        bHandUp = false;
        bHasSomeoneHandUp = false;
    }

    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("bJoined:" + bJoined + ", ");
        info.append("bSpeaking:" + bSpeaking + ", ");
        info.append("bHandUp:" + bHandUp + ", ");
        info.append("bHasSomeoneHandUp:" + bHasSomeoneHandUp);
        return info.toString();
    }
}
