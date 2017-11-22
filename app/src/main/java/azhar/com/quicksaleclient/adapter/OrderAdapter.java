package azhar.com.quicksaleclient.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.CreateOrderActivity;
import azhar.com.quicksaleclient.model.OrderModel;

/**
 * Created by azharuddin on 24/7/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context context;
    private List<OrderModel> orderList;

    public OrderAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new OrderAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderModel order = orderList.get(position);
        final HashMap<String, Object> orderMap = order.getOrderMap();

        String orderName = "Customer Name : " + orderMap.get("customer_name").toString() + "\n";

        if (orderMap.containsKey("customer_gst"))
            orderName += "Customer GST : " + orderMap.get("customer_gst").toString() + "\n";

        if (orderMap.containsKey("sales_man_name"))
            orderName += "Sales Man : " + orderMap.get("sales_man_name").toString();

        holder.orderName.setText(orderName);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        holder.orderStart.setLayoutParams(params);
        holder.orderStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderMap.get("process").toString().equalsIgnoreCase("start")) {
                    Intent editIntent = new Intent(context, CreateOrderActivity.class);
                    editIntent.putExtra("key", order.getKey());
                    editIntent.putExtra("orderMap", orderMap);
                    editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(editIntent);
                } else {
                    Toast.makeText(context, "Order is already closed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.orderClose.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView orderName;
        TextView orderStart, orderClose;

        MyViewHolder(View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.name);
            orderStart = itemView.findViewById(R.id.start);
            orderClose = itemView.findViewById(R.id.close);
        }
    }

}
