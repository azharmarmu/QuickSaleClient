package azhar.com.quicksaleclient.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import azhar.com.quicksaleclient.R;


/**
 * Created by azharuddin on 04/08/17.
 * Dialog utils for showing progress dialog, Toast, SnackBar etc...
 */

@SuppressWarnings("deprecation")
public class DialogUtils {
    private static ProgressDialog mProgressDialog;

    private static void appDialog(final Activity activity, String title, String Message) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(title);
        alertbox.setMessage(Message);
        alertbox.setPositiveButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        alertbox.show();
    }

    public static void appDialogWithCallBack(final Activity activity, String title, String Message, final OnClickListener listener) {
        onClickListener = listener;
        AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(title);
        alertbox.setMessage(Message);
        alertbox.setPositiveButton("Logout", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onClickListener.onOk();
                    }
                });
        alertbox.setNegativeButton("Cancel", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        onClickListener.onCancel();
                        dialog.dismiss();
                    }
                });

        alertbox.show();
    }

    private static OnClickListener onClickListener;

    interface OnClickListener {
        public void onOk();

        public void onCancel();
    }

    public static void appDialog(final Activity activity, String Message) {
        appDialog(activity, "", Message);
    }

    public static void appToastLong(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_LONG).show();
    }

    public static void appToastShort(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }

    public static void showProgressDialog(Context context, String message) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing())
                mProgressDialog.cancel();
        }
    }

    public static void appSnakeBar(final Activity activity, String message) {
        Snackbar sb = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        sb.getView().setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
        sb.show();
    }

    interface DelayCallback {
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }

}
