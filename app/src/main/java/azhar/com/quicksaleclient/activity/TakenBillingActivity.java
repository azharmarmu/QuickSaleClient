package azhar.com.quicksaleclient.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.adapter.BillAdapter;
import azhar.com.quicksaleclient.api.FireBaseAPI;
import azhar.com.quicksaleclient.model.BillModel;

@SuppressWarnings("unchecked")
public class TakenBillingActivity extends AppCompatActivity {
    String key;
    private static List<BillModel> billList = new ArrayList<>();
    HashMap<String, Object> billMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_billing);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = (String) extras.get("key");

            FireBaseAPI.billingDBREf.keepSynced(true);
            FireBaseAPI.billingDBREf.addValueEventListener(new ValueEventListener() {
                TextView noBill = (TextView) findViewById(R.id.no_bill);

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        billList = new ArrayList<>();
                        noBill.setVisibility(View.GONE);
                        FireBaseAPI.billing = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (String my_key : FireBaseAPI.billing.keySet()) {
                            if (key.equals(my_key)) {
                                billMap = (HashMap<String, Object>) FireBaseAPI.billing.get(key);
                                if (billMap != null) {
                                    for (String name : billMap.keySet()) {
                                        HashMap<String, Object> billName = (HashMap<String, Object>) billMap.get(name);
                                        billList.add(new BillModel(my_key, name, billName));
                                    }
                                }
                                break;
                            }
                        }
                        if (billList.size() > 0) {
                            populateBillTable();
                        } else {
                            noBill.setVisibility(View.VISIBLE);
                        }
                    } else {
                        noBill.setVisibility(View.VISIBLE);
                        FireBaseAPI.billing.clear();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    noBill.setVisibility(View.VISIBLE);
                    Log.e("FireError", databaseError.getMessage());
                }
            });
        }
    }

    private void populateBillTable() {
        RecyclerView billView = (RecyclerView) findViewById(R.id.rv_bill);
        BillAdapter adapter = new BillAdapter(getApplicationContext(), billList);
        billView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        billView.setLayoutManager(layoutManager);
        billView.setItemAnimator(new DefaultItemAnimator());
        billView.setAdapter(adapter);
    }
}
