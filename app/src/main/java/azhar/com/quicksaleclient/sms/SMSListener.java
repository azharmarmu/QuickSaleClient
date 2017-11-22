package azhar.com.quicksaleclient.sms;

/**
 * Created by azharuddin on 8/6/17.
 */

public interface SMSListener {
    void messageReceived(String messageText);
}
