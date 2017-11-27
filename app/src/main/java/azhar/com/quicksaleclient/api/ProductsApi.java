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
public class ProductsApi {
    public static DatabaseReference productDBRef = ENVIRONMENT.child(Constants.ADMIN_PRODUCT_PRICE);
    public static DatabaseReference productHsnDBRef = Constants.DATABASE.getReference(Constants.ADMIN_PRODUCT_HSN);
    public static HashMap<String, Object> productPrice = new HashMap<>();
    public static HashMap<String, Object> productHSN = new HashMap<>();

    public void getProductPrice() {
        productDBRef.keepSynced(true);
        productDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        productPrice = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        productPrice.clear();
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

    public void getProductHSN() {
        productHsnDBRef.keepSynced(true);
        productHsnDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        productHSN = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        productHSN.clear();
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
