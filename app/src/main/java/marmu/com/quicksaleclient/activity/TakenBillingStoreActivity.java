package marmu.com.quicksaleclient.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.adapter.BillAdapter;
import marmu.com.quicksaleclient.model.BillModel;
import marmu.com.quicksaleclient.utils.Constants;
import marmu.com.quicksaleclient.utils.DialogUtils;

@SuppressWarnings("unchecked")
public class TakenBillingStoreActivity extends AppCompatActivity {
    private static final String TAG = "Billing";
    String key;
    private List<BillModel> billList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_billing);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = (String) extras.get("key");

            DialogUtils.showProgressDialog(this, "Loading...");
            FirebaseFirestore.getInstance()
                    .collection(Constants.BILLING)
                    .orderBy("billNo", Query.Direction.ASCENDING)
                    .whereEqualTo("_id", key)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        TextView noBill = (TextView) findViewById(R.id.no_bill);

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            billList.clear();
                            DialogUtils.dismissProgressDialog();
                            if (task.isSuccessful()) {
                                noBill.setVisibility(View.GONE);
                                for (DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    billList.add(new BillModel(key,
                                            document.getId(),
                                            (HashMap<String, Object>) document.getData()));
                                }
                                if (billList.size() > 0) {
                                    populateBillTable();
                                } else {
                                    noBill.setVisibility(View.VISIBLE);
                                }
                            } else {
                                noBill.setVisibility(View.VISIBLE);
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }

    private void populateBillTable() {
        RecyclerView billView = findViewById(R.id.rv_bill);
        BillAdapter adapter = new BillAdapter(getApplicationContext(), billList);
        billView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        billView.setLayoutManager(layoutManager);
        billView.setItemAnimator(new DefaultItemAnimator());
        billView.setAdapter(adapter);
    }
}
