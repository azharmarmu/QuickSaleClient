package azhar.com.quicksaleclient.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.FireBaseAPI;


@SuppressWarnings({"unchecked", "deprecation"})
public class ViewStockActivity extends AppCompatActivity {
    String key;
    HashMap<String, Object> takenMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stock);

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

        populateTable();
    }

    private void populateTable() {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);
        HashMap<String, Object> productTakenDetails = (HashMap<String, Object>) takenMap.get("sales_order_qty");
        HashMap<String, Object> productLeftDetails = (HashMap<String, Object>) takenMap.get("sales_order_qty_left");
        tableLayout.removeAllViews();
        for (String prodKey : productTakenDetails.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(getApplicationContext());
                tr.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorLightWhite));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tr.setWeightSum(4);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;

            int takenQty = Integer.parseInt(productTakenDetails.get(prodKey).toString());
            int leftQty = Integer.parseInt(productLeftDetails.get(prodKey).toString());
            int soldQty = takenQty - leftQty;


            /* Product Name --> TextView */
            TextView name = new TextView(getApplicationContext());
            name.setLayoutParams(params);

            name.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey.replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(name);


            /* Product Taken --> TextView */
            TextView taken = new TextView(getApplicationContext());
            taken.setLayoutParams(params);

            taken.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            taken.setPadding(16, 16, 16, 16);
            taken.setText(String.valueOf(takenQty));
            taken.setGravity(Gravity.CENTER);
            taken.setInputType(InputType.TYPE_CLASS_NUMBER);
            taken.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(taken);

            /* Product Sold --> TextView */
            TextView sold = new TextView(getApplicationContext());
            sold.setLayoutParams(params);

            sold.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            sold.setPadding(16, 16, 16, 16);
            sold.setText(String.valueOf(soldQty));
            sold.setGravity(Gravity.CENTER);
            sold.setInputType(InputType.TYPE_CLASS_NUMBER);
            sold.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(sold);

            /* Product Left --> TextView */
            TextView left = new TextView(getApplicationContext());
            left.setLayoutParams(params);

            left.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            left.setPadding(16, 16, 16, 16);
            left.setText(String.valueOf(leftQty));
            left.setGravity(Gravity.CENTER);
            left.setInputType(InputType.TYPE_CLASS_NUMBER);
            left.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(left);

            // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }
}
