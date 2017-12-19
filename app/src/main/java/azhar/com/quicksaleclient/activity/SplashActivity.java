package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import azhar.com.quicksaleclient.api.BillNoApi;
import azhar.com.quicksaleclient.api.CustomerApi;
import azhar.com.quicksaleclient.api.ProductsApi;
import azhar.com.quicksaleclient.api.SalesManApi;
import azhar.com.quicksaleclient.api.UsersApi;
import azhar.com.quicksaleclient.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new SalesManApi().getSalesMan(); //calling salesman Api
        new CustomerApi().getCustomer(); //calling Customer Api
        new UsersApi().getUsers(); //calling Users Api
        new BillNoApi().getBillNo(); //calling BillNo Api
        new ProductsApi().getProducts(); //calling Products Api

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
