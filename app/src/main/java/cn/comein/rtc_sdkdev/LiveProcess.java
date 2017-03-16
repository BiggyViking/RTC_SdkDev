package cn.comein.rtc_sdkdev;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2017/3/15.
 */

public class LiveProcess {

    private ComeInNativeMedia comeInNativeMedia;
    private LiveData liveData;

    private Handler mWorkHandler;
    private HandlerThread mWorkThread;

    public LiveProcess(Context context, SurfaceView sv_local, SurfaceView sv_remote) {
        comeInNativeMedia = new ComeInNativeMedia();
        liveData = new LiveData(context, sv_local, sv_remote);
        mWorkThread = new HandlerThread("sdk_dev_thread");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    /**
     * 切换身份：主席/成员
     */
    public void changeRole() {
        if (liveData.memberRole == MemberRole.CHAIR) {
            liveData.memberRole = MemberRole.NORMAL;
            liveData.userName = "10089";
            liveData.passWord = "10089";
        } else {
            liveData.memberRole = MemberRole.CHAIR;
            liveData.userName = "10087";
            liveData.passWord = "10087";
        }
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
    public void memberCancelHandUp(){
        // TODO 
    }

    /**
     * 主席允许举手的成员上麦发言
     */
    public void chairAllowMemberSpeak() {
        // TODO
    }

    /**
     * 主席将正在发言的成员踢下麦 结束其发言
     */
    public void chairStopMemberSpeak() {
        // TODO
    }

    /**
     * 切换前后摄像头
     */
    public void switchCamera() {
        // TODO
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
}
