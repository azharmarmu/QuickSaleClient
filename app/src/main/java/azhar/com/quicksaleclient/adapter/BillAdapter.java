package azhar.com.quicksaleclient.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.PrintActivity;
import azhar.com.quicksaleclient.api.FireBaseAPI;
import azhar.com.quicksaleclient.model.BillModel;
import azhar.com.quicksaleclient.utils.Constants;


/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class BillAdapter extends RecyclerView.Adapter<BillAdapter.MyViewHolder> {

    private Context context;
    private List<BillModel> billList;

    public BillAdapter(Context context, List<BillModel> billList) {
        this.context = context;
        this.billList = billList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new BillAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BillModel billModel = billList.get(position);
        final HashMap<String, Object> soldOrders = billModel.getBillMap();
        holder.billName.setText((String) soldOrders.get("customer_name"));

        holder.billStart.setText("Print");
        holder.billClose.setText("Delete");

        holder.billStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 30/9/17  edit has to be done
                Intent intent = new Intent(context, PrintActivity.class);
                intent.putExtra("key", billModel.getName());
                intent.putExtra("bill_no", (String) soldOrders.get("bill_no"));
                intent.putExtra("sold_orders", soldOrders);
                context.startActivity(intent);
            }
        });

        holder.billClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                dbStore.collection(Constants.SALES_MAN_BILLING)
                        .document(billModel.getName())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context,
                                    "Amount added successfully!",
                                    Toast.LENGTH_SHORT).show();
                            billList.remove(position);
                            re_AddItemsToStock(billModel);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }


    private void re_AddItemsToStock(BillModel billModel) {
        HashMap<String, Object> bill = billModel.getBillMap();
        HashMap<String, Object> itemStock = (HashMap<String, Object>) bill.get("sold_items");
        HashMap<String, Object> takenMap = (HashMap<String, Object>) FireBaseAPI.taken.get(billModel.getKey());
        HashMap<String, Object> orders = new HashMap<>();
        HashMap<String, Object> qtyLeft = (HashMap<String, Object>) takenMap.get("sales_order_qty_left");

        for (String itemKey : itemStock.keySet()) {
                int itemVal = Integer.parseInt(itemStock.get(itemKey).toString());
                int qtyVal = Integer.parseInt(qtyLeft.get(itemKey).toString());

            orders.put(itemKey, String.valueOf(itemVal + qtyVal));
        }

        FireBaseAPI.takenDBRef.child(billModel.getKey()).child("sales_order_qty_left").updateChildren(orders);
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView billName, billStart, billClose;

        MyViewHolder(View itemView) {
            super(itemView);
            billName = itemView.findViewById(R.id.name);
            billStart = itemView.findViewById(R.id.start);
            billClose = itemView.findViewById(R.id.close);
        }
    }
}
