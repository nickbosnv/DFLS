package com.visual.dfls;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dfls.R;
import com.example.dfls.databinding.ActivityMainBinding;

import dfls.sipservice.LSService;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private static MainActivity _instance = null;

    private ActivityMainBinding binding;

    public static TextView status;
    private int flag = 0;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity._instance = this;

        if(flag == 0){
            this.startService( new Intent( this, AppService.class ) );
            flag ++;
            requestCameraPermission();
        }

        //unlockScreen();

        initializeView();
    }

    private void initializeView(){

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ImageView logoDigitalFrontier = (ImageView) findViewById(R.id.df_logo);

        final TextView dfText = (TextView) findViewById(R.id.dfl_text);
        final TextView lineSoftphoneText = (TextView) findViewById(R.id.line_softphone);
        status = (TextView) findViewById(R.id.sipLabel);

        status.setText("You must Login");

        binding.makecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCallActivity();
            }
        });

    }

    public void openCallActivity(){
        if (LSService.data == null) return;

        Intent intent = new Intent(this, CallActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sipLogin) {
            if(LSService.data == null) {
                Intent intentReg = new Intent(this, Registration.class);
                startActivity(intentReg);
            }
        }
        else if (id == R.id.sipLogout) {
            if(LSService.data != null) {
                LSService.closeAccount();
                status.setText("Profile Logged Out");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LSService.closeEndPoint();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        // Do what you want.
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Your Permission is needed to get access the camera", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO}, 50);
            }
        }
    }

    public static MainActivity getInstance() {
        return _instance;
    }

    public void startNewActivity() {
        Log.v(TAG, "in startNewActivity");
        Intent intent = new Intent(this, IncomingCall.class);
        this.startActivity(intent);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 50: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Activity", "Granted!");
                }
                else {
                    Log.d("Activity", "Denied!");
                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void unlockScreen(){
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
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON|
                    WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
    }
}