package cn.comein.rtc_sdkdev;

import android.os.Handler;
import android.os.HandlerThread;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LibWebrtcMedia_sdkdev";

    private Button btn_ChangeRole;
    private Button btn_ChangeLiveMode;
    private Button btn_Join;
    private Button btn_StartStopSpeak;
    private Button btn_HandUp;
    private Button btn_PopMenu;
    private PopupMenu popupMenu;
    private Menu menu;
    private SurfaceView sv_local;
    private SurfaceView sv_remote;

    private LiveProcess liveProcess;
    private LiveControl liveControl;

    private MemberRole memberRole = MemberRole.CHAIR;

    private boolean bJoined;
    private boolean bSpeaking;
    private boolean bHandUp;
    private boolean bHasSomeoneHandUp;

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
        btn_Join = (Button) findViewById(R.id.btn_Join);
        btn_StartStopSpeak = (Button) findViewById(R.id.btn_StartStopSpeak);
        btn_HandUp = (Button) findViewById(R.id.btn_HandUp);
        btn_PopMenu = (Button) findViewById(R.id.btn_PopMenu);

        popupMenu = new PopupMenu(this, findViewById(R.id.btn_PopMenu));
        menu = popupMenu.getMenu();

        sv_local = (SurfaceView) findViewById(R.id.sv_local);
        sv_remote = (SurfaceView) findViewById(R.id.sv_remote);

        btn_ChangeRole.setOnClickListener(this);
        btn_ChangeLiveMode.setOnClickListener(this);
        btn_Join.setOnClickListener(this);
        btn_StartStopSpeak.setOnClickListener(this);
        btn_HandUp.setOnClickListener(this);
        btn_PopMenu.setOnClickListener(this);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        btn_ChangeRole.setText("身份切换：主席");
        btn_Join.setText("加入会议");
        btn_StartStopSpeak.setText("上麦");
        if (memberRole == MemberRole.CHAIR) {
            btn_HandUp.setText("无人举手");
        } else {
            btn_HandUp.setText("举手");
        }
        btn_PopMenu.setText("举手列表");
    }

    private void initData() {
        liveProcess = new LiveProcess(getApplicationContext(), sv_local, sv_remote);
        liveControl = new LiveControl();

        bJoined = false;
        bSpeaking = false;
        bHandUp = false;
        bHasSomeoneHandUp = false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ChangeRole:
                if (bJoined) {
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
            case R.id.btn_Join:
                if (bJoined) {
                    liveProcess.quitLive();
                    bJoined = false;
                    btn_Join.setText("加入会议");
                } else {
                    liveProcess.joinLive();
                    bJoined = true;
                    btn_Join.setText("退出会议");
                }
                break;
            case R.id.btn_StartStopSpeak:
                if (bSpeaking) {
                    liveProcess.stopSpeak();
                    bSpeaking = false;
                    btn_StartStopSpeak.setText("上麦");
                } else {
                    if (memberRole == MemberRole.CHAIR) {
                        liveProcess.chairStartSpeak();
                        bSpeaking = true;
                        btn_StartStopSpeak.setText("下麦");
                    } else {
                        String info = "You are not allowed to StartSpeak directly, please hand up";
                        Log.d(TAG, info);
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.btn_HandUp:
                if (memberRole == MemberRole.CHAIR) {
                    //TODO
                } else if (memberRole == MemberRole.NORMAL) {
                    if (bSpeaking) {
                        String info = "You already StartSpeak";
                        Log.d(TAG, info);
                        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_LONG).show();
                    } else {
                        if (bHandUp) {
                            //TODO
                        } else if (!bHandUp) {
                            liveProcess.memberHandUp();
                            bHandUp = true;
                            btn_HandUp.setText("取消举手");
                        }
                    }
                }
                break;
            case R.id.btn_PopMenu:
                // TODO
                break;
            default:
                break;
        }
    }

    public void setPopupMenu() {

    }

    public void showPopupMenu() {
        popupMenu.show();
    }

}
