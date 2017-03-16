package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/10.
 */

public enum CameraType {

    BACK(0),      //后置
    FRONT(1),     //前置
    UNKNOWN(2);

    private int value;

    CameraType(int val) {
        value = val;
    }

    public int getValue() {
        return value;
    }

}