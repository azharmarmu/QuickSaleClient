package azhar.com.quicksaleclient.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by azharuddin on 5/12/17.
 */

public class SoftInputUtil {
    public void hideSoftInput(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }

    }
}
