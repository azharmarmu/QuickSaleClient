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
import azhar.com.quicksaleclient.api.TakenApi;
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
        HashMap<String, Object> customer = (HashMap<String, Object>) soldOrders.get(Constants.CUSTOMER);
        holder.billName.setText((String) customer.get(Constants.CUSTOMER_NAME));

        holder.billStart.setText(R.string.print);
        holder.billClose.setText(R.string.delete);

        holder.billStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 30/9/17  edit has to be done
                Intent intent = new Intent(context, PrintActivity.class);
                intent.putExtra(Constants.KEY, billModel.getName());
                intent.putExtra(Constants.BILL_NO, (String) soldOrders.get(Constants.BILL_NO));
                intent.putExtra(Constants.BILL_SALES, soldOrders);
                context.startActivity(intent);
            }
        });

        holder.billClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                dbStore.collection(Constants.BILLING)
                        .document(billModel.getName())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context,
                                    "Bill deleted successfully!",
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
        HashMap<String, Object> takenMap = (HashMap<String, Object>) TakenApi.taken.get(billModel.getKey());
        HashMap<String, Object> updatedTaken = new HashMap<>();

        HashMap<String, Object> takenSales = (HashMap<String, Object>) takenMap.get(Constants.TAKEN_SALES);
        HashMap<String, Object> billSales = (HashMap<String, Object>) bill.get(Constants.BILL_SALES);

        for (String itemKey : takenSales.keySet()) {
            if (billSales.containsKey(itemKey)) {
                HashMap<String, Object> stockItems = (HashMap<String, Object>) takenSales.get(itemKey);
                HashMap<String, Object> soldItems = (HashMap<String, Object>) billSales.get(itemKey);
                int left = Integer.parseInt(stockItems.get(Constants.TAKEN_SALES_QTY_STOCK).toString());
                int sold = Integer.parseInt(soldItems.get(Constants.TAKEN_SALES_QTY).toString());
                String available = String.valueOf(left + sold);
                HashMap<String, Object> itemDetails = new HashMap<>();
                itemDetails.put(Constants.TAKEN_SALES_PRODUCT_NAME, itemKey);
                itemDetails.put(Constants.TAKEN_SALES_QTY_STOCK, available);
                itemDetails.put(Constants.TAKEN_SALES_QTY, stockItems.get(Constants.TAKEN_SALES_QTY).toString());
                updatedTaken.put(itemKey, itemDetails);
            }
        }

        //TakenApi.takenDBRef.child(billModel.getKey()).child("sales_order_qty_left").updateChildren(orders);

        FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
        dbStore.collection(Constants.TAKEN)
                .document(billModel.getKey())
                .update(Constants.TAKEN_SALES, updatedTaken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context,
                                    "Taken re-added!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

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
