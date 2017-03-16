package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/15.
 */

public class LiveControl {
    public boolean bJoined;
    public boolean bSpeaking;
    public boolean bHandUp;
    public boolean bHasSomeoneHandUp;

    public LiveControl(){
        bJoined = false;
        bSpeaking = false;
        bHandUp = false;
        bHasSomeoneHandUp = false;
    }
}
