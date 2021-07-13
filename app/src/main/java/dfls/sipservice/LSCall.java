package dfls.sipservice;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.visual.dfls.CallActivity;
import com.visual.dfls.IncomingCall;

import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;

import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;

import org.pjsip.pjsua2.pjsua_call_media_status;

public class LSCall extends Call {

    private Handler handler = new Handler(Looper.getMainLooper());

    private int role;
    private int state;

    private boolean answer = false;

    private boolean isFirst = true;

    private OnCallStateListener onCallStateListener;
    private OnComListener listener;

    public LSCall(LSAccountData myAccount, int i) {
        super(myAccount,i);
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        try {
            CallInfo info = getInfo();
            state = info.getState();
            role = info.getRole();
            System.out.println(" ---- Role: " + role + " ---- " + "\t" + " ---- State: " + state + " ---- ");

            if (role == pjsip_role_e.PJSIP_ROLE_UAC) {
                if (state == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                    onCallStateListener.calling();
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    if (isFirst) {
                        onCallStateListener.early();
                        isFirst = false;
                    }
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                    onCallStateListener.connecting();
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    onCallStateListener.confirmed();
                } else if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    System.out.println("Hangup");
                    try {
                        CallActivity.endActivity();
                    } catch (Exception ignored) {
                        Log.v("Caller closed problem: ",ignored.toString());
                    }
                    onCallStateListener.disconnected();
                }

            } else if (role == pjsip_role_e.PJSIP_ROLE_UAS) {
                if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                    if (answer == true){
                        System.out.println("Hangup");
                        try {
                            CallActivity.endActivity();
                        } catch (Exception ignored) {
                            Log.v("Caller closed problem: ",ignored.toString());
                        }
                        onCallStateListener.disconnected();
                    }
                    else {
                        System.out.println("Hangup");
                        try {
                            IncomingCall.endActivity();
                        } catch (Exception ignored) {
                            Log.v("Caller closed problem: ", ignored.toString());
                        }
                        if (listener != null) {
                            listener.disconnected();
                        }
                    }
                }
                else if (state == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                    if (isFirst) {
                        onCallStateListener.early();
                        isFirst = false;
                    }
                }
                else if (state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                    System.out.println("Pick up");
                    answer = true;
                    onCallStateListener.confirmed();
                }
            }
        } catch (Exception e) {
            if (role == pjsip_role_e.PJSIP_ROLE_UAC) {
                if (onCallStateListener != null) {
                    onCallStateListener.error();
                }
            } else if (role == pjsip_role_e.PJSIP_ROLE_UAS) {

                if (listener != null) {
                    listener.disconnected();
                }
                if (onCallStateListener != null) {
                    onCallStateListener.error();
                }
            } else {
                if (LSService.currentCall != null) {
                    LSService.currentCall.delete();
                }
                LSService.currentCall = null;
                if (listener != null) {
                    listener.disconnected();
                }
                if (onCallStateListener != null) {
                    onCallStateListener.error();
                }
            }
        }

    }


    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        CallInfo ci;
        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }

        CallMediaInfoVector cmiv = ci.getMedia();

        for (int i = 0; i < cmiv.size(); i++) {
            CallMediaInfo cmi = cmiv.get(i);
            if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD)) {
                Media m = getMedia(i);
                AudioMedia am = AudioMedia.typecastFromMedia(m);
                try {
                    LSService.endPoint.audDevManager().getCaptureDevMedia().startTransmit(am);
                    am.startTransmit(LSService.endPoint.audDevManager().getPlaybackDevMedia());
                } catch (Exception e) {
                    continue;
                }
            }
        }

    }
}



