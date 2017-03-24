package cn.comein.rtc_sdkdev;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2017/3/10.
 */

public class ComeInNativeMedia extends Activity {

    private static final String TAG = "LibWebrtcMedia_sdkdev";

    static {
        System.loadLibrary("webrtcMedia");
    }

    private OnSipStateListener mOnSipStateListener;

    protected ComeInNativeMedia(OnSipStateListener onSipStateListener) {
        mOnSipStateListener = onSipStateListener;
    }

    public synchronized void destroy() {
        mOnSipStateListener = null;
    }

    /**
     * 初始化环境
     *
     * @param context 上下文
     * @return 0 成功  其他失败
     */
    protected int setContext(Context context) {
        return kRTC_SetContext(context, ComeInNativeMedia.this);
    }

    /**
     * 加入演播厅
     *
     * @param name        用户名
     * @param password    密码
     * @param host        host
     * @param port        port
     * @param meetingId   房间号
     * @param memberRole  成员角色
     * @param localView   本地视图    音频为null  (暂时不用 为null)
     * @param remoteView  远程视图    音频为null
     * @param meetingType 会议类型
     * @return 0 成功  其他失败
     */
    protected int joinMeeting(String name, String password, String host, String port
            , String meetingId, MemberRole memberRole
            , SurfaceView localView, SurfaceView remoteView, MeetingType meetingType) {
        String log = "joinMeeting name = " + name + " password = " + password +
                " host = " + host + " port = " + port +
                " meetingId = " + meetingId + " meetingType = " + meetingType.getValue() +
                " memberRole = " + memberRole.getValue() +
                " localView = " + localView + " remoteView = " + remoteView + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        long start = System.currentTimeMillis();
        int result = kRTC_JoinMeeting(name, password, host, port, meetingId
                , memberRole.getValue(), localView, remoteView, meetingType.getValue());
        log = "joinMeeting " + result + " need time = " + (System.currentTimeMillis() - start);
        Log.d(TAG, log);
        return result;
    }

    /**
     * 退出演播厅
     *
     * @param meetingId 房间号
     * @return 0 成功  其他失败
     */
    protected int quitMeeting(String meetingId) {
        Log.d(TAG, "quitMeeting");
        long start = System.currentTimeMillis();
        int result = kRTC_QuitMeeting(meetingId);
        String log = "quitMeeting " + result + " need time = "
                + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 上麦发言申请
     * 如果是chair身份
     *
     * @param memberRole  成员角色
     * @param meetingType 路演厅类型
     * @return 0 成功  其他失败
     */
    protected int startSpeaking(MemberRole memberRole, MeetingType meetingType) {
        Log.d(TAG, "startSpeaking");
        long start = System.currentTimeMillis();
        int result = kRTC_StartSpeaking(memberRole.getValue(), meetingType.getValue());
        String log = "startSpeaking result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 结束发言
     *
     * @return 0 成功  其他失败
     */
    protected int stopSpeaking() {
        Log.d(TAG, "stopSpeaking");
        long start = System.currentTimeMillis();
        int result = kRTC_StopSpeaking();
        String log = "stopSpeaking result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 成员取消举手
     *
     * @return 0 成功 其他失败
     */
    protected int memberCancelHandUp() {
        Log.d(TAG, "memberCancelHandUp");
        long start = System.currentTimeMillis();
        int result = kRTC_MemberCancelHandUp();
        String log = "memberCancelHandUp result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 转换摄像头
     *
     * @param cameraType 摄像头类型
     * @return 0 成功  其他失败
     */
    protected int switchCamera(CameraType cameraType) {
        Log.d(TAG, "switchCamera");
        long start = System.currentTimeMillis();
        int result = kRTC_SwitchCamera(cameraType.getValue());
        String log = "switchCamera result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 允许举手成员发言
     *
     * @param memberId 成员用户id
     * @return 是否成功 0成功 其他失败
     */
    protected int allowMemberSpeaking(String memberId) {
        Log.d(TAG, "allowMemberSpeaking");
        long start = System.currentTimeMillis();
        int result = kRTC_AdminAllowMemberSpeaking(memberId);
        String log = "allowMemberSpeaking result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 主席停止成员发言
     *
     * @param memberId 成员用户id
     * @return 是否成功 0成功 其他失败
     */
    protected int stopMemberSpeaking(String memberId) {
        Log.d(TAG, "stopMemberSpeaking");
        long start = System.currentTimeMillis();
        int result = kRTC_AdminStopMemberSpeaking(memberId);
        String log = "stopMemberSpeaking result = " + result +
                " needTime = " + (System.currentTimeMillis() - start) + " thread = " + Thread.currentThread().getId();
        Log.d(TAG, log);
        return result;
    }

    /**
     * 状态回调方法 底层调用
     *
     * @param state 状态码 {@link MediaNativeStatus}
     */
    public synchronized void onSipStateChanged(final int state) {
        if (mOnSipStateListener != null) {
            mOnSipStateListener.onSipStateChanged(state, null);
        }
    }

    /**
     * 上报日志
     *
     * @param log
     */
    public void onLogReport(final String log) {

    }

    /**
     * jni上报 举手/发言 的成员ID列表
     *
     * @param state      状态码 {@link MediaNativeStatus}
     * @param speakerIDs
     */
    public void onSipSpeakerId(int state, String speakerIDs) {
        if (mOnSipStateListener != null) {
            mOnSipStateListener.onSipStateChanged(state, speakerIDs);
        }
    }

    private native int kRTC_SetContext(Object context, Object object);

    private native int kRTC_JoinMeeting(String name, String password, String host,
                                        String port, String meetingId,
                                        int isChair, Object localView,
                                        Object remoteView, int isVideo);

    private native int kRTC_QuitMeeting(String meetingId);

    private native int kRTC_StartSpeaking(int isChair, int isVideo);

    private native int kRTC_StopSpeaking();

    private native int kRTC_MemberCancelHandUp();

    private native int kRTC_SwitchCamera(int isFront);

    private native int kRTC_AdminAllowMemberSpeaking(String memberId);

    private native int kRTC_AdminStopMemberSpeaking(String memberId);

    public interface OnSipStateListener {
        void onSipStateChanged(int state, String speakId);
    }

}
