package marmu.com.quicksaleclient.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import marmu.com.quicksaleclient.R;
import marmu.com.quicksaleclient.api.FireBaseAPI;
import marmu.com.quicksaleclient.utils.Constants;
import marmu.com.quicksaleclient.utils.DialogUtils;
import marmu.com.quicksaleclient.utils.Permissions;
import marmu.com.quicksaleclient.model.SalesManModel;
import marmu.com.quicksaleclient.sms.SMSListener;
import marmu.com.quicksaleclient.sms.SMSReceiver;
import marmu.com.quicksaleclient.utils.Persistance;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {
    List<SalesManModel> salesManList = new ArrayList<>();
    EditText etName, etPhone, etOTP;

    String phoneVerificationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Permissions.SMS(LoginActivity.this);

        etName = (EditText) findViewById(R.id.et_sales_man_name);
        etPhone = (EditText) findViewById(R.id.et_sales_man_phone);
        etOTP = (EditText) findViewById(R.id.et_sales_man_otp);

        //Listening for OTP
        SMSReceiver.bindListener(new SMSListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.d("Text", messageText);
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                etOTP.setText(messageText);
                String code = etOTP.getText().toString();
                etOTP.setSelection(code.length());
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationID, code);
                signInWithPhoneAuthCredential(credential, name, phone);
            }
        });

    }

    private void changeMapToList() {
        HashMap<String, Object> salesMan = FireBaseAPI.salesMan;
        salesManList.clear();

        for (String key : salesMan.keySet()) {
            salesManList.add(new SalesManModel(key, (String) salesMan.get(key)));
        }
    }

    public void login(View view) {
        if (Permissions.SMS(LoginActivity.this)) {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String otp = etOTP.getText().toString();

            if (!otp.equals("")) {
                DialogUtils.showProgressDialog(LoginActivity.this, "Loading...");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationID, otp);
                signInWithPhoneAuthCredential(credential, name, phone);
            } else {
                changeMapToList();

                boolean isUserExists = false;

                for (int i = 0; i < salesManList.size(); i++) {
                    SalesManModel model = salesManList.get(i);
                    if (model.getName().equalsIgnoreCase(name) && model.getPhone().equalsIgnoreCase(phone)) {
                        isUserExists = true;
                        break;
                    } else {
                        isUserExists = false;
                    }
                }

                if (phone.length() == 10) {
                    if (isUserExists) {
                        DialogUtils.showProgressDialog(LoginActivity.this, "Loading...");
                        phoneNumberVerification(name, phone);
                    } else {
                        Toast.makeText(getApplicationContext(), "Not an validate sales man", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    etPhone.setError("Invalid number");
                    etPhone.requestFocus();
                }
            }
        }
    }

    public void phoneNumberVerification(final String name, final String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        //Instant verification or Auto-retrieval.
                        Log.d("Success", "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential, name, phoneNumber);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("Failed", "onVerificationFailed", e);

                        DialogUtils.dismissProgressDialog();

                        etName.setVisibility(View.VISIBLE);
                        etPhone.setVisibility(View.VISIBLE);
                        etOTP.setVisibility(View.GONE);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(getApplicationContext(), "Invalid request", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getApplicationContext(),
                                    "The SMS quota for the project has been exceeded",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d("OTP Code", "onCodeSent:" + verificationId);

                        DialogUtils.dismissProgressDialog();

                        etName.setVisibility(View.GONE);
                        etPhone.setVisibility(View.GONE);
                        etOTP.setVisibility(View.VISIBLE);

                        phoneVerificationID = verificationId;
                    }


                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential, final String name, final String phoneNumber) throws NullPointerException {
        Constants.AUTH.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener((new OnCompleteListener<AuthResult>() {
                    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored", "ConstantConditions"})
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        DialogUtils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Persistance.saveUserData(Constants.MY_NAME, name, LoginActivity.this);
                            Persistance.saveUserData(Constants.MY_PHONE, phoneNumber, LoginActivity.this);
                            Log.d("Success", "signInWithCredential:success");
                            Intent landingActivity = new Intent(LoginActivity.this, LandingActivity.class);
                            landingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(landingActivity);
                            finish();
                        } else {
                            Log.w("Failed", "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),
                                        "Verification code is wrong",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }));
    }
}