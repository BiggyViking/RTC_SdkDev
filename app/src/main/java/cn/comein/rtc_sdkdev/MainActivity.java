package cn.comein.rtc_sdkdev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_Setting;
    private Button btn_SwitchCamera;
    private Button btn_Join;
    private Button btn_StartStopSpeak;
    private Button btn_PopHandUpMenu;
    private Button btn_PopSpeakingMenu;
    private PopupMenu popupMenu_HandUp;
    private PopupMenu popupMenu_Speaking;
    private Menu menu_HandUp;
    private Menu menu_Speaking;
    private SurfaceView sv_local;
    private SurfaceView sv_remote;

    private LiveProcess liveProcess;
    private LiveControl liveControl;
    private SettingData settingData;
    private ShowInfoUtil showInfoUtil;

    private Handler mWorkHandler;
    private SharedPreferences sharedPreferences;

    private MemberRole memberRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            String shared = SerializeUtil.writeObject(settingData);
            editor.putString("sharedObj", shared);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void initView() {
        btn_Setting = (Button) findViewById(R.id.btn_setting);
        btn_SwitchCamera = (Button) findViewById(R.id.btn_switch_camera);
        btn_Join = (Button) findViewById(R.id.btn_join);
        btn_StartStopSpeak = (Button) findViewById(R.id.btn_start_stop_speak);
        btn_PopHandUpMenu = (Button) findViewById(R.id.btn_pop_hand_up_menu);
        btn_PopSpeakingMenu = (Button) findViewById(R.id.btn_pop_speaking_menu);

        popupMenu_HandUp = new PopupMenu(this, findViewById(R.id.btn_pop_hand_up_menu));
        menu_HandUp = popupMenu_HandUp.getMenu();
        popupMenu_Speaking = new PopupMenu(this, findViewById(R.id.btn_pop_speaking_menu));
        menu_Speaking = popupMenu_Speaking.getMenu();

        sv_local = (SurfaceView) findViewById(R.id.sv_local);
        sv_remote = (SurfaceView) findViewById(R.id.sv_remote);

        btn_Setting.setOnClickListener(this);
        btn_SwitchCamera.setOnClickListener(this);
        btn_Join.setOnClickListener(this);
        btn_StartStopSpeak.setOnClickListener(this);
        btn_PopHandUpMenu.setOnClickListener(this);
        btn_PopSpeakingMenu.setOnClickListener(this);

        popupMenu_HandUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String memberId = item.getTitle().toString();
                liveProcess.chairAllowMemberSpeak(memberId);
                updateHandUpMenu(item.getItemId());
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

        btn_Setting.setText("设置");
        btn_SwitchCamera.setText("切换摄像头");
        btn_Join.setText("加入会议");
        btn_StartStopSpeak.setText("上麦");
        btn_PopSpeakingMenu.setText("发言列表");
    }

    private void initData() {
        mWorkHandler = new Handler();

        liveProcess = new LiveProcess(getApplicationContext(), sv_local, sv_remote);
        liveControl = new LiveControl();
        settingData = new SettingData();
        showInfoUtil = new ShowInfoUtil(getApplicationContext());

        liveProcess.setOnFlushMediaStatusListener(new LiveProcess.OnFlushMediaStatusListener() {
            @Override
            public void OnFlush(int state, List<String> list) {
                FlushMediaStatusRunnable runnable = new FlushMediaStatusRunnable(state, list);
                mWorkHandler.post(runnable);
            }
        });

        sharedPreferences = getSharedPreferences("sharedSettingData", 0);
        String shared = sharedPreferences.getString("sharedObj", "");
        try {
            settingData = (SettingData) SerializeUtil.readObject(shared);
            liveProcess.updateBySetting(settingData);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        memberRole = settingData.getMemberRole();
        if (memberRole == MemberRole.CHAIR) {
            btn_PopHandUpMenu.setText("无人举手");
        } else {
            btn_PopHandUpMenu.setText("举手");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
    /* 设置 */
            case R.id.btn_setting:
                if (liveControl.bJoined) {
                    showInfoUtil.show("You already join in meeting, can not enter setting");
                } else {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    intent.putExtra("SettingData", settingData);
                    startActivityForResult(intent, 1);
                }
                break;
    /* 切换前后摄像头 */
            case R.id.btn_switch_camera:
                liveProcess.switchCamera();
                break;
    /* 加入/退出会议 */
            case R.id.btn_join:
                if (!liveProcess.checkOutSetting()) {
                    showInfoUtil.show("Please input correct meeting information through setting");
                    break;
                }
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
    /* 上麦/下麦 */
            case R.id.btn_start_stop_speak:
                if (!liveControl.bJoined) {
                    showInfoUtil.show("Please join in the meeting first !");
                    break;
                }
                if (!checkPermission(this, android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)) {
                    break;
                }
                if (liveControl.bSpeaking) {
                    liveProcess.stopSpeak();
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                } else {
                    if (memberRole == MemberRole.CHAIR) {
                        liveProcess.chairStartSpeak();
                    } else {
                        showInfoUtil.show("You are not allowed to StartSpeak directly, please hand up");
                    }
                }
                break;
    /* 举手(成员)/ 举手列表(主席) */
            case R.id.btn_pop_hand_up_menu:
                if (memberRole == MemberRole.CHAIR) {
                    if (liveControl.bHasSomeoneHandUp) {
                        showHandUpMenu();
                    }
                } else if (memberRole == MemberRole.NORMAL) {
                    if (!liveControl.bJoined) {
                        showInfoUtil.show("You have not joined the meeting");
                    } else if (liveControl.bSpeaking) {
                        showInfoUtil.show("You already StartSpeak");
                    } else {
                        if (liveControl.bHandUp) {
                            liveProcess.memberCancelHandUp();
                            liveControl.bHandUp = false;
                            btn_PopHandUpMenu.setText("举手");
                        } else if (!liveControl.bHandUp) {
                            liveProcess.memberHandUp();
                            liveControl.bHandUp = true;
                            btn_PopHandUpMenu.setText("取消举手");
                        }
                    }
                }
                break;
    /* 发言列表 */
            case R.id.btn_pop_speaking_menu:
                showSpeakingMenu();
                break;
            default:
                break;
        }
    }

    public void showHandUpMenu() {
        popupMenu_HandUp.show();
    }

    public void updateHandUpMenu(int id) {
        menu_HandUp.removeItem(id);
        if (menu_HandUp.size() == 0) {
            btn_PopHandUpMenu.setText("无人举手");
            liveControl.bHasSomeoneHandUp = false;
        }
    }

    public void showSpeakingMenu() {
        popupMenu_Speaking.show();
    }

    public boolean checkPermission(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, 0);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        settingData = (SettingData) data.getSerializableExtra("SettingDataCallback");
        if (liveProcess.updateBySetting(settingData)) {
            if (memberRole == MemberRole.NORMAL) {
                btn_PopHandUpMenu.setText("无人举手");
                memberRole = MemberRole.CHAIR;
            } else {
                btn_PopHandUpMenu.setText("举手");
                memberRole = MemberRole.NORMAL;
            }
        }
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
                case MediaNativeStatus.JOIN_SUCCESS:    //200 加入会议成功
                    liveControl.bJoined = true;
                    btn_Join.setText("退出会议");
                    break;
                case MediaNativeStatus.START_SPEAKER_SUCCESS:   //400 上麦成功
                    liveControl.bSpeaking = true;
                    btn_StartStopSpeak.setText("下麦");
                    if (liveControl.bHandUp == true) {
                        liveControl.bHandUp = false;
                        btn_PopHandUpMenu.setText("举手");
                    }
                    break;
                case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER:
                case MediaNativeStatus.SHOW_LIST_HAND_UP_MEMBER_SINGLE:
                case MediaNativeStatus.SHOW_LIST_CANCEL_HAND_UP_MEMBER_SINGLE:
                    menu_HandUp.clear();
                    if (!list.isEmpty()) {
                        liveControl.bHasSomeoneHandUp = true;
                        btn_PopHandUpMenu.setText("举手列表");
                        for (String s : list) {
                            menu_HandUp.add(s);
                        }
                    } else {
                        liveControl.bHasSomeoneHandUp = false;
                        btn_PopHandUpMenu.setText("无人举手");
                    }
                    break;
                case MediaNativeStatus.SHOW_LIST_SPEAKING_MEMBER:
                    menu_Speaking.clear();
                    for (String s : list) {
                        menu_Speaking.add(s);
                    }
                    break;
                case MediaNativeStatus.KICK_OUT_SPEAK:  //30001 被结束发言
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                    break;
                        /* 201~210 加入会议失败 */
                case MediaNativeStatus.JOIN_INIT_FAIL:
                case MediaNativeStatus.JOIN_SERVER_ERROR:
                case MediaNativeStatus.JOIN_REGISTER_FAIL:
                case MediaNativeStatus.JOIN_REGISTER_TIMEOUT:
                case MediaNativeStatus.JOIN_PASSWORD_ERROR:
                case MediaNativeStatus.JOIN_CALL_REFUSE:
                case MediaNativeStatus.JOIN_CALL_TIMEOUT:
                case MediaNativeStatus.JOIN_UNKNOWN_ERROR:
                    showInfoUtil.show("failed to join the meeting !");
                    liveProcess.quitLive();
                    break;
                        /* 401~406 上麦发言失败 */
                case MediaNativeStatus.START_SPEAKER_TIMEOUT:
                case MediaNativeStatus.START_SPEAKER_AUDIO_SERVER_ERROR:
                case MediaNativeStatus.START_SPEAKER_VIDEO_SERVER_ERROR:
                case MediaNativeStatus.START_SPEAKER_REFUSED:
                case MediaNativeStatus.START_SPEAKER_SERVER_ERROR:
                case MediaNativeStatus.START_SPEAKER_UNKNOWN_ERROR:
                    showInfoUtil.show("failed to start speaking !");
                    break;
                case MediaNativeStatus.MEETING_END:
                case MediaNativeStatus.MEETING_FULL:
                case MediaNativeStatus.MEETING_MEMBER_BYE:
                    showInfoUtil.show("Meeting end ! you are about to quit");
                    liveProcess.quitLive();
                    btn_Join.setText("加入会议");
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                    break;
                case MediaNativeStatus.CONNECT_TIMEOUT:
                    showInfoUtil.show("connection timeout !");
                    liveProcess.reconnectLive();
                    liveControl.bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                    break;
                default:
                    break;
            }
        }
    }

}
