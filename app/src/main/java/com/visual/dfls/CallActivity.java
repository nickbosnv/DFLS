package com.visual.dfls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.dfls.R;

import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;

import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_flag;

import dfls.sipservice.LSCall;
import dfls.sipservice.LSService;


public class CallActivity extends Activity implements View.OnClickListener,
        View.OnLongClickListener {

    private boolean hold = false;
    private boolean speaker;
    private AudioManager audioManager;

    public static Activity activity = null;

    public TextView status;
    private EditText lsPhoneNumberField;
    private Button lsOneButton;
    private Button lsTwoButton;
    private Button lsThreeButton;
    private Button lsFourButton;
    private Button lsFiveButton;
    private Button lsSixButton;
    private Button lsSevenButton;
    private Button lsEightButton;
    private Button lsNineButton;
    private Button lsZeroButton;
    private Button lsStarButton;
    private Button lsPoundButton;
    private Button lsDialButton;
    private Button lsDeleteButton;
    private Button lsPauseButton;
    private Button lsForwardButton;
    private Button lsCloseButton;
    private Button lsSpeakerButton;

    private static final int DURATION = 50;

    private Vibrator lsVibrator;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialpad);

        lsVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        initUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initUI() {
        initializeViews();
        addNumberFormatting();
        setClickListeners();
        unlockWhenRing();
        setSpeakerFalse();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void unlockWhenRing(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if(keyguardManager!=null)
                keyguardManager.requestDismissKeyguard(this, null);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                    WindowManager.LayoutParams.FLAG_SPLIT_TOUCH|
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
    }

    private void setSpeakerFalse(){
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
        speaker = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initializeViews() {
        lsPhoneNumberField = (EditText) findViewById(R.id.phone_number);
        lsPhoneNumberField.setInputType(android.text.InputType.TYPE_NULL);

        lsOneButton = (Button) findViewById(R.id.one);
        lsTwoButton = (Button) findViewById(R.id.two);
        lsThreeButton = (Button) findViewById(R.id.three);
        lsFourButton = (Button) findViewById(R.id.four);
        lsFiveButton = (Button) findViewById(R.id.five);
        lsSixButton = (Button) findViewById(R.id.six);
        lsSevenButton = (Button) findViewById(R.id.seven);
        lsEightButton = (Button) findViewById(R.id.eight);
        lsNineButton = (Button) findViewById(R.id.nine);
        lsZeroButton = (Button) findViewById(R.id.zero);
        lsStarButton = (Button) findViewById(R.id.asterisk);
        lsPoundButton = (Button) findViewById(R.id.hash);
        lsDialButton = (Button) findViewById(R.id.dialButton);
        lsDeleteButton = (Button) findViewById(R.id.deleteButton);
        lsPauseButton = (Button) findViewById(R.id.pauseButton);
        lsForwardButton = (Button) findViewById(R.id.forwardButton);
        lsCloseButton = (Button) findViewById(R.id.closeButton);
        lsSpeakerButton = (Button) findViewById(R.id.speaekerButton);

        status = (TextView) findViewById(R.id.callingstatus);

        if(LSService.caller != null){
            status.setText("Calling from:" + LSService.caller);
        }
        else{
            status.setText("" );
        }
    }

    /**
     * Adds number formatting to the field
     */
    private void addNumberFormatting() {
        lsPhoneNumberField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    /**
     * Sets click listeners for the views
     */
    private void setClickListeners() {
        lsZeroButton.setOnClickListener(this);
        lsZeroButton.setOnLongClickListener(this);

        lsOneButton.setOnClickListener(this);
        lsTwoButton.setOnClickListener(this);
        lsThreeButton.setOnClickListener(this);
        lsFourButton.setOnClickListener(this);
        lsFiveButton.setOnClickListener(this);
        lsSixButton.setOnClickListener(this);
        lsSevenButton.setOnClickListener(this);
        lsEightButton.setOnClickListener(this);
        lsNineButton.setOnClickListener(this);
        lsStarButton.setOnClickListener(this);
        lsPoundButton.setOnClickListener(this);
        lsDialButton.setOnClickListener(this);

        lsDeleteButton.setOnClickListener(this);
        lsDeleteButton.setOnLongClickListener(this);

        lsPauseButton.setOnClickListener(this);
        lsForwardButton.setOnClickListener(this);
        lsCloseButton.setOnClickListener(this);
        lsSpeakerButton.setOnClickListener(this);
    }

    private void keyPressed(int keyCode) {
        lsVibrator.vibrate(DURATION);
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        lsPhoneNumberField.onKeyDown(keyCode, event);
    }

    /**
     * Click handler for the views
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one: {
                keyPressed(KeyEvent.KEYCODE_1);
                return;
            }
            case R.id.two: {
                keyPressed(KeyEvent.KEYCODE_2);
                return;
            }
            case R.id.three: {
                keyPressed(KeyEvent.KEYCODE_3);
                return;
            }
            case R.id.four: {
                keyPressed(KeyEvent.KEYCODE_4);
                return;
            }
            case R.id.five: {
                keyPressed(KeyEvent.KEYCODE_5);
                return;
            }
            case R.id.six: {
                keyPressed(KeyEvent.KEYCODE_6);
                return;
            }
            case R.id.seven: {
                keyPressed(KeyEvent.KEYCODE_7);
                return;
            }
            case R.id.eight: {
                keyPressed(KeyEvent.KEYCODE_8);
                return;
            }
            case R.id.nine: {
                keyPressed(KeyEvent.KEYCODE_9);
                return;
            }
            case R.id.zero: {
                keyPressed(KeyEvent.KEYCODE_0);
                return;
            }
            case R.id.hash: {
                keyPressed(KeyEvent.KEYCODE_POUND);
                return;
            }
            case R.id.asterisk: {
                keyPressed(KeyEvent.KEYCODE_STAR);
                return;
            }
            case R.id.deleteButton: {
                keyPressed(KeyEvent.KEYCODE_DEL);
                return;
            }
            case R.id.dialButton: {
                dialNumber();
                return;
            }
            case R.id.forwardButton:{
                forwardCall();
                return;
            }
            case R.id.closeButton:{
                endCall();
                return;
            }
            case R.id.pauseButton:{
                holdCall();
                return;
            }
            case R.id.speaekerButton:{
                speakerCall();
                return;
            }
        }
    }



    /**
     * Long Click Listener
     */
    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.deleteButton: {
                Editable digits = lsPhoneNumberField.getText();
                digits.clear();
                return true;
            }
            case R.id.zero: {
                keyPressed(KeyEvent.KEYCODE_PLUS);
                return true;
            }
        }
        return false;
    }

    private void dialNumber() {
        String number = lsPhoneNumberField.getText().toString();

        if (number.length() > 0) {
            if (LSService.currentCall == null) {
                LSCall myCall = new LSCall(LSService.data, -1);

                CallOpParam prm = new CallOpParam(true);
                CallSetting opt = prm.getOpt();
                opt.setFlag(pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA);
                opt.setAudioCount(1);
                opt.setVideoCount(0);

                String dst_uri = "sip:" + number + "@" + LSService.data.getDomain();
                try {
                    myCall.makeCall(dst_uri, prm);
                    lsPhoneNumberField.getText().clear();
                    status.setText("Calling " + number);
                } catch (Exception e) {
                    Log.v("Call problem: ", e.toString());
                    myCall.delete();
                }
                LSService.currentCall = myCall;
            }
        }
    }

    private void forwardCall(){
        String transferNumber = lsPhoneNumberField.getText().toString();
        if (transferNumber.length() == 0){
            status.setText("Give the forward number");
            return;
        }
        else {
            if (LSService.currentCall != null) {
                CallOpParam prm = new CallOpParam();
                prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
                try {
                    LSService.currentCall.xfer("sip:" + transferNumber + "@" + LSService.data.getDomain(), prm);
                    lsPhoneNumberField.getText().clear();
                    status.setText("Forward to " + transferNumber);
                } catch (Exception e) {
                    Log.v("Forward problem: ", e.toString());
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    status.setText("");
                    finish();
                }
            }, 2000);
            LSService.currentCall = null;
            LSService.caller = null;
        }
    }

    private void endCall(){
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
        try {
            LSService.currentCall.hangup(prm);
            LSService.currentCall = null;
            LSService.caller = null;
            lsPhoneNumberField.getText().clear();
            status.setText("Close Call");
        } catch (Exception e) {
            Log.v("End Call problem: ", e.toString());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                status.setText("");
                finish();
            }
        }, 2000);
    }

    @SuppressLint("LongLogTag")
    private void holdCall(){
        if(LSService.currentCall == null) return;

        CallOpParam param = new CallOpParam(true);

        try {
            if (!hold) {
                LSService.currentCall.setHold(param);
                status.setText("Hold Call");
                hold = true;
            } else {
                CallSetting opt = param.getOpt();
                opt.setAudioCount(1);
                opt.setVideoCount(0);
                opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD);
                LSService.currentCall.reinvite(param);
                status.setText("Resume Call");
                hold = false;
            }
        } catch (Exception e) {
            Log.v("Hold/Unhold Call problem: ", e.toString());
        }
    }

    private void speakerCall() {
        if(LSService.currentCall == null) return;

        if (!speaker) {
            audioManager.setSpeakerphoneOn(true);
            speaker = true;
        }
        else{
            audioManager.setSpeakerphoneOn(false);
            speaker = false;
        }
    }

    public static void endActivity(){
        LSService.currentCall = null;
        LSService.caller = null;
        activity.finish();
    }
}