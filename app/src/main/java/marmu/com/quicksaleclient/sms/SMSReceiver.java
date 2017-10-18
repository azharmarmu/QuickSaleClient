package marmu.com.quicksaleclient.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by azharuddin on 19/07/16.
 */
@SuppressWarnings("deprecation")
public class SMSReceiver extends BroadcastReceiver {

    private static SMSListener mListener;

    public void onReceive(Context context, Intent intent) {
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages;

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");
            assert pdus != null;
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (messages[i].getMessageBody().contains("is your verification code.")) {
                    String OTP = messages[i].getMessageBody();
                    OTP = OTP.replaceAll("\\D+", "");
                    if (OTP.matches(".*\\d+.*")) {
                        mListener.messageReceived(OTP);
                    }
                }
            }
        }
    }

    public static void bindListener(SMSListener listener) {
        mListener = listener;
    }
}