package azhar.com.quicksaleclient.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import azhar.com.quicksaleclient.utils.Constants;

import static azhar.com.quicksaleclient.api.FireBaseAPI.ENVIRONMENT;


/**
 * Created by azharuddin on 22/11/17.
 */

@SuppressWarnings("unchecked")
public class SalesManApi {
    public static DatabaseReference salesManDBRef = ENVIRONMENT.child(Constants.SALES_MAN);
    public static HashMap<String, Object> salesMan = new HashMap<>();

    public void getSalesMan() {
        salesManDBRef.keepSynced(true);
        salesManDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        salesMan = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        salesMan.clear();
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
