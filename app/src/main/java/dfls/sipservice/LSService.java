package dfls.sipservice;

import android.util.Log;

import com.visual.dfls.AppService;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;

import org.pjsip.pjsua2.pjsip_transport_type_e;

public class LSService extends AppService {

    public static LSAccountData data;
    public static Endpoint endPoint;
    public static LSCall currentCall = null;
    public static String caller = null;

    public LSService(LSAccountData data) {
        this.data = data;
    }

    public void addAccount() {
        if(endPoint == null) {
            startStack();
        }
        try {
            AccountConfig acfg = new AccountConfig();
            acfg.getNatConfig().setIceEnabled(true);
            acfg.setIdUri(data.getIdUri());
            acfg.getRegConfig().setRegistrarUri(data.getRegUri());
            AuthCredInfo cred = new AuthCredInfo("digest", "*", data.username, 0, data.password);
            acfg.getSipConfig().getAuthCreds().add(cred);
            data.create(acfg);
            Thread.sleep(10000);
        } catch (Exception e) {
            Log.v("Can't to create account", e.toString());
        }
    }

    public void startStack() {
        try {
            endPoint = new Endpoint();
            endPoint.libCreate();
            EpConfig epConfig = new EpConfig();
            endPoint.libInit(epConfig);
            TransportConfig sipTpConfig = new TransportConfig();
            sipTpConfig.setPort(5060);
            endPoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            endPoint.libStart();
        } catch (Exception e) {
            Log.d("Initialization failed", e.getMessage());
        }
    }

    public static void closeAccount() {
        data.delete();
        data = null;
    }

    public static void closeEndPoint() {
        endPoint.delete();
        endPoint = null;
    }
}
