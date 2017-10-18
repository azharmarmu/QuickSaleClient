package marmu.com.quicksaleclient.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksaleclient.utils.Constants;


/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class FireBaseAPI {


    public static DatabaseReference salesManDBRef = Constants.DATABASE.getReference(Constants.SALES_MAN);
    public static DatabaseReference customerDBRef = Constants.DATABASE.getReference(Constants.CUSTOMER);
    public static DatabaseReference takenDBRef = Constants.DATABASE.getReference(Constants.ADMIN_TAKEN);
    public static DatabaseReference productDBRef = Constants.DATABASE.getReference(Constants.ADMIN_PRODUCT_PRICE);
    public static DatabaseReference productHsnDBRef = Constants.DATABASE.getReference(Constants.ADMIN_PRODUCT_HSN);
    public static DatabaseReference orderDBRef = Constants.DATABASE.getReference(Constants.ADMIN_ORDER);
    public static DatabaseReference billingDBREf = Constants.DATABASE.getReference(Constants.SALES_MAN_BILLING);
    public static DatabaseReference usersDBRef = Constants.DATABASE.getReference(Constants.USERS);
    public static DatabaseReference billNoDBRef = Constants.DATABASE.getReference(Constants.BILL_NO);
    public static HashMap<String, Object> salesMan = new HashMap<>();
    public static HashMap<String, Object> customer = new HashMap<>();
    public static HashMap<String, Object> taken = new HashMap<>();
    public static HashMap<String, Object> productPrice = new HashMap<>();
    public static HashMap<String, Object> productHSN = new HashMap<>();
    public static HashMap<String, Object> order = new HashMap<>();
    public static HashMap<String, Object> billing = new HashMap<>();
    public static HashMap<String, Object> users = new HashMap<>();
    public static List<Integer> billNo = new ArrayList<>();

    public static void getSalesMan() {
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

    public static void getCustomer() {
        customerDBRef.keepSynced(true);
        customerDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        customer = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        customer.clear();
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

    public static void getUsers() {
        usersDBRef.keepSynced(true);
        usersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        users = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        users.clear();
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

    public static void getBillNo() {
        billNoDBRef.keepSynced(true);
        billNoDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        billNo = (List<Integer>) dataSnapshot.getValue();
                    } else {
                        billNo.clear();
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

    public static void getProductPrice() {
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

    public static void getProductHSN() {
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
