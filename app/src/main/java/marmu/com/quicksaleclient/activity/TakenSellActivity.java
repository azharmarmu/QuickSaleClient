package marmu.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.api.FireBaseAPI;
import marmu.com.quicksaleclient.utils.Constants;
import marmu.com.quicksaleclient.utils.Permissions;

@SuppressWarnings({"unchecked", "deprecation"})
public class TakenSellActivity extends AppCompatActivity {
    String key;
    HashMap<String, Object> takenMap = new HashMap<>();
    HashMap<String, Object> sellItems = new HashMap<>();
    HashMap<String, Object> sellItemsRate = new HashMap<>();
    HashMap<String, Object> sellItemsHSN = new HashMap<>();
    HashMap<String, Object> sellItemsTotal = new HashMap<>();
    HashMap<String, Object> itemDetails = new HashMap<>();

    TextView salesManListView, totalView;
    EditText customerGst, customerAddress;
    AutoCompleteTextView customerName;
    TableLayout tableLayout;
    List<String> salesMan = new ArrayList<>();
    List<Integer> billNo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taken_sell);

        billNo = FireBaseAPI.billNo;

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


        salesManListView = (TextView) findViewById(R.id.sales_man_list);
        customerName = (AutoCompleteTextView) findViewById(R.id.et_customer_name);
        customerGst = (EditText) findViewById(R.id.et_customer_gst);
        customerAddress = (EditText) findViewById(R.id.et_customer_address);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);
        totalView = (TextView) findViewById(R.id.tv_sell_total);

        String[] countries = {"Azhar","sudha"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        customerName.setAdapter(adapter);

        FireBaseAPI.productDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    FireBaseAPI.productPrice = (HashMap<String, Object>) dataSnapshot.getValue();
                    populateSalesMan();
                    populateTable();
                } else {
                    FireBaseAPI.productPrice.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireError", databaseError.getMessage());
            }
        });
    }

    private void performSearch() {
        //// TODO: 18/10/17  
    }

    private void populateSalesMan() {
        salesMan = (List<String>) takenMap.get("sales_man_name");
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

    private void populateTable() {
        itemDetails = (HashMap<String, Object>) takenMap.get("sales_order_qty_left");
        HashMap<String, Object> productPrice = FireBaseAPI.productPrice;
        HashMap<String, Object> productHSN = FireBaseAPI.productHSN;
        for (String prodKey : itemDetails.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setBackground(getResources().getDrawable(R.drawable.box_white));
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setWeightSum(4);

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
            productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            productName.setGravity(Gravity.CENTER);
            tr.addView(productName);

            /* Product HSN --> TextView */ //visibility gone
            TextView prodHSN = new TextView(this);
            prodHSN.setLayoutParams(params);
            prodHSN.setTextColor(getResources().getColor(R.color.colorLightBlack));
            prodHSN.setText(productHSN.get(prodKey).toString());
            prodHSN.setVisibility(View.GONE);
            tr.addView(prodHSN);

            /* Product QTY --> EditText */
            EditText productQTY = new EditText(this);
            productQTY.setLayoutParams(params);

            productQTY.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productQTY.setPadding(16, 16, 16, 16);
            productQTY.setHint("0");
            productQTY.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setInputType(InputType.TYPE_CLASS_NUMBER);
            tr.addView(productQTY);

            /* Product Rate --> EditText */
            EditText productRate = new EditText(this);
            productRate.setLayoutParams(params);

            productRate.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productRate.setPadding(16, 16, 16, 16);
            productRate.setHint("0");
            productRate.setText(productPrice.get(prodKey).toString());
            productRate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            productRate.setGravity(Gravity.CENTER);
            productRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            tr.addView(productRate);

            TextView productTotal = new TextView(this);
            productQTY.setLayoutParams(params);

            productTotal.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productTotal.setPadding(16, 16, 16, 16);
            productTotal.setHint("0000");
            productTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            productTotal.setGravity(Gravity.END);
            productTotal.setInputType(InputType.TYPE_CLASS_NUMBER);
            tr.addView(productTotal);
            // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }

        addQTy();
    }

    private void addQTy() {
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (tableRow != null) {
                final TextView productName = (TextView) tableRow.getChildAt(0);
                final EditText productQTY = (EditText) tableRow.getChildAt(2);
                final EditText productPrice = (EditText) tableRow.getChildAt(3);
                final TextView productSubTotal = (TextView) tableRow.getChildAt(4);
                productQTY.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String prodName = productName.getText().toString();
                        String prodQTY = productQTY.getText().toString();
                        String prodPrice = productPrice.getText().toString();

                        if (prodQTY.isEmpty()) {
                            productSubTotal.setText("");
                            productSubTotal.setHint("0000");
                        } else if (!prodQTY.isEmpty() && !prodPrice.isEmpty()) {
                            int leftQty = Integer.parseInt(itemDetails.get(prodName).toString());
                            int QTY = Integer.parseInt(prodQTY);
                            int price = Integer.parseInt(prodPrice);
                            int subTotal = QTY * price;
                            if (leftQty >= QTY && QTY > 0) {
                                productSubTotal.setText(String.valueOf(subTotal));
                            } else {
                                if (QTY == 0) {
                                    productQTY.setError("Not valid");
                                } else {
                                    productQTY.setError("No stock");
                                }
                                productQTY.setText("");
                                productQTY.setHint("0");
                                productQTY.requestFocus();
                            }
                        }
                        total();
                    }
                });

                productPrice.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String prodQTY = productQTY.getText().toString();
                        String prodPrice = productPrice.getText().toString();

                        if (prodPrice.isEmpty()) {
                            productSubTotal.setText("");
                            productSubTotal.setHint("0000");
                        } else if (!prodQTY.isEmpty() && !prodPrice.isEmpty()) {
                            int QTY = Integer.parseInt(prodQTY);
                            int price = Integer.parseInt(prodPrice);
                            int subTotal = QTY * price;
                            productSubTotal.setText(String.valueOf(subTotal));
                        }
                        total();
                    }
                });
            }
        }
    }

    private void total() {
        int total = 0;
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (tableRow != null) {
                final TextView productSubTotal = (TextView) tableRow.getChildAt(4);
                String value = productSubTotal.getText().toString();
                if (!value.isEmpty())
                    total += Integer.parseInt(value);
            }
        }
        totalView.setText(String.valueOf(total));
    }

    public void sellItems(View view) throws ParseException {
        if (Permissions.EXTERNAL_STORAGE(TakenSellActivity.this)) {
            String localSalesMan = (String) salesManListView.getText();
            String localCustomerName = String.valueOf(customerName.getText());
            String localCustomerGst = String.valueOf(customerGst.getText());
            String localCustomerAddress = String.valueOf(customerAddress.getText());

            for (int i = 0; i < tableLayout.getChildCount(); i++) {
                TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                if (tableRow != null) {
                    TextView productName = (TextView) tableRow.getChildAt(0);
                    String prodName = productName.getText().toString().replace("/", "_");
                    TextView productHSN = (TextView) tableRow.getChildAt(1);
                    EditText productQTY = (EditText) tableRow.getChildAt(2);
                    EditText productRate = (EditText) tableRow.getChildAt(3);
                    TextView productTotal = (TextView) tableRow.getChildAt(4);
                    String prodHSN = productHSN.getText().toString();
                    String prodQTY = productQTY.getText().toString();
                    String prodRate = productRate.getText().toString();
                    String prodTotal = productTotal.getText().toString();
                    if (!prodName.isEmpty() && !prodHSN.isEmpty() && !prodQTY.isEmpty() && Integer.parseInt(prodQTY) > 0) {
                        sellItems.put(prodName, prodQTY);
                        sellItemsRate.put(prodName, prodRate);
                        sellItemsHSN.put(prodName, prodHSN);
                        sellItemsTotal.put(prodName, prodTotal);
                    }
                }
            }

            HashMap<String, Object> avail = updateLeftStock();

            if (localSalesMan.equals("NIL") || localSalesMan.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Select Sales Man", Toast.LENGTH_SHORT).show();
            } else if (localCustomerName.isEmpty()) {
                customerName.setError("Please Enter Customer name");
                customerName.requestFocus();
            } else if (sellItems.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Select Item for sale", Toast.LENGTH_SHORT).show();
            } else {

                int number = 1;
                if (billNo.size() > 0) {
                    number = Integer.parseInt(String.valueOf(billNo.get(billNo.size() - 1)));
                    number = number + 1;
                }
                billNo.add(number);

                HashMap<String, Object> soldOrders = new HashMap<>();
                soldOrders.put("sold_date", Constants.currentDate());
                soldOrders.put("bill_no", "RS-" + number);
                soldOrders.put("sales_man_name", salesMan);
                soldOrders.put("customer_name", localCustomerName);
                if (!localCustomerGst.isEmpty()) {
                    soldOrders.put("customer_gst", localCustomerGst);
                }
                soldOrders.put("sold_items", sellItems);
                soldOrders.put("sold_items_rate", sellItemsRate);
                soldOrders.put("sold_items_hsn", sellItemsHSN);
                soldOrders.put("sold_items_total", sellItemsTotal);
                soldOrders.put("customer_address", localCustomerAddress);
                soldOrders.put("net_total", totalView.getText().toString());
                soldOrders.put("sold_route", takenMap.get("sales_route"));
                FireBaseAPI.billingDBREf.child(key).child("RS-" + number).updateChildren(soldOrders);
                FireBaseAPI.takenDBRef.child(key).child("sales_order_qty_left").updateChildren(avail);
                FireBaseAPI.billNoDBRef.setValue(billNo);

                Intent intent = new Intent(TakenSellActivity.this, PrintActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("bill_no", "RS-" + number);
                intent.putExtra("sold_orders", soldOrders);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Storage permission is required", Toast.LENGTH_SHORT).show();
        }

    }


    private HashMap<String, Object> updateLeftStock() {
        HashMap<String, Object> avail = new HashMap<>();
        for (String key : itemDetails.keySet()) {
            if (sellItems.containsKey(key)) {
                int left = Integer.parseInt(itemDetails.get(key).toString());
                int sold = Integer.parseInt(sellItems.get(key).toString());
                String available = String.valueOf(left - sold);
                avail.put(key, available);
            }
        }
        return avail;
    }
}
