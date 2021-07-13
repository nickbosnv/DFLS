package com.visual.dfls;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.dfls.R;
import com.visual.dfls.MainActivity;

import org.pjsip.pjsua2.Endpoint;

import dfls.sipservice.LSAccountData;
import dfls.sipservice.LSService;

public class Registration extends Activity implements View.OnClickListener {

    public Button login;
    public EditText username;
    public EditText serverIP;
    public EditText password;
    public ProgressBar progressBar;

    public LSAccountData us_account;
    public static Endpoint enPoint;
    public static LSService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_form);

        System.loadLibrary("openh264");
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");

        login = (Button) findViewById(R.id.login);

        username = (EditText) findViewById(R.id.username);
        serverIP = (EditText) findViewById(R.id.serverip);
        password = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        initializeLocalProfile();
    }

    public void initializeLocalProfile() {
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String domain = serverIP.getText().toString();
        user ="vosios";
        pass ="12345678";
        domain = "192.168.2.100";
        if (user.length() == 0 || pass.length() == 0 || domain.length() == 0){
            return;
        }
        else {
            us_account = new LSAccountData(user, domain, pass);
            progressBar.setVisibility(View.VISIBLE);
            MainActivity.status.setText("Online");
            service = new LSService(us_account);
            us_account.updateService(service);
            closeKeyboard();
            if (us_account != null) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        service.addAccount();
                        us_account.updateService(service);
                        finish();
                    }
                }, 2000);
            }
        }

    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
