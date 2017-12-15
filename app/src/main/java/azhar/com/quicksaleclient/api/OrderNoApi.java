package azhar.com.quicksaleclient.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import azhar.com.quicksaleclient.utils.Constants;

import static azhar.com.quicksaleclient.api.FireBaseAPI.ENVIRONMENT;

/**
 * Created by azharuddin on 26/11/17.
 */

@SuppressWarnings("unchecked")
public class OrderNoApi {
    public static DatabaseReference orderNoDBRef = ENVIRONMENT.child(Constants.ORDER_NO);

    public static List<Integer> orderNo = new ArrayList<>();

    public void getOrderNo() {
        orderNoDBRef.keepSynced(true);
        orderNoDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        orderNo = (List<Integer>) dataSnapshot.getValue();
                    } else {
                        orderNo.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireError", databaseError.getMessage());
            }
        });
    }
}
