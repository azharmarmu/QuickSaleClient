package marmu.com.quicksaleclient.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.api.FireBaseAPI;

@SuppressWarnings("unchecked")
public class TakenActivity extends AppCompatActivity {

    String key;
    HashMap<String, Object> takenMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            key = (String) extras.get("key");
            for (String my_key : FireBaseAPI.taken.keySet()) {
                if (key.equals(my_key)) {
                    takenMap = (HashMap<String, Object>) FireBaseAPI.taken.get(key);
                    break;
                }
            }
        }

    }

    public void purchaseClick(View view) {
        Intent takenPurchaseActivity = new Intent(TakenActivity.this, TakenSellActivity.class);
        takenPurchaseActivity.putExtra("key", key);
        startActivity(takenPurchaseActivity);
    }

    public void billingClick(View view) {
        Intent takenBillingActivity = new Intent(TakenActivity.this, TakenBillingActivity.class);
        takenBillingActivity.putExtra("key", key);
        startActivity(takenBillingActivity);
    }

    public void viewStockClick(View view) {
        Intent viewStockActivity = new Intent(TakenActivity.this, ViewStockActivity.class);
        viewStockActivity.putExtra("key", key);
        startActivity(viewStockActivity);
    }

    public void closeClick(View view) {
        FireBaseAPI.takenDBRef.child(key).child("process").setValue("close");
        finish();
    }
}
