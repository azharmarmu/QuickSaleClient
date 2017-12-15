package azhar.com.quicksaleclient.modules;

import android.app.Activity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.LandingActivity;
import azhar.com.quicksaleclient.api.ProductsApi;
import azhar.com.quicksaleclient.utils.Constants;


/**
 * Created by azharuddin on 25/7/17.
 */

@SuppressWarnings({"deprecation", "unchecked"})
public class Setup {
    private HashMap<String, Object> products = new HashMap<>();

    private Activity activity;
    private View itemView;

    public void evaluate(LandingActivity activity, final View itemView) {

        this.activity = activity;
        this.itemView = itemView;

        products = ProductsApi.products;
        populateTable();
    }

    private void populateTable() {
        TableLayout tableLayout = itemView.findViewById(R.id.table_layout);
        tableLayout.removeAllViews();
        for (String prodKey : products.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(activity);
            tr.setBackgroundColor(activity.getResources().getColor(R.color.colorLightWhite));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tr.setWeightSum(2);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;


            HashMap<String, Object> productDetails = (HashMap<String, Object>) products.get(prodKey);

            /* Product Name --> EditText */
            TextView name = new TextView(activity);
            name.setLayoutParams(params);

            name.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey.replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
            tr.addView(name);


            /* Product Price --> EditText */
            TextView rate = new TextView(activity);
            rate.setLayoutParams(params);

            rate.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText((String) productDetails.get(Constants.PRODUCT_RATE));
            rate.setGravity(Gravity.CENTER);
            rate.setInputType(InputType.TYPE_CLASS_NUMBER);
            rate.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
            tr.addView(rate); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }
}
