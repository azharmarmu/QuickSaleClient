package azhar.com.quicksaleclient.api;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import azhar.com.quicksaleclient.utils.Constants;

import static azhar.com.quicksaleclient.api.FireBaseAPI.ENVIRONMENT;

/**
 * Created by azharuddin on 22/11/17.
 */

@SuppressWarnings("unchecked")
public class TakenApi {
    public static DatabaseReference takenDBRef = ENVIRONMENT.child(Constants.ADMIN_TAKEN);
    public static HashMap<String, Object> taken = new HashMap<>();
}
