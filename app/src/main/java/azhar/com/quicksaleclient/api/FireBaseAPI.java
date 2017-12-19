package azhar.com.quicksaleclient.api;

import com.google.firebase.database.DatabaseReference;

import azhar.com.quicksaleclient.utils.Constants;


/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class FireBaseAPI {
    static final DatabaseReference ENVIRONMENT = Constants.DATABASE.getReference(Constants.ENV);
}
