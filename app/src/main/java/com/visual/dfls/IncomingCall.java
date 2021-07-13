package com.visual.dfls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.dfls.R;
import com.visual.dfls.CallActivity;

import org.pjsip.pjsua2.CallOpParam;

import org.pjsip.pjsua2.pjsip_status_code;

import dfls.sipservice.LSAccountData;
import dfls.sipservice.LSService;

public class IncomingCall extends Activity implements View.OnClickListener{

    public static Activity activity = null;

    public static TextView calling;

    ImageView logoDigitalFrontier;
    private Button answerButton;
    private Button muteButton;
    private Button declineButton;

    private Ringtone ring;
    private Vibrator vib;
    private boolean mute = false;


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        initializeView();
        setClickListeners();
        unlockWhenRing();
        ringPhone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ring.stop();
        vib.cancel();
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void unlockWhenRing(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
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

    @SuppressLint("SetTextI18n")
    private void initializeView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.incoming_call);

        calling = (TextView) findViewById(R.id.text_calling);
        calling.setText("Call From: " + LSAccountData.getCallFrom());
        LSService.caller = LSAccountData.getCallFrom();

        answerButton = (Button) findViewById(R.id.answer);
        muteButton = (Button) findViewById(R.id.mute);
        declineButton = (Button) findViewById(R.id.decline);

        ImageView logoDigitalFrontier = (ImageView) findViewById(R.id.dflogo);
    }

    private void ringPhone(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ring = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ring.play();
        vib = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        vib.vibrate(2000);
        try {
            CallOpParam param = new CallOpParam();
            param.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            LSService.currentCall.answer(param);
        } catch (Exception exc) {
            Log.v("Failed to", exc.toString());
        }
    }

    private void setClickListeners() {
        answerButton.setOnClickListener(this);
        muteButton.setOnClickListener(this);
        declineButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.answer: {
                answerCall();
                return;
            }
            case R.id.mute: {
                muteCall();
                return;
            }
            case R.id.decline: {
                declineCall();
            }
        }
    }

    private void answerCall() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            LSService.currentCall.answer(prm);
            ring.stop();
            vib.cancel();
            finish();
        } catch (Exception e) {
            Log.v("Answer Caller Problem: ", e.toString());
        }
        Intent intent = new Intent(this, CallActivity.class);
        startActivity(intent);
    }

    private void muteCall() {
        if (!mute) {
            ring.stop();
            vib.cancel();
            mute = true;
        }
        else{
            ring.play();
            vib.vibrate(2000);
            mute = false;
        }
    }

    private void declineCall() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
        try {
            LSService.currentCall.hangup(prm);
            LSService.currentCall = null;
            LSService.caller = null;
            ring.stop();
            vib.cancel();
            finish();
        } catch (Exception e) {
            Log.v("Decline problem: ", e.toString());
        }
    }

    public static void endActivity(){
        LSService.currentCall = null;
        LSService.caller = null;
        activity.finish();
    }
}


