package marmu.com.quicksaleclient;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by azharuddin on 1/8/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
