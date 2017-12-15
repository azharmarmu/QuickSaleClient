package azhar.com.quicksaleclient.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.TakenActivity;
import azhar.com.quicksaleclient.model.TakenModel;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.DialogUtils;
import azhar.com.quicksaleclient.utils.Persistance;


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

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TakenModel taken = takenList.get(position);
        final HashMap<String, Object> takenMap = taken.getTakenMap();

        final List<String> salesMan = (List<String>) takenMap.get(Constants.TAKEN_SALES_MAN_NAME);

        holder.takenName.setText("");
        holder.takenName.append("Sales Man : "
                + takenMap.get(Constants.TAKEN_SALES_MAN_NAME)
                + "\n"
                + "Route : " + salesMan);

        if (takenMap.get(Constants.TAKEN_PROCESS).toString()
                .equalsIgnoreCase(Constants.START)) {
            holder.takenStart.setText(R.string._start);
        } else if (takenMap.get(Constants.TAKEN_PROCESS).toString()
                .equalsIgnoreCase(Constants.STARTED)) {
            holder.takenStart.setText(R.string._continue);
        } else {
            holder.takenStart.setVisibility(View.GONE);
            holder.takenClose.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            holder.takenClose.setText(Constants.CLOSED);
            holder.takenClose.setClickable(false);
        }

        holder.takenStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = Persistance.getUserData(Constants.MY_NAME, context);
                if (salesMan.contains(myName)) {
                    FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                    dbStore.collection(Constants.TAKEN)
                            .document(taken.getKey())
                            .update(Constants.TAKEN_PROCESS, Constants.STARTED)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context,
                                                "Sales Started!",
                                                Toast.LENGTH_SHORT).show();
                                        Intent editIntent = new Intent(context, TakenActivity.class);
                                        editIntent.putExtra(Constants.KEY, taken.getKey());
                                        editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(editIntent);
                                    }
                                }
                            });
                } else {
                    DialogUtils.appToastShort(context, "You can start this route");
                }
            }
        });

        holder.takenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                dbStore.collection(Constants.TAKEN)
                        .document(taken.getKey())
                        .update(Constants.TAKEN_PROCESS, Constants.CLOSED)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context,
                                            "Sales Closed!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
