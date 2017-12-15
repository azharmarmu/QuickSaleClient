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

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.CreateOrderActivity;
import azhar.com.quicksaleclient.model.OrderModel;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DialogUtils;

/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
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
        HashMap<String, Object> customer = (HashMap<String, Object>) orderMap.get(Constants.ORDER_CUSTOMER);

        String orderName = "Customer Name : "
                + customer.get(Constants.CUSTOMER_NAME).toString() + "\n";

        if (customer.containsKey(Constants.CUSTOMER_GST))
            orderName += "Customer GST : "
                    + customer.get(Constants.CUSTOMER_GST).toString() + "\n";

        orderName += "Sales Man : "
                + orderMap.get(Constants.ORDER_SALES_MAN_NAME).toString();

        holder.orderName.setText(orderName);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        holder.orderStart.setLayoutParams(params);
        holder.orderStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderMap.get(Constants.ORDER_PROCESS).toString()
                        .equalsIgnoreCase(Constants.START)) {
                    Intent editIntent = new Intent(context, CreateOrderActivity.class);
                    editIntent.putExtra(Constants.KEY, order.getKey());
                    editIntent.putExtra(Constants.ORDER, orderMap);
                    editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(editIntent);
                } else {
                    DialogUtils.appToastShort(context, "Order is already closed");
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
