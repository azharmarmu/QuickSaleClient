package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.TakenApi;
import azhar.com.quicksaleclient.utils.Constants;

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
            key = (String) extras.get(Constants.KEY);
            for (String my_key : TakenApi.taken.keySet()) {
                if (key.equals(my_key)) {
                    takenMap = (HashMap<String, Object>) TakenApi.taken.get(key);
                    break;
                }
            }
        }
    }

    public void purchaseStoreClick(View view) {
        Intent takenPurchaseActivity =
                new Intent(TakenActivity.this, TakenSellActivity.class);
        takenPurchaseActivity.putExtra(Constants.KEY, key);
        startActivity(takenPurchaseActivity);
    }

    public void billingClick(View view) {
        Intent takenBillingActivity =
                new Intent(TakenActivity.this, TakenBillingActivity.class);
        takenBillingActivity.putExtra(Constants.KEY, key);
        startActivity(takenBillingActivity);
    }

    public void viewStockClick(View view) {
        Intent viewStockActivity =
                new Intent(TakenActivity.this, ViewStockActivity.class);
        viewStockActivity.putExtra(Constants.KEY, key);
        startActivity(viewStockActivity);
    }

    public void closeClick(View view) {
        FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
        dbStore.collection(Constants.TAKEN)
                .document(key)
                .update(Constants.TAKEN_PROCESS, Constants.CLOSED)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TakenActivity.this,
                                    "Sales Closed!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}
