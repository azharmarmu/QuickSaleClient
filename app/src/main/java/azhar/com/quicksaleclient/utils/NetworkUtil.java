package azhar.com.quicksaleclient.utils;

/*
 * Created by azharuddin on 04/08/17.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
* this class is used to check Network connection
* */
public class NetworkUtil {

    private static final int NOT_CONNECTED = 0;      // if device is not connected to internet
    private static final int WIFI = 1;               // if device is connected to wi-fi
    private static final int MOBILE = 2;             // if device is connected to mobile data

    // to check the type of connection and return respective values
    public static int getConnectionStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return WIFI;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return MOBILE;
        }
        return NOT_CONNECTED;
    }
}
