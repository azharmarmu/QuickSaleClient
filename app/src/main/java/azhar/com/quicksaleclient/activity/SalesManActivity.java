package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.adapter.SalesManAdapter;
import azhar.com.quicksaleclient.api.FireBaseAPI;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.model.SalesManModel;

/**
 * Created by azharuddin on 26/7/17.
 */

@SuppressWarnings("unchecked")
public class SalesManActivity extends AppCompatActivity implements Serializable {

    private HashMap<String, Object> salesMan = new HashMap<>();
    private List<SalesManModel> salesManList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_man);

        salesMan = FireBaseAPI.salesMan;

        //// TODO: 26/7/17 checkIn correction

        changeMapToList();

        populateSalesManList();

    }

    private void changeMapToList() {
        if (salesMan != null) {
            for (String key : salesMan.keySet()) {
                salesManList.add(new SalesManModel(key, (String) salesMan.get(key)));
            }
        }
    }

    private void populateSalesManList() {
        SalesManAdapter adapter = new SalesManAdapter(getApplicationContext(), salesManList, Constants.CHECK);
        RecyclerView takenView = (RecyclerView) findViewById(R.id.rv_sales_man);
        takenView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        takenView.setLayoutManager(layoutManager);
        takenView.setItemAnimator(new DefaultItemAnimator());
        takenView.setAdapter(adapter);
    }

    public void salesManDone(View view) {
        List<String> salesMan = SalesManAdapter.getSelectedSalesMan();
        if (salesMan.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("salesMan", (Serializable) salesMan);
            setResult(Constants.SALES_MAN_CODE, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Select sales man", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
