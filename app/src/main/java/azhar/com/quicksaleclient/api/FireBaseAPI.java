package azhar.com.quicksaleclient.api;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import azhar.com.quicksaleclient.utils.Constants;


/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class FireBaseAPI {

    static final DatabaseReference ENVIRONMENT = Constants.DATABASE.getReference(Constants.ENV);
    
    public static DatabaseReference orderDBRef = ENVIRONMENT.child(Constants.ADMIN_ORDER);
    public static DatabaseReference billingDBREf = ENVIRONMENT.child(Constants.SALES_MAN_BILLING);

    public static HashMap<String, Object> order = new HashMap<>();
    public static HashMap<String, Object> billing = new HashMap<>();
}
