package azhar.com.quicksaleclient.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.FireBaseAPI;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.model.SalesManModel;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManAdapter extends RecyclerView.Adapter<SalesManAdapter.MyViewHolder> {

    private Context context;
    private List<SalesManModel> salesManList;
    private String viewInfo;
    private static List<String> selectedSalesMan = new ArrayList<>();

    public SalesManAdapter(Context context, List<SalesManModel> salesManList, String viewInfo) {
        selectedSalesMan.clear();
        this.context = context;
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

        if (viewInfo.equals(Constants.EDIT)) {
            holder.salesEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog(men.getName(), men.getPhone(), position);
                }
            });

            holder.salesDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FireBaseAPI.salesManDBRef.child(men.getName()).removeValue();
                    salesManList.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else if (viewInfo.equals(Constants.CHECK)) {
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

    @SuppressLint("InflateParams")
    private void alertDialog(final String originalName, final String originalPhone, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final HashMap<String, Object> salesMan = new HashMap<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_sales_man, null);
        dialogBuilder.setView(dialogView);

        final EditText name = dialogView.findViewById(R.id.et_sales_man_name);
        final EditText phone = dialogView.findViewById(R.id.et_sales_man_phone);

        name.setText(originalName);
        phone.setText(originalPhone);

        dialogBuilder.setTitle("Details");
        dialogBuilder.setMessage("Edit Sales Man");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String SalesManName = name.getText().toString();
                String SalesManPhone = phone.getText().toString();
                if (!SalesManName.isEmpty() && !SalesManPhone.isEmpty()) {
                    FireBaseAPI.salesManDBRef.child(originalName).removeValue();
                    salesManList.remove(position);
                    salesManList.add(new SalesManModel(SalesManName, SalesManPhone));
                    salesMan.put(SalesManName, SalesManPhone);
                    FireBaseAPI.salesManDBRef.updateChildren(salesMan);
                    notifyDataSetChanged();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
