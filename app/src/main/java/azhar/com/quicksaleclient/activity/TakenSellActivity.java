package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.BillNoApi;
import azhar.com.quicksaleclient.api.CustomerApi;
import azhar.com.quicksaleclient.api.ProductsApi;
import azhar.com.quicksaleclient.api.TakenApi;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DialogUtils;
import azhar.com.quicksaleclient.utils.Permissions;
import azhar.com.quicksaleclient.utils.SoftInputUtil;


@SuppressWarnings({"unchecked", "deprecation"})
public class TakenSellActivity extends AppCompatActivity {
    String key;
    HashMap<String, Object> takenMap = new HashMap<>();
    HashMap<String, Object> sellItems = new HashMap<>();
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

        billNo = BillNoApi.billNo;

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

        salesManListView = findViewById(R.id.sales_man_list);
        customerName = findViewById(R.id.et_customer_name);
        customerGst = findViewById(R.id.et_customer_gst);
        customerAddress = findViewById(R.id.et_customer_address);
        tableLayout = findViewById(R.id.table_layout);
        totalView = findViewById(R.id.tv_sell_total);
        getCustomerDetails();
        populateSalesMan();
        populateTable();
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
                customerGst.setText(custGST.get(custName.indexOf(customerName.getText().toString())));
                customerAddress.setText(custAddress.get(custName.indexOf(customerName.getText().toString())));
            }
        });
    }

    private void populateSalesMan() {
        salesMan = (List<String>) takenMap.get(Constants.TAKEN_SALES_MAN_NAME);
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

    private void populateTable() {
        itemDetails = (HashMap<String, Object>) takenMap.get(Constants.TAKEN_SALES);
        HashMap<String, Object> products = ProductsApi.products;
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

            HashMap<String, Object> productDetails = (HashMap<String, Object>) products.get(prodKey);


            /* Product Name --> TextView */
            TextView productName = new TextView(this);
            productName.setLayoutParams(params);

            productName.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productName.setPadding(16, 16, 16, 16);
            productName.setText(productDetails.get(Constants.PRODUCT_NAME).toString());
            productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            productName.setGravity(Gravity.CENTER);
            tr.addView(productName);

            /* Product HSN --> TextView */ //visibility gone
            TextView prodHSN = new TextView(this);
            prodHSN.setLayoutParams(params);
            prodHSN.setTextColor(getResources().getColor(R.color.colorLightBlack));
            prodHSN.setText((String) productDetails.get(Constants.PRODUCT_HSN));
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
            productRate.setText((String) productDetails.get(Constants.PRODUCT_RATE));
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
                            HashMap<String, Object> details = (HashMap<String, Object>) itemDetails.get(prodName);
                            int leftQty = Integer.parseInt((String) details.get(Constants.TAKEN_SALES_QTY_STOCK));
                            int QTY = Integer.parseInt(prodQTY);
                            int price = Integer.parseInt(prodPrice);
                            int subTotal = QTY * price;
                            if (leftQty >= QTY && QTY > 0) {
                                productSubTotal.setText(String.valueOf(subTotal));
                            } else {
                                if (QTY == 0) {
                                    productQTY.setError(getString(R.string.not_valid));
                                } else {
                                    productQTY.setError(getString(R.string.no_stock));
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
                    TextView productHSN = (TextView) tableRow.getChildAt(1);
                    EditText productQTY = (EditText) tableRow.getChildAt(2);
                    EditText productRate = (EditText) tableRow.getChildAt(3);
                    TextView productTotal = (TextView) tableRow.getChildAt(4);
                    String prodName = productName.getText().toString()
                            .replace("/", "_");
                    String prodHSN = productHSN.getText().toString();
                    String prodQTY = productQTY.getText().toString();
                    String prodRate = productRate.getText().toString();
                    String prodTotal = productTotal.getText().toString();
                    if (!prodName.isEmpty() && !prodHSN.isEmpty() && !prodQTY.isEmpty() && Integer.parseInt(prodQTY) > 0) {

                        HashMap<String, Object> items = new HashMap<>();
                        items.put(Constants.PRODUCT_NAME, prodName);
                        items.put(Constants.PRODUCT_QTY, prodQTY);
                        items.put(Constants.PRODUCT_RATE, prodRate);
                        items.put(Constants.PRODUCT_HSN, prodHSN);
                        items.put(Constants.PRODUCT_TOTAL, prodTotal);
                        sellItems.put(prodName, items);
                    }
                }
            }

            if (localSalesMan.equals(getString(R.string.nil))
                    || localSalesMan.isEmpty()) {
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

                HashMap<String, Object> customerDetails = new HashMap<>();
                customerDetails.put(Constants.CUSTOMER_NAME, localCustomerName);
                if (!localCustomerGst.isEmpty()) {
                    customerDetails.put(Constants.CUSTOMER_GST, localCustomerGst);
                }
                customerDetails.put(Constants.CUSTOMER_ADDRESS, localCustomerAddress);

                HashMap<String, Object> soldOrders = new HashMap<>();
                soldOrders.put(Constants.BILL_ID, key);
                soldOrders.put(Constants.BILL_DATE, Constants.currentDate());
                soldOrders.put(Constants.BILL_NO, getString(R.string.bill_prefix) + number);
                soldOrders.put(Constants.BILL_SALES_MAN_NAME, salesMan);
                soldOrders.put(Constants.BILL_CUSTOMER, customerDetails);
                soldOrders.put(Constants.BILL_SALES, sellItems);
                soldOrders.put(Constants.BILL_NET_TOTAL, totalView.getText().toString());
                soldOrders.put(Constants.BILL_ROUTE, takenMap.get(Constants.TAKEN_ROUTE));

                final HashMap<String, Object> finalSoldOrders = soldOrders;
                final HashMap<String, Object> updateLeftStock = updateLeftStock();
                final int finalNumber = number;

                DialogUtils.showProgressDialog(TakenSellActivity.this, getString(R.string.loading));

                FirebaseFirestore.getInstance().collection(Constants.BILLING)
                        .add(finalSoldOrders)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull final Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    DialogUtils.dismissProgressDialog();
                                    final String billKey = task.getResult().getId();
                                    // Update taken
                                    FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                                    dbStore.collection(Constants.TAKEN)
                                            .document(key)
                                            .update(Constants.TAKEN_SALES, updateLeftStock)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> subTask) {
                                                    if (subTask.isSuccessful()) {
                                                        Toast.makeText(TakenSellActivity.this,
                                                                "Taken updated!",
                                                                Toast.LENGTH_SHORT).show();
                                                        // Update Bill-No
                                                        BillNoApi.billNoDBRef.setValue(billNo);

                                                        /*Navigate to printer*/
                                                        Intent intent = new Intent(TakenSellActivity.this,
                                                                PrintActivity.class);
                                                        intent.putExtra(Constants.KEY, billKey);
                                                        intent.putExtra(Constants.BILL_NO,
                                                                getString(R.string.bill_prefix) + finalNumber);
                                                        intent.putExtra(Constants.BILL_SALES, finalSoldOrders);
                                                        startActivity(intent);
                                                        RelativeLayout holder = findViewById(R.id.holder);
                                                        new SoftInputUtil().hideSoftInput(holder, TakenSellActivity.this);
                                                        finish();
                                                    }
                                                }
                                            });

                                }
                            }
                        });

            }
        } else {
            Toast.makeText(getApplicationContext(), "Storage permission is required", Toast.LENGTH_SHORT).show();
        }

    }

    private HashMap<String, Object> updateLeftStock() {
        HashMap<String, Object> updatedTaken = itemDetails;
        for (String itemKey : itemDetails.keySet()) {
            if (sellItems.containsKey(itemKey)) {
                HashMap<String, Object> stockItems = (HashMap<String, Object>) itemDetails.get(itemKey);
                HashMap<String, Object> soldItems = (HashMap<String, Object>) sellItems.get(itemKey);
                int left = Integer.parseInt(stockItems.get(Constants.TAKEN_SALES_QTY_STOCK).toString());
                int sold = Integer.parseInt(soldItems.get(Constants.TAKEN_SALES_QTY).toString());
                String available = String.valueOf(left - sold);
                HashMap<String, Object> items = new HashMap<>();
                items.put(Constants.TAKEN_SALES_PRODUCT_NAME, itemKey);
                items.put(Constants.TAKEN_SALES_QTY_STOCK, available);
                items.put(Constants.TAKEN_SALES_QTY, stockItems.get(Constants.TAKEN_SALES_QTY).toString());
                updatedTaken.put(itemKey, items);
            }
        }
        return updatedTaken;
    }
}