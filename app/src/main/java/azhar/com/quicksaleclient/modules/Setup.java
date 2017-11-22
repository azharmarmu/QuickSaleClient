package azhar.com.quicksaleclient.modules;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.FireBaseAPI;


/**
 * Created by azharuddin on 25/7/17.
 */

@SuppressWarnings({"deprecation", "unchecked"})
public class Setup {
    private static HashMap<String, Object> productPrice = new HashMap<>();

    public static void evaluate(final Context context, final View itemView) {
        FireBaseAPI.productDBRef.keepSynced(true);
        FireBaseAPI.productDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    FireBaseAPI.productPrice = (HashMap<String, Object>) dataSnapshot.getValue();
                    productPrice = FireBaseAPI.productPrice;
                    populateTable(context, itemView);
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

    private static void populateTable(Context context, View itemView) {
        TableLayout tableLayout = itemView.findViewById(R.id.table_layout);
        tableLayout.removeAllViews();
        for (String prodKey : productPrice.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(context);
            tr.setBackgroundColor(context.getResources().getColor(R.color.colorLightWhite));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tr.setWeightSum(2);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;


        /* Product Name --> EditText */
            TextView name = new TextView(context);
            name.setLayoutParams(params);

            name.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey.replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(name);


        /* Product Price --> EditText */
            TextView rate = new TextView(context);
            rate.setLayoutParams(params);

            rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(productPrice.get(prodKey).toString());
            rate.setGravity(Gravity.CENTER);
            rate.setInputType(InputType.TYPE_CLASS_NUMBER);
            rate.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(rate); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }
}
