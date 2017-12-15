package azhar.com.quicksaleclient.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.model.SalesManModel;
import azhar.com.quicksaleclient.utils.Constants;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManAdapter extends RecyclerView.Adapter<SalesManAdapter.MyViewHolder> {

    private List<SalesManModel> salesManList;
    private String viewInfo;
    private static List<String> selectedSalesMan = new ArrayList<>();

    public SalesManAdapter(List<SalesManModel> salesManList, String viewInfo) {
        selectedSalesMan.clear();
        this.salesManList = salesManList;
        this.viewInfo = viewInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;

        if (viewInfo.equals(Constants.EDIT)) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_sales_man_edit,
                            parent, false);
        } else if (viewInfo.equals(Constants.CHECK)) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_sales_man_check,
                            parent, false);
        }

        return new SalesManAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SalesManModel men = salesManList.get(position);
        holder.salesManName.setText(men.getName());

        if (viewInfo.equals(Constants.CHECK)) {
            holder.salesManCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        selectedSalesMan.add(men.getName());
                    } else {
                        selectedSalesMan.remove(men.getName());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return salesManList.size();
    }

    public static List<String> getSelectedSalesMan() {
        return selectedSalesMan;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout salesManDetails;
        TextView salesManName;
        CheckBox salesManCheck;
        TextView salesEdit, salesDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            salesManDetails = itemView.findViewById(R.id.sales_man_details);
            salesManName = itemView.findViewById(R.id.sales_man_name);
            if (viewInfo.equals(Constants.EDIT)) {
                salesEdit = itemView.findViewById(R.id.sales_man_edit);
                salesDelete = itemView.findViewById(R.id.sales_man_delete);
            } else if (viewInfo.equals(Constants.CHECK)) {
                salesManCheck = itemView.findViewById(R.id.sales_man_check);
            }
        }
    }
}
