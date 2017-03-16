package cn.comein.rtc_sdkdev;

/**
 * Created by Administrator on 2017/3/10.
 */

public class MediaNativeStatus {

    public static final int JOIN_SUCCESS  = 200;              //进入会议成功
    public static final int JOIN_INIT_FAIL = 201;              //底层初始化失败
    public static final int JOIN_SERVER_ERROR = 204;        //服务器错误
    public static final int JOIN_REGISTER_FAIL = 205;       //注册服务器失败
    public static final int JOIN_REGISTER_TIMEOUT = 206;       //注册服务器超时
    public static final int JOIN_PASSWORD_ERROR = 207;       //密码错误
    public static final int JOIN_CALL_REFUSE = 208;       //呼叫服务器被拒绝
    public static final int JOIN_CALL_TIMEOUT = 209;       //呼叫服务器超时
    public static final int JOIN_UNKNOWN_ERROR = 210;       //加入未知错误

    public static final int QUIT_FAIL  = 301;             //退出会议失败
    public static final int QUIT_SUCCESS  = 300;          //退出会议成功

    public static final int START_SPEAKER_SUCCESS = 400;                     //用户发言成功
    public static final int START_SPEAKER_TIMEOUT = 401;                       //上麦超时
    public static final int START_SPEAKER_AUDIO_SERVER_ERROR  = 402;    //无法启动录音服务 提醒用户
    public static final int START_SPEAKER_VIDEO_SERVER_ERROR = 403;   //无法启动摄像服务 提醒用户
    public static final int START_SPEAKER_REFUSED  = 404;    //服务器拒绝 提醒用户
    public static final int START_SPEAKER_SERVER_ERROR = 405;     //服务器错误  提醒用户
    public static final int START_SPEAKER_UNKNOWN_ERROR  = 406;   // 未知错误  提醒用户

    public static final int NOT_JOIN_LIVE = 407;  // 没有加入会议

    public static final int STOP_SPEAK_SUCCESS  = 500;       //停止发言成功
    public static final int STOP_SPEAK_FAIL = 501;          //停止发言失败

    public static final int CONNECT_TIMEOUT  = 601;       //在进入会议后，超过某个时间没有收到包。


    public static final int MEETING_END = 20001;//直播结束
    public static final int MEETING_FULL = 20002;//直播人數已滿
    public static final int MEETING_MEMBER_BYE = 20003;//没有主讲人



    public static final int KICK_OUT_SPEAK = 30001;//被结束发言
    public static final int NETWORK_STRONG = 40000;//网络强
    public static final int NETWORK_POOR = 40001;//网络状态差

}
