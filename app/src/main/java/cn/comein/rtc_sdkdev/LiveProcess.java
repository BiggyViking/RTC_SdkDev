package cn.comein.rtc_sdkdev;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/15.
 */

public class LiveProcess implements ComeInNativeMedia.OnSipStateListener {

    private static final String TAG = "LibWebrtcMedia_process";

    private ComeInNativeMedia comeInNativeMedia;
    private LiveData liveData;

    private Handler mWorkHandler;
    private HandlerThread mWorkThread;

    private OnFlushMediaStatusListener mFlushListener;

    public LiveProcess(Context context, SurfaceView sv_local, SurfaceView sv_remote) {
        comeInNativeMedia = new ComeInNativeMedia(this);
        liveData = new LiveData(context, sv_local, sv_remote);
        mWorkThread = new HandlerThread("sdkdev_process_thread");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    /**
     * 加入会议
     */
    public void joinLive() {
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkHandler.post(mJoinMeetingRunnable);
    }

    /**
     * 退出会议
     */
    public void quitLive() {
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkHandler.post(mQuitMeetingRunnable);
    }

    /**
     * 重新连接
     */
    public void reconnectLive() {
        mWorkHandler.removeCallbacksAndMessages(null);
        mWorkHandler.post(mQuitMeetingRunnable);
        mWorkHandler.postDelayed(mJoinMeetingRunnable, 1000);
    }

    /**
     * 主席上麦发言
     */
    public void chairStartSpeak() {
        mWorkHandler.removeCallbacks(mStartSpeakingRunnable);
        mWorkHandler.post(mStartSpeakingRunnable);
    }

    /**
     * 主席/成员下麦 结束发言
     */
    public void stopSpeak() {
        mWorkHandler.removeCallbacks(mStopSpeakingRunnable);
        mWorkHandler.post(mStopSpeakingRunnable);
    }

    /**
     * 成员举手 申请发言
     */
    public void memberHandUp() {
        mWorkHandler.removeCallbacks(mStartSpeakingRunnable);
        mWorkHandler.post(mStartSpeakingRunnable);
    }

    /**
     * 成员取消举手
     */
    public void memberCancelHandUp() {
        mWorkHandler.removeCallbacks(mMemberCancelHandUpRunnable);
        mWorkHandler.post(mMemberCancelHandUpRunnable);
    }

    /**
     * 主席允许举手的成员上麦发言
     */
    public void chairAllowMemberSpeak(String memberId) {
        if (liveData.memberRole == MemberRole.CHAIR) {
            liveData.selectedMemberID = memberId;
            mWorkHandler.removeCallbacks(mAdminAllowMemberSpeakRunnable);
            mWorkHandler.post(mAdminAllowMemberSpeakRunnable);
        }
    }

    /**
     * 主席将正在发言的成员踢下麦 结束其发言
     */
    public void chairStopMemberSpeak(String memberId) {
        if (liveData.memberRole == MemberRole.CHAIR) {
            liveData.selectedMemberID = memberId;
            mWorkHandler.removeCallbacks(mAdminStopMemberSpeakRunnable);
            mWorkHandler.post(mAdminStopMemberSpeakRunnable);
        }
    }

    /**
     * 切换前后摄像头
     */
    public void switchCamera() {
        mWorkHandler.removeCallbacks(mSwitchCameraRunnable);
        mWorkHandler.post(mSwitchCameraRunnable);
    }

    /**
     * 切换直播清晰度
     */
    public void switchMediaLevel() {
        mWorkHandler.removeCallbacks(mSwitchMediaLevelRunnable);
        mWorkHandler.post(mSwitchMediaLevelRunnable);
    }

    /**
     * 根据setting界面结果更新会议数据
     *
     * @param data
     * @return
     */
    public boolean updateBySetting(SettingData data) {
        Boolean changed = (liveData.memberRole != data.getMemberRole());
        liveData.userName = data.getUserName();
        liveData.passWord = data.getPassword();
        liveData.meetingId = data.getMeetingID();
        liveData.memberRole = data.getMemberRole();
        liveData.meetingType = data.getMeetingType();
        return changed;
    }

    /**
     * 检查setting界面的输入结果是否合法
     *
     * @return
     */
    public boolean checkOutSetting() {
        if ("".equals(liveData.userName) || "".equals(liveData.passWord) || "".equals(liveData.meetingId)) {
            return false;
        }
        if (!liveData.userName.equals(liveData.passWord)) {
            return false;
        }
        if (!liveData.meetingId.substring(0, 4).equals("conf")) {
            return false;
        }
        return true;
    }

    @Override
    public void onSipStateChanged(final int state, final String speakId) {
        if (state != MediaNativeStatus.NETWORK_STRONG)
            Log.d(TAG, "LiveProcess receive sip state: " + state + (speakId == null ? "" : ("speakerId: " + speakId)));
        switch (state) {
            case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER:
                liveData.setHandUpMemberList(speakId);
                flushMediaStatus(state, liveData.getHandUpMemberList());
                break;
            case MediaNativeStatus.SHOW_LIST_SPEAKING_MEMBER:
                liveData.setSpeakingMemberList(speakId);
                flushMediaStatus(state, liveData.getSpeakingMemberList());
                break;
            case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER_SINGLE:
                liveData.addToHandUpMemberList(speakId);
                flushMediaStatus(state, liveData.getHandUpMemberList());
                break;
            case MediaNativeStatus.SHOW_LIST_CANCEL_HAND_UP_MEMBER_SINGLE:
                liveData.removeFromHandUpMemberList(speakId);
                flushMediaStatus(state, liveData.getHandUpMemberList());
                break;
            default:
                flushMediaStatus(state, null);
                break;
        }
    }

    /**
     * 通知界面刷新UI
     *
     * @param state 状态码 {@link MediaNativeStatus}
     * @param list  举手/发言的成员ID列表
     */
    public void flushMediaStatus(int state, List<String> list) {
        if (mFlushListener != null) {
            mFlushListener.OnFlush(state, list);
        }
    }

    private Runnable mJoinMeetingRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.setContext(liveData.context);
            comeInNativeMedia.joinMeeting(liveData.userName, liveData.passWord, liveData.host, liveData.port, liveData.meetingId, liveData.memberRole, liveData.sv_local, liveData.sv_remote, liveData.meetingType);
        }
    };

    private Runnable mQuitMeetingRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.quitMeeting(liveData.meetingId);
        }
    };

    private Runnable mStartSpeakingRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.startSpeaking(liveData.memberRole, liveData.meetingType);
        }
    };

    private Runnable mStopSpeakingRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.stopSpeaking();
        }
    };

    private Runnable mMemberCancelHandUpRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.memberCancelHandUp();
        }
    };

    private Runnable mAdminAllowMemberSpeakRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.allowMemberSpeaking(liveData.selectedMemberID);
            liveData.removeFromHandUpMemberList(liveData.selectedMemberID);
        }
    };

    private Runnable mAdminStopMemberSpeakRunnable = new Runnable() {
        @Override
        public void run() {
            comeInNativeMedia.stopMemberSpeaking(liveData.selectedMemberID);
        }
    };

    private Runnable mSwitchCameraRunnable = new Runnable() {
        @Override
        public void run() {
            if (liveData.cameraType == CameraType.BACK) {
                liveData.cameraType = CameraType.FRONT;
            } else if (liveData.cameraType == CameraType.FRONT) {
                liveData.cameraType = CameraType.BACK;
            }
            comeInNativeMedia.switchCamera(liveData.cameraType);
        }
    };

    private Runnable mSwitchMediaLevelRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO
        }
    };

    public void setOnFlushMediaStatusListener(OnFlushMediaStatusListener listener) {
        mFlushListener = listener;
    }

    public interface OnFlushMediaStatusListener {
        public void OnFlush(int state, List<String> list);
    }
}
