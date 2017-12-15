package azhar.com.quicksaleclient.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.LandingActivity;
import azhar.com.quicksaleclient.adapter.TakenAdapter;
import azhar.com.quicksaleclient.api.TakenApi;
import azhar.com.quicksaleclient.listeners.DateListener;
import azhar.com.quicksaleclient.model.TakenModel;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DateUtils;
import azhar.com.quicksaleclient.utils.DialogUtils;
import azhar.com.quicksaleclient.utils.Persistance;

/**
 * Created by azharuddin on 25/7/17.
 */


@SuppressWarnings("unchecked")
@SuppressLint("SimpleDateFormat")
public class Taken implements DateListener {

    private List<TakenModel> takenList = new ArrayList<>();

    private Activity activity;
    private View itemView;
    private TextView datePicker;

    public void evaluate(LandingActivity activity, View itemView) {

        try {

            this.activity = activity;
            this.itemView = itemView;

            initViews();

            new DateUtils().dateListener(this);
            new DateUtils().currentDate(datePicker);
            new DateUtils().datePicker(activity, datePicker, Constants.TAKEN);

            changeMapToList(datePicker.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        datePicker = itemView.findViewById(R.id.et_date_picker);
    }

    private void changeMapToList(String pickedDate) {
        DialogUtils.showProgressDialog(activity, activity.getString(R.string.loading));
        FirebaseFirestore.getInstance()
                .collection(Constants.TAKEN)
                .whereEqualTo(Constants.TAKEN_DATE, pickedDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    TextView noTaken = itemView.findViewById(R.id.no_view);
                    RecyclerView takenView = itemView.findViewById(R.id.rv_taken);

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        DialogUtils.dismissProgressDialog();
                        if (e != null) {
                            Log.w("Error", "Listen failed.", e);
                            return;
                        }

                        TakenApi.taken.clear();
                        takenList.clear();
                        assert value != null;
                        for (DocumentSnapshot document : value) {
                            Log.d(activity.getString(R.string.result),
                                    document.getId() + " => " + document.getData());
                            HashMap<String, Object> takenDetails = (HashMap<String, Object>) document.getData();
                            List<String> salesMan = (List<String>) takenDetails.get(Constants.TAKEN_SALES_MAN_NAME);
                            String myName = Persistance.getUserData(Constants.MY_NAME, activity);

                            if (salesMan.contains(myName)) {
                                takenList.add(new TakenModel(document.getId(), takenDetails));
                                TakenApi.taken.put(document.getId(), takenDetails);
                            }
                        }
                        if (takenList.size() > 0) {
                            noTaken.setVisibility(View.GONE);
                            takenView.setVisibility(View.VISIBLE);
                            populateTaken();
                        } else {
                            noTaken.setVisibility(View.VISIBLE);
                            takenView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void populateTaken() {
        TakenAdapter adapter = new TakenAdapter(activity, takenList);
        RecyclerView takenView = itemView.findViewById(R.id.rv_taken);
        takenView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        takenView.setLayoutManager(layoutManager);
        takenView.setItemAnimator(new DefaultItemAnimator());
        takenView.setAdapter(adapter);
    }

    @Override
    public void getDate(String date) {
        changeMapToList(date);
    }
}
