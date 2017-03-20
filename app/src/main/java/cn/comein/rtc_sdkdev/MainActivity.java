package cn.comein.rtc_sdkdev;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LibWebrtcMedia_sdkdev";

    private Button btn_ChangeRole;
    private Button btn_ChangeLiveMode;
    private Button btn_SwitchCamera;
    private Button btn_Join;
    private Button btn_StartStopSpeak;
    private Button btn_HandUp;
    private Button btn_PopMenu;
    private PopupMenu popupMenu_HandUp;
    private PopupMenu popupMenu_Speaking;
    private Menu menu_HandUp;
    private Menu menu_Speaking;
    private SurfaceView sv_local;
    private SurfaceView sv_remote;

    private LiveProcess liveProcess;
    private LiveControl liveControl;

    private Handler mWorkHandler;

    private MemberRole memberRole = MemberRole.CHAIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        btn_ChangeRole = (Button) findViewById(R.id.btn_ChangeRole);
        btn_ChangeLiveMode = (Button) findViewById(R.id.btn_ChangeLiveMode);
        btn_SwitchCamera = (Button) findViewById(R.id.btn_SwitchCamera);
        btn_Join = (Button) findViewById(R.id.btn_Join);
        btn_StartStopSpeak = (Button) findViewById(R.id.btn_StartStopSpeak);
        btn_HandUp = (Button) findViewById(R.id.btn_HandUp);
        btn_PopMenu = (Button) findViewById(R.id.btn_PopMenu);

        popupMenu_HandUp = new PopupMenu(this, findViewById(R.id.btn_HandUp));
        menu_HandUp = popupMenu_HandUp.getMenu();
        popupMenu_Speaking = new PopupMenu(this, findViewById(R.id.btn_PopMenu));
        menu_Speaking = popupMenu_Speaking.getMenu();

        sv_local = (SurfaceView) findViewById(R.id.sv_local);
        sv_remote = (SurfaceView) findViewById(R.id.sv_remote);

        btn_ChangeRole.setOnClickListener(this);
        btn_ChangeLiveMode.setOnClickListener(this);
        btn_SwitchCamera.setOnClickListener(this);
        btn_Join.setOnClickListener(this);
        btn_StartStopSpeak.setOnClickListener(this);
        btn_HandUp.setOnClickListener(this);
        btn_PopMenu.setOnClickListener(this);

        popupMenu_HandUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String memberId = item.getTitle().toString();
                liveProcess.chairAllowMemberSpeak(memberId);
                return true;
            }
        });
        popupMenu_Speaking.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String memberId = item.getTitle().toString();
                liveProcess.chairStopMemberSpeak(memberId);
                return true;
            }
        });

        btn_ChangeRole.setText("身份切换：主席");
        btn_SwitchCamera.setText("切换摄像头");
        btn_Join.setText("加入会议");
        btn_StartStopSpeak.setText("上麦");
        if (memberRole == MemberRole.CHAIR) {
            btn_HandUp.setText("无人举手");
        } else {
            btn_HandUp.setText("举手");
        }
        btn_PopMenu.setText("发言列表");
    }

    private void initData() {
        mWorkHandler = new Handler();

        liveProcess = new LiveProcess(getApplicationContext(), sv_local, sv_remote);
        liveControl = new LiveControl();

        liveProcess.setOnFlushMediaStatusListener(new LiveProcess.OnFlushMediaStatusListener() {
            @Override
            public void OnFlush(int state, List<String> list) {
                FlushMediaStatusRunnable runnable = new FlushMediaStatusRunnable(state, list);
                mWorkHandler.post(runnable);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ChangeRole:
                if (liveControl.bJoined) {
                    String info = "You already join in meeting, can not change your role";
                    Log.d(TAG, info);
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                } else {
                    liveProcess.changeRole();
                    if (memberRole == MemberRole.NORMAL) {
                        btn_ChangeRole.setText("身份切换：主席");
                        btn_HandUp.setText("无人举手");
                        memberRole = MemberRole.CHAIR;
                    } else {
                        btn_ChangeRole.setText("身份切换：成员");
                        btn_HandUp.setText("举手");
                        memberRole = MemberRole.NORMAL;
                    }
                }
                break;
            case R.id.btn_ChangeLiveMode:
                // TODO
                break;
            case R.id.btn_SwitchCamera:
                liveProcess.switchCamera();
                break;
            case R.id.btn_Join:
                if (liveControl.bJoined) {
                    liveProcess.quitLive();
                    liveControl.bJoined = false;
                    btn_Join.setText("加入会议");
                    if (liveControl.bSpeaking) {
                        liveControl.bSpeaking = false;
                        btn_StartStopSpeak.setText("上麦");
                    }
                } else {
                    liveProcess.joinLive();
                }
                break;
            case R.id.btn_StartStopSpeak:
                if (liveControl.bSpeaking) {
                    liveProcess.stopSpeak();
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                } else {
                    if (memberRole == MemberRole.CHAIR) {
                        liveProcess.chairStartSpeak();
                    } else {
                        String info = "You are not allowed to StartSpeak directly, please hand up";
                        Log.d(TAG, info);
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.btn_HandUp:
                if (memberRole == MemberRole.CHAIR) {
                    if (liveControl.bHasSomeoneHandUp) {
                        showHandUpMenu();
                    }
                } else if (memberRole == MemberRole.NORMAL) {
                    if (liveControl.bSpeaking) {
                        String info = "You already StartSpeak";
                        Log.d(TAG, info);
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                    } else {
                        if (liveControl.bHandUp) {
                            liveProcess.memberCancelHandUp();
                            liveControl.bHandUp = false;
                            btn_HandUp.setText("举手");
                        } else if (!liveControl.bHandUp) {
                            liveProcess.memberHandUp();
                            liveControl.bHandUp = true;
                            btn_HandUp.setText("取消举手");
                        }
                    }
                }
                break;
            case R.id.btn_PopMenu:
                showSpeakingMenu();
                break;
            default:
                break;
        }
    }

    public void showHandUpMenu() {
        popupMenu_HandUp.show();
    }

    public void showSpeakingMenu() {
        popupMenu_Speaking.show();
    }

    class FlushMediaStatusRunnable implements Runnable {
        private int state;
        private List<String> list;

        public FlushMediaStatusRunnable(int state, List<String> list) {
            this.state = state;
            this.list = list;
        }

        @Override
        public void run() {
            switch (state) {
                case MediaNativeStatus.JOIN_SUCCESS:
                    liveControl.bJoined = true;
                    btn_Join.setText("退出会议");
                    break;
                case MediaNativeStatus.START_SPEAKER_SUCCESS:
                    liveControl.bSpeaking = true;
                    btn_StartStopSpeak.setText("下麦");
                    if (liveControl.bHandUp == true) {
                        liveControl.bHandUp = false;
                        btn_HandUp.setText("举手");
                    }
                    break;
                case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER:
                case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER_SINGLE:
                case MediaNativeStatus.SHOW_LIST_CANCEL_HAND_UP_MEMBER_SINGLE:
                    menu_HandUp.clear();
                    if (!list.isEmpty()) {
                        liveControl.bHasSomeoneHandUp = true;
                        btn_HandUp.setText("有人举手");
                        for (String s : list) {
                            menu_HandUp.add(s);
                        }
                    } else {
                        liveControl.bHasSomeoneHandUp = false;
                        btn_HandUp.setText("无人举手");
                    }
                    break;
                case MediaNativeStatus.SHOW_LIST_SPEAKING_MEMBER:
                    menu_Speaking.clear();
                    for (String s : list) {
                        menu_Speaking.add(s);
                    }
                    break;
                case MediaNativeStatus.KICK_OUT_SPEAK:
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                    break;
                default:
                    break;
            }
        }
    }

}
