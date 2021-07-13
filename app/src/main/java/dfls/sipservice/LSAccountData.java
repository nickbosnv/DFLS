package dfls.sipservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.visual.dfls.MainActivity;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;

import static android.content.ContentValues.TAG;

public class LSAccountData extends Account{

    public String username;
    public String domain;
    public String password;
    public Boolean registered = false;
    private LSService service;
    public static String callFrom;
    private Handler handler = new Handler();
    private Context context;


    public LSAccountData(String username, String domain, String password){
        this.username = username;
        this.domain = domain;
        this.password = password;
        this.registered = true;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        System.out.println("On registration state: " + prm.getCode() + " " + prm.getReason());
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        LSCall call = new LSCall(LSService.data, prm.getCallId());
        setCallFrom(prm);
        LSService.currentCall = call;
        incomingonCall();
    }

    public String getUsername() {
        return username;
    }

    public String getDomain() {
        return domain;
    }

    public String getPassword() {
        return password;
    }

    public String getRegUri() {
        return "sip:"+ domain;
    }

    public String getIdUri() {
        return "<sip:"+ username +"@"+ domain +">";
    }

    public void updateService(LSService service) {
        this.service = service;
    }

    private void setCallFrom(OnIncomingCallParam msg){
        String[] lines = msg.getRdata().getWholeMsg().split(System.getProperty("line.separator"));
        String from = lines[5].split(":")[2];
        from = from.split("@")[0];
        callFrom = from;
        System.out.println("Sip data: " + from);
    }

    public static String getCallFrom(){
        return callFrom;
    }

    @SuppressLint("WrongConstant")
    public void incomingonCall() {
        System.out.println("========  Someone calls ======== ");
        MainActivity.getInstance().startNewActivity();
    }
}
