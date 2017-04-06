package cn.comein.rtc_sdkdev;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by Administrator on 2017/3/23.
 */

public class SettingActivity extends AppCompatActivity {

    private EditText et_UserName;
    private EditText et_Password;
    private EditText et_MeetingID;
    private RadioGroup rg_SwitchRole;
    private RadioGroup rg_SwitchMode;
    private RadioButton rb_RoleChair;
    private RadioButton rb_RoleNormal;
    private RadioButton rb_ModeVideo;
    private RadioButton rb_ModeAudio;

    private SettingData settingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        settingData.setUserName(et_UserName.getText().toString());
        settingData.setPassword(et_Password.getText().toString());
        settingData.setMeetingID(et_MeetingID.getText().toString());

        Intent data = new Intent();
        data.putExtra("SettingDataCallback", settingData);
        setResult(1, data);

        super.onBackPressed();
    }

    private void initData() {
        Intent intent = getIntent();
        settingData = (SettingData) intent.getSerializableExtra("SettingData");
    }

    private void initView() {
        et_UserName = (EditText) findViewById(R.id.et_user_name);
        et_Password = (EditText) findViewById(R.id.et_password);
        et_MeetingID = (EditText) findViewById(R.id.et_meeting_id);

        rg_SwitchRole = (RadioGroup) findViewById(R.id.rg_switch_role);
        rg_SwitchMode = (RadioGroup) findViewById(R.id.rg_switch_mode);
        rb_RoleChair = (RadioButton) findViewById(R.id.rb_role_chair);
        rb_RoleNormal = (RadioButton) findViewById(R.id.rb_role_normal);
        rb_ModeVideo = (RadioButton) findViewById(R.id.rb_mode_video);
        rb_ModeAudio = (RadioButton) findViewById(R.id.rb_mode_audio);

        if (settingData.getUserName() == null || "".equals(settingData.getUserName())) {
            et_UserName.setHint("10086");
        } else {
            et_UserName.setText(settingData.getUserName());
        }
        if (settingData.getPassword() == null || "".equals(settingData.getPassword())) {
            et_Password.setHint("10086");
        } else {
            et_Password.setText(settingData.getPassword());
        }
        if (settingData.getMeetingID() == null || "".equals(settingData.getMeetingID())) {
            et_MeetingID.setHint("conf3001");
        } else {
            et_MeetingID.setText(settingData.getMeetingID());
        }

        switch (settingData.getMemberRole()) {
            case CHAIR:
                rb_RoleChair.setChecked(true);
                break;
            case NORMAL:
                rb_RoleNormal.setChecked(true);
                break;
            default:
                break;
        }

        switch (settingData.getMeetingType()) {
            case VIDEO:
                rb_ModeVideo.setChecked(true);
                break;
            case AUDIO:
                rb_ModeAudio.setChecked(true);
                break;
            default:
                break;
        }

        RadioGroupListener listener = new RadioGroupListener();
        rg_SwitchRole.setOnCheckedChangeListener(listener);
        rg_SwitchMode.setOnCheckedChangeListener(listener);
    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.rb_role_chair:
                    settingData.setMemberRole(MemberRole.CHAIR);
                    break;
                case R.id.rb_role_normal:
                    settingData.setMemberRole(MemberRole.NORMAL);
                    break;
                case R.id.rb_mode_video:
                    settingData.setMeetingType(MeetingType.VIDEO);
                    break;
                case R.id.rb_mode_audio:
                    settingData.setMeetingType(MeetingType.AUDIO);
                    break;
                default:
                    break;
            }
        }
    }

}
