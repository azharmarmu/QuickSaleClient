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
import azhar.com.quicksaleclient.activity.TakenActivity;
import azhar.com.quicksaleclient.api.TakenApi;
import azhar.com.quicksaleclient.model.TakenModel;


/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class TakenAdapter extends RecyclerView.Adapter<TakenAdapter.MyViewHolder> {

    private Context context;
    private List<TakenModel> takenList;

    public TakenAdapter(Context context, List<TakenModel> takenList) {
        this.context = context;
        this.takenList = takenList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new TakenAdapter.MyViewHolder(itemView);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TakenModel taken = takenList.get(position);
        final HashMap<String, Object> takenMap = taken.getTakenMap();
        holder.takenName.setText("");
        holder.takenName.append(takenMap.get("sales_man_name").toString() +
                " / " +
                takenMap.get("sales_route").toString());

        if (takenMap.get("process").toString().equalsIgnoreCase("start")) {
            holder.takenStart.setText("Start");
        } else if (takenMap.get("process").toString().equalsIgnoreCase("started")) {
            holder.takenStart.setText("Continue");
        } else {
            holder.takenStart.setVisibility(View.GONE);
            holder.takenClose.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            holder.takenClose.setText("Closed");
            holder.takenClose.setClickable(false);
        }

        holder.takenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TakenApi.takenDBRef.child(taken.getKey()).child("process").setValue("started");

                Intent editIntent = new Intent(context, TakenActivity.class);
                editIntent.putExtra("key", taken.getKey());
                editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(editIntent);
            }
        });

        holder.takenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakenApi.takenDBRef.child(taken.getKey()).child("process").setValue("close");
            }
        });

    }

    @Override
    public int getItemCount() {
        return takenList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView takenName, takenStart, takenClose;

        MyViewHolder(View itemView) {
            super(itemView);
            takenName = itemView.findViewById(R.id.name);
            takenStart = itemView.findViewById(R.id.start);
            takenClose = itemView.findViewById(R.id.close);
        }
    }

}
