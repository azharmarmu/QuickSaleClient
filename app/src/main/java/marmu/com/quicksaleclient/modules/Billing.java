package marmu.com.quicksaleclient.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.activity.LandingActivity;
import marmu.com.quicksaleclient.adapter.BillAdapter;
import marmu.com.quicksaleclient.model.BillModel;
import marmu.com.quicksaleclient.utils.Constants;
import marmu.com.quicksaleclient.utils.DialogUtils;
import marmu.com.quicksaleclient.utils.Persistance;

/**
 * Created by azharuddin on 25/7/17.
 */

@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"deprecation", "unchecked"})
public class Billing {
    private static final String TAG = "Billing";
    private String key;
    private List<BillModel> billList = new ArrayList<>();

    private Activity activity;
    private View itemView;
    private TextView datePicker;

    public void evaluate(LandingActivity activity, View itemView) {

        try {
            this.activity = activity;
            this.itemView = itemView;

            initViews();
            currentDate();

            datePicker();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initViews() {
        datePicker = itemView.findViewById(R.id.et_date_picker);
    }

    private void currentDate() throws ParseException {
        Date currentDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month <= 9) {
            datePicker.setText("");
            datePicker.append(day + "/" + "0" + (month) + "/" + year);
        } else {
            datePicker.setText("");
            datePicker.append(day + "/" + (month) + "/" + year);
        }
        changeMapToList(datePicker.getText().toString());
    }

    private void changeMapToList(String pickedDate) {
        //Todo need to fetch from CloudStore
        DialogUtils.showProgressDialog(activity, "Loading...");
        FirebaseFirestore.getInstance()
                .collection(Constants.BILLING)
                .orderBy("billNo", Query.Direction.ASCENDING)
                .whereEqualTo("date", pickedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    TextView noBill = itemView.findViewById(R.id.no_bill);

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        billList.clear();
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            noBill.setVisibility(View.GONE);
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());\
                                String number = Persistance.getUserData(Constants.MY_PHONE, LandingActivity.this);
                                if(document.get("salesMan"))
                                billList.add(new BillModel(key,
                                        document.getId(),
                                        (HashMap<String, Object>) document.getData()));
                            }
                        }
                    }
                });
    }

    @SuppressLint("SimpleDateFormat")
    private void datePicker() {
        final TextView datePicker = itemView.findViewById(R.id.et_date_picker);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String pYear = String.valueOf(year);
                                String pMonth = String.valueOf(monthOfYear + 1);
                                String pDay = String.valueOf(dayOfMonth);

                                String cYear = String.valueOf(mYear);
                                String cMonth = String.valueOf(mMonth + 1);
                                String cDay = String.valueOf(mDay);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                try {
                                    Date pickedDate = formatter.parse((pDay + "-" + pMonth + "-" + pYear));
                                    Date currentDate = formatter.parse((cDay + "-" + cMonth + "-" + cYear));
                                    if (pickedDate.compareTo(currentDate) <= 0) {
                                        if ((monthOfYear + 1) <= 9) {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                        } else {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        }
                                        datePicker.clearFocus();
                                        changeMapToList(datePicker.getText().toString());
                                    } else {
                                        datePicker.setError("Choose Valid date");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void populateTaken() {
        BillAdapter adapter = new BillAdapter(activity, billList);
        RecyclerView orderView = itemView.findViewById(R.id.rv_orders);
        orderView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        orderView.setLayoutManager(layoutManager);
        orderView.setItemAnimator(new DefaultItemAnimator());
        orderView.setAdapter(adapter);
    }

}
