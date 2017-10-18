package marmu.com.quicksaleclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by azharuddin on 27/7/17.
 */

public class Persistance {

    public static void saveUserData(String key,String data,Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, data);
        editor.apply();

    }
    public static String getUserData(String key, Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        return sharedPref.getString(key,"");
    }
}
