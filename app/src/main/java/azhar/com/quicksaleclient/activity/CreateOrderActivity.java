package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.CustomerApi;
import azhar.com.quicksaleclient.api.OrderNoApi;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DialogUtils;

@SuppressWarnings({"unchecked", "deprecation"})
public class CreateOrderActivity extends AppCompatActivity implements Serializable {

    String key;
    HashMap<String, Object> orderMap = new HashMap<>();
    HashMap<String, Object> orderItems = new HashMap<>();
    HashMap<String, Object> items = new HashMap<>();

    TextView salesManListView;
    EditText customerGst, customerAddress;
    AutoCompleteTextView customerName;
    TableLayout tableLayout;
    List<String> salesMan;
    List<Integer> orderNo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        orderNo = OrderNoApi.orderNo;

        salesManListView = findViewById(R.id.sales_man_list);
        customerName = findViewById(R.id.et_customer_name);
        customerGst = findViewById(R.id.et_customer_gst);
        customerAddress = findViewById(R.id.et_customer_address);
        tableLayout = findViewById(R.id.table_layout);
        isOrderEdit(getIntent().getExtras());
        getCustomerDetails();
    }

    private void getCustomerDetails() {
        final List<String> custName = new ArrayList<>();
        final List<String> custGST = new ArrayList<>();
        final List<String> custAddress = new ArrayList<>();
        HashMap<String, Object> customer = CustomerApi.customer;
        if (customer.size() > 0) {
            for (String customerKey : customer.keySet()) {
                HashMap<String, Object> customerDetails = (HashMap<String, Object>) customer.get(customerKey);
                custName.add(customerDetails.get(Constants.CUSTOMER_NAME).toString());
                custGST.add(customerDetails.get(Constants.CUSTOMER_GST).toString());
                custAddress.add(customerDetails.get(Constants.CUSTOMER_ADDRESS).toString());
            }
        }
        ArrayAdapter<List<String>> adapter =
                new ArrayAdapter(this, android.R.layout.select_dialog_item, custName);
        customerName.setAdapter(adapter);
        customerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                customerGst.setText(custGST.get(position));
                customerAddress.setText(custAddress.get(position));
            }
        });
    }

    private void isOrderEdit(Bundle extras) {
        if (extras != null) {
            key = extras.getString(Constants.KEY);
            orderMap = (HashMap<String, Object>) extras.getSerializable(Constants.ORDER);
            if (orderMap != null) {
                salesMan = (List<String>) orderMap.get(Constants.ORDER_SALES_MAN_NAME);
                populateSalesMan();
                HashMap<String, Object> customer = (HashMap<String, Object>) orderMap.get(Constants.ORDER_CUSTOMER);
                customerName.setText((String) customer.get(Constants.CUSTOMER_NAME));
                if (!String.valueOf(customer.get(Constants.CUSTOMER_GST)).isEmpty()) {
                    customerGst.setText((String) customer.get(Constants.CUSTOMER_GST));
                } else {
                    customerGst.setText(R.string.nil);
                }
                customerAddress.setText((String) customer.get(Constants.CUSTOMER_ADDRESS));
                items = (HashMap<String, Object>) orderMap.get(Constants.ORDER_SALES);
                populateItemsDetails();
            }
        } else {
            populateItemsDetails();
            salesManListView.setText(R.string.nil);
        }
    }

    private void populateItemsDetails() {
        for (String prodKey : items.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setBackground(getResources().getDrawable(R.drawable.box_white));
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setWeightSum(2);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;


            HashMap<String,Object> itemsDetails = (HashMap<String, Object>) items.get(prodKey);

            /* Product Name --> TextView */
            TextView productName = new TextView(this);
            productName.setLayoutParams(params);

            productName.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productName.setPadding(16, 16, 16, 16);
            productName.setText(prodKey);
            productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            productName.setGravity(Gravity.CENTER);
            tr.addView(productName);

            /* Product QTY --> EditText */
            EditText productQTY = new EditText(this);
            productQTY.setLayoutParams(params);

            productQTY.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productQTY.setPadding(16, 16, 16, 16);
            if (itemsDetails != null && itemsDetails.containsKey(prodKey)) {
                productQTY.setText((String) itemsDetails.get(prodKey));
            } else {
                productQTY.setHint("0");
            }
            productQTY.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setInputType(InputType.TYPE_CLASS_NUMBER);
            tr.addView(productQTY); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }

    public void salesManClick(View view) {
        Intent intent = new Intent(CreateOrderActivity.this, SalesManActivity.class);
        intent.putExtra(Constants.SALES_MAN, (Serializable) salesMan);
        startActivityForResult(intent, Constants.SALES_MAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SALES_MAN_CODE) {
            try {
                salesMan = (List<String>) data.getSerializableExtra(Constants.SALES_MAN);
                populateSalesMan();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void populateSalesMan() {
        StringBuilder salesManName = new StringBuilder();
        for (int i = 0; i < salesMan.size(); i++) {
            if (!salesMan.get(i).isEmpty()) {
                if (i == 0) {
                    salesManName = new StringBuilder(salesMan.get(i));
                } else {
                    salesManName.append(", ").append(salesMan.get(i));
                }
            }
        }
        salesManListView.setText(salesManName.toString());
    }

    public void createOrder(View view) throws ParseException {

        String localSalesMan = (String) salesManListView.getText();
        String localCustomerName = String.valueOf(customerName.getText());
        String localCustomerGst = String.valueOf(customerGst.getText());
        String localCustomerAddress = String.valueOf(customerAddress.getText());

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (tableRow != null) {
                TextView productName = (TextView) tableRow.getChildAt(0);
                String prodName = productName.getText().toString().replace("/", "_");
                EditText productQTY = (EditText) tableRow.getChildAt(1);
                String prodQTY = productQTY.getText().toString();
                if (!prodName.isEmpty() && !prodQTY.isEmpty() && Integer.parseInt(prodQTY) > 0) {
                    HashMap<String, Object> productDetails = new HashMap<>();
                    productDetails.put(Constants.TAKEN_SALES_PRODUCT_NAME, prodName);
                    productDetails.put(Constants.TAKEN_SALES_QTY, prodQTY);
                    productDetails.put(Constants.TAKEN_SALES_QTY_STOCK, prodQTY);
                    orderItems.put(prodName, productDetails);
                }
            }
        }
        if (localSalesMan.equals(getString(R.string.nil)) || localSalesMan.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Sales Man", Toast.LENGTH_SHORT).show();
        } else if (localCustomerName.isEmpty()) {
            customerName.setError("Please Enter Customer name");
            customerName.requestFocus();
        } else if (localCustomerAddress.isEmpty()) {
            customerAddress.setError("Please Enter Customer Address");
            customerAddress.requestFocus();
        } else if (orderItems.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Item for sale", Toast.LENGTH_SHORT).show();
        } else {

            int number = 1;
            if (orderNo.size() > 0) {
                number = Integer.parseInt(String.valueOf(orderNo.get(orderNo.size() - 1)));
                number = number + 1;
            }
            orderNo.add(number);

            HashMap<String, Object> customerDetails = new HashMap<>();
            customerDetails.put(Constants.CUSTOMER_NAME, localCustomerName);
            if (!localCustomerGst.isEmpty()) {
                customerDetails.put(Constants.CUSTOMER_GST, localCustomerGst);
            }
            customerDetails.put(Constants.CUSTOMER_ADDRESS, localCustomerAddress);

            HashMap<String, Object> orders = new HashMap<>();
            if (key == null) {
                key = UUID.randomUUID().toString();
            }
            orders.put(Constants.ORDER_ID, key);
            orders.put(Constants.ORDER_NO, orderNo);
            orders.put(Constants.ORDER_PROCESS, Constants.START);
            orders.put(Constants.ORDER_DATE, Constants.currentDate());
            orders.put(Constants.ORDER_SALES, orderItems);
            orders.put(Constants.ORDER_SALES_MAN_NAME, salesMan);
            orders.put(Constants.ORDER_CUSTOMER, customerDetails);

            FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

            DialogUtils.showProgressDialog(CreateOrderActivity.this, getString(R.string.loading));
            dbStore.collection(Constants.ORDER)
                    .add(orders)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            DialogUtils.dismissProgressDialog();
                            if (task.isSuccessful()) {
                                DialogUtils.appToastShort(getApplicationContext(), "Created order successfully");
                                finish();
                            } else {
                                DialogUtils.appToastShort(getApplicationContext(), "Order couldn't be created");
                            }
                        }
                    });
        }
    }
}

