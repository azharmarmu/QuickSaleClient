package marmu.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.api.FireBaseAPI;
import marmu.com.quicksaleclient.utils.Constants;

@SuppressWarnings({"unchecked", "deprecation"})
public class CreateOrderActivity extends AppCompatActivity implements Serializable {

    String key;
    HashMap<String, Object> orderMap = new HashMap<>();
    HashMap<String, Object> orderItems = new HashMap<>();
    HashMap<String, Object> itemDetails = new HashMap<>();

    TextView salesManListView;
    EditText customerGst, customerAddress;
    AutoCompleteTextView customerName;
    TableLayout tableLayout;
    List<String> salesMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        salesManListView = (TextView) findViewById(R.id.sales_man_list);
        customerName = (AutoCompleteTextView) findViewById(R.id.et_customer_name);
        customerGst = (EditText) findViewById(R.id.et_customer_gst);
        customerAddress = (EditText) findViewById(R.id.et_customer_address);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);
        isOrderEdit(getIntent().getExtras());
        getCustomerDetails();
    }

    private void getCustomerDetails() {
        final List<String> custName = new ArrayList<>();
        final List<String> custGST = new ArrayList<>();
        final List<String> custAddress = new ArrayList<>();
        HashMap<String, Object> customer = FireBaseAPI.customer;
        if (customer.size() > 0) {
            for (String key : customer.keySet()) {
                HashMap<String, Object> customerDetails = (HashMap<String, Object>) customer.get(key);
                custName.add(customerDetails.get("customer_name").toString());
                custGST.add(customerDetails.get("customer_gst").toString());
                custAddress.add(customerDetails.get("customer_address").toString());
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
            key = extras.getString("key");
            orderMap = (HashMap<String, Object>) extras.getSerializable("orderMap");
            if (orderMap != null) {
                salesMan = (List<String>) orderMap.get("sales_man_name");
                populateSalesMan(salesMan);
                customerName.setText((String) orderMap.get("customer_name"));
                if (!String.valueOf(orderMap.get("customer_gst")).isEmpty()) {
                    customerGst.setText((String) orderMap.get("customer_gst"));
                } else {
                    customerGst.setText("NIL");
                }
                customerAddress.setText((String) orderMap.get("customer_address"));
                itemDetails = (HashMap<String, Object>) orderMap.get("sales_order_qty");
                populateItemsDetails();
            }
        } else {
            populateItemsDetails();
            salesManListView.setText("NIL");
        }
    }

    private void populateItemsDetails() {
        HashMap<String, Object> products = FireBaseAPI.productPrice;

        for (String prodKey : products.keySet()) {
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
            if (itemDetails != null && itemDetails.containsKey(prodKey)) {
                productQTY.setText((String) itemDetails.get(prodKey));
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
        intent.putExtra("sales_man", (Serializable) salesMan);
        startActivityForResult(intent, Constants.SALES_MAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SALES_MAN_CODE) {
            try {
                salesMan = (List<String>) data.getSerializableExtra("salesMan");
                populateSalesMan(salesMan);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void populateSalesMan(List<String> salesMan) {
        String salesManName = "";
        for (int i = 0; i < salesMan.size(); i++) {
            if (!salesMan.get(i).isEmpty()) {
                if (i == 0) {
                    salesManName = salesMan.get(i);
                } else {
                    salesManName = salesManName + ", " + salesMan.get(i);
                }
            }
        }
        salesManListView.setText(salesManName);
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
                    orderItems.put(prodName, prodQTY);
                }
            }
        }
        if (localSalesMan.equals("NIL") || localSalesMan.isEmpty()) {
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
            HashMap<String, Object> orders = new HashMap<>();
            orders.put("process", "start");
            orders.put("order_date", Constants.currentDate());
            orders.put("sales_man_name", salesMan);
            orders.put("customer_name", localCustomerName);
            if (!localCustomerGst.isEmpty()) {
                orders.put("customer_gst", localCustomerGst);
            }
            orders.put("customer_address", localCustomerAddress);
            orders.put("sales_order_qty", orderItems);
            orders.put("sales_order_qty_left", orderItems);
            if (key == null) {
                FireBaseAPI.orderDBRef.push().updateChildren(orders);
            } else {
                FireBaseAPI.orderDBRef.child(key).updateChildren(orders);
            }
            finish();
        }
    }
}

