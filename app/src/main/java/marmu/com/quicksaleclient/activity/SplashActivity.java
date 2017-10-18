package marmu.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import marmu.com.quicksaleclient.api.FireBaseAPI;
import marmu.com.quicksaleclient.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FireBaseAPI.getCustomer();
        FireBaseAPI.getSalesMan();
        FireBaseAPI.getUsers();
        FireBaseAPI.getBillNo();
        FireBaseAPI.getProductPrice();
        FireBaseAPI.getProductHSN();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent activity;
                if (isLoggedIn()) {
                    activity = new Intent(SplashActivity.this, LandingActivity.class);
                } else {
                    activity = new Intent(SplashActivity.this, LoginActivity.class);
                }
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
                finish();
            }
        }, 500);

    }

    private boolean isLoggedIn() {
        return Constants.AUTH.getCurrentUser() != null;
    }
}
