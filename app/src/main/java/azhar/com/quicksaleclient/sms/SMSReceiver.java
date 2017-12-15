package azhar.com.quicksaleclient.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by azharuddin on 19/07/16.
 */
@SuppressWarnings("deprecation")
public class SMSReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages;

        try {
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
                            Intent myIntent = new Intent("otp");
                            myIntent.putExtra("message",OTP);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}