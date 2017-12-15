package azhar.com.quicksaleclient.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.CreateOrderActivity;
import azhar.com.quicksaleclient.activity.LandingActivity;
import azhar.com.quicksaleclient.adapter.OrderAdapter;
import azhar.com.quicksaleclient.listeners.DateListener;
import azhar.com.quicksaleclient.model.OrderModel;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DateUtils;
import azhar.com.quicksaleclient.utils.DialogUtils;
import azhar.com.quicksaleclient.utils.Persistance;


/**
 * Created by azharuddin on 25/7/17.
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings({"deprecation", "unchecked"})
public class Order implements DateListener {
    private List<OrderModel> orderList = new ArrayList<>();
    private List<String> salesMan = new ArrayList<>();

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
            new DateUtils().datePicker(activity, datePicker, Constants.ORDER);

            changeMapToList(datePicker.getText().toString());

            createOrder();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        datePicker = itemView.findViewById(R.id.et_date_picker);
    }

    private void changeMapToList(String pickedDate) {
        //Todo need to fetch from CloudStore
        DialogUtils.showProgressDialog(activity, activity.getString(R.string.loading));
        FirebaseFirestore.getInstance()
                .collection(Constants.ORDER)
                .whereEqualTo(Constants.ORDER_DATE, pickedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    TextView noOrder = itemView.findViewById(R.id.no_view);
                    RecyclerView orderView = itemView.findViewById(R.id.rv_orders);

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        orderList.clear();
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            String myName = Persistance.getUserData(Constants.MY_NAME, activity);
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(activity.getString(R.string.result),
                                        document.getId() + " => " + document.getData());
                                if (document.contains(Constants.ORDER_SALES_MAN_NAME)) {
                                    salesMan = (List<String>) document.get(Constants.ORDER_SALES_MAN_NAME);
                                    for (int i = 0; i < salesMan.size(); i++) {
                                        if (salesMan.get(i).equalsIgnoreCase(myName)) {
                                            orderList.add(new OrderModel(document.getId(),
                                                    (HashMap<String, Object>) document.getData()));
                                        }
                                    }
                                }
                            }
                            if (orderList.size() > 0) {
                                noOrder.setVisibility(View.GONE);
                                orderView.setVisibility(View.VISIBLE);
                                populateTaken();
                            } else {
                                noOrder.setVisibility(View.VISIBLE);
                                orderView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    private void populateTaken() {
        OrderAdapter adapter = new OrderAdapter(activity, orderList);
        RecyclerView orderView = itemView.findViewById(R.id.rv_orders);
        orderView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        orderView.setLayoutManager(layoutManager);
        orderView.setItemAnimator(new DefaultItemAnimator());
        orderView.setAdapter(adapter);
    }

    private void createOrder() {
        TextView createOrder = itemView.findViewById(R.id.btn_create_order);
        createOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CreateOrderActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public void getDate(String date) {
        changeMapToList(date);
    }
}
