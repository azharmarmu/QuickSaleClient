package azhar.com.quicksaleclient.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mocoo.hang.rtprinter.driver.Contants;
import com.mocoo.hang.rtprinter.driver.HsBluetoothPrintDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.api.FireBaseAPI;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.PrintReceipt;

@SuppressWarnings({"unchecked", "deprecation"})
public class PrintActivity extends AppCompatActivity {

    static TextView txtPrinterStatus;
    Button mBtnConnectBluetoothDevice;

    private static final String TAG = "BloothPrinterActivity";
    private static BluetoothDevice device;
    private AlertDialog.Builder alertDlgBuilder;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter mBluetoothAdapter = null;
    public static HsBluetoothPrintDriver BLUETOOTH_PRINTER = null;

    private static Context CONTEXT;
    private TextView date, billNo, custName, custGST, netTotal, grossTotal, cgst, sgst;
    private TableLayout tableLayout;
    private RelativeLayout printLayout;

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that to be enabled.
        // initializeBluetoothDevice() will then be called during onActivityResult
        try {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            } else {
                if (BLUETOOTH_PRINTER == null) {
                    initializeBluetoothDevice();
                } else {
                    if (BLUETOOTH_PRINTER.IsNoConnection()) {
                        txtPrinterStatus.setText(R.string.no_printer_is_connected);
                    } else {
                        txtPrinterStatus.setText(R.string.title_connected_to);
                        txtPrinterStatus.append(device.getName());
                    }
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    String key, billNumber;
    HashMap<String, Object> soldOrders = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            key = bundle.getString("key");
            billNumber = bundle.getString("bill_no");
            soldOrders = (HashMap<String, Object>) bundle.getSerializable("sold_orders");

            CONTEXT = getApplicationContext();


            FireBaseAPI.billingDBREf.child(key).child(billNumber).child("amount_received").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        EditText etAmountReceived = (EditText) findViewById(R.id.et_amount_received);
                        etAmountReceived.setText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Error", databaseError.getMessage());
                }
            });


            printLayout = (RelativeLayout) findViewById(R.id.print_layout);
            date = (TextView) findViewById(R.id.date);
            billNo = (TextView) findViewById(R.id.bill);
            custName = (TextView) findViewById(R.id.cust_name);
            custGST = (TextView) findViewById(R.id.cust_gst);
            tableLayout = (TableLayout) findViewById(R.id.table_layout);
            netTotal = (TextView) findViewById(R.id.total);
            grossTotal = (TextView) findViewById(R.id.gross_total);
            cgst = (TextView) findViewById(R.id.cgst);
            sgst = (TextView) findViewById(R.id.sgst);


            populateView();

            txtPrinterStatus = (TextView) findViewById(R.id.txtPrinterStatus);
            mBtnConnectBluetoothDevice = (Button) findViewById(R.id.btn_connect_bluetooth_device);
            mBtnConnectBluetoothDevice.setOnClickListener(mBtnConnectBluetoothDeviceOnClickListener);

            alertDlgBuilder = new AlertDialog.Builder(PrintActivity.this);

            // Get device's Bluetooth adapter
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            // If the adapter is null, then Bluetooth is not available in your device
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                //finish();
            }
        }

    }

    private void populateView() {
        billNo.append(soldOrders.get("bill_no").toString());
        date.append(soldOrders.get("sold_date").toString());
        custName.append(soldOrders.get("customer_name").toString());
        if (soldOrders.containsKey("customer_gst")) {
            custGST.append(soldOrders.get("customer_gst").toString());
        } else {
            custGST.append("NIL");
        }

        populateTable();

        DecimalFormat dformat = new DecimalFormat("#.##");
        double netTot = Double.parseDouble(soldOrders.get("net_total").toString());
        netTot = Double.valueOf(dformat.format(netTot));
        double grossTot = Double.valueOf(dformat.format(netTot / 1.05));
        double gst = Double.valueOf(dformat.format((netTot - Math.round(grossTot)) / 2));
        grossTot = Math.round(grossTot);
        netTotal.append("" + netTot);
        grossTotal.append("" + grossTot);
        cgst.append("" + gst);
        sgst.append("" + gst);
    }

    private void populateTable() {
        HashMap<String, Object> prodQTY = (HashMap<String, Object>) soldOrders.get("sold_items");
        HashMap<String, Object> prodRate = (HashMap<String, Object>) soldOrders.get("sold_items_rate");
        HashMap<String, Object> prodTotal = (HashMap<String, Object>) soldOrders.get("sold_items_total");
        HashMap<String, Object> prodHSN = (HashMap<String, Object>) soldOrders.get("sold_items_hsn");

        for (String prodKey : prodQTY.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setWeightSum(4);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;


            /* Product Name --> TextView */
            TextView productName = new TextView(this);
            productName.setLayoutParams(params);

            productName.setTextColor(getResources().getColor(R.color.colorBlack));
            productName.setText(prodKey + "(" + prodHSN.get(prodKey) + ")");
            productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tr.addView(productName);

            /* Product QTY --> EditText */
            TextView productQTY = new TextView(this);
            productQTY.setLayoutParams(params);

            productQTY.setTextColor(getResources().getColor(R.color.colorBlack));
            productQTY.setText(prodQTY.get(prodKey).toString());
            productQTY.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tr.addView(productQTY);

            /* Product Rate --> EditText */
            TextView productRate = new TextView(this);
            productRate.setLayoutParams(params);

            productRate.setTextColor(getResources().getColor(R.color.colorBlack));
            productRate.setText(prodRate.get(prodKey).toString());
            productRate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tr.addView(productRate);

            TextView productTotal = new TextView(this);
            productQTY.setLayoutParams(params);

            productTotal.setTextColor(getResources().getColor(R.color.colorBlack));
            productTotal.setText(prodTotal.get(prodKey).toString());
            productTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tr.addView(productTotal);
            // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }

    private void initializeBluetoothDevice() {
        Log.d(TAG, "setupChat()");
        // Initialize HsBluetoothPrintDriver class to perform bluetooth connections
        BLUETOOTH_PRINTER = HsBluetoothPrintDriver.getInstance();//
        BLUETOOTH_PRINTER.setHandler(new BluetoothHandler(PrintActivity.this));
    }

    public void printBill(View view) {
        if (!BLUETOOTH_PRINTER.IsNoConnection()) {
            Bitmap bitmap = saveBitMap(printLayout, soldOrders.get("bill_no").toString());
            printBillBitMap(bitmap);
        } else {
            Toast.makeText(getApplicationContext(), "Connect Printer", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Bitmap saveBitMap(View drawView, String billNo) {
        File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "QS_POS");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + billNo + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        //scanGallery(context, pictureFile.getAbsolutePath());
        return bitmap;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void printBillBitMap(Bitmap bitmap) {
        PrintReceipt.printBill(PrintActivity.this, soldOrders, bitmap);
    }

    public void amountReceived(View view) {
        EditText etAmountReceived = findViewById(R.id.et_amount_received);
        int amountReceived;
        if (etAmountReceived.getText().toString().isEmpty())
            amountReceived = 0;
        else {
            amountReceived = Integer.parseInt(etAmountReceived.getText().toString());
        }

        FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
        dbStore.collection(Constants.SALES_MAN_BILLING)
                .document(key)
                .update("amount_received", amountReceived)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PrintActivity.this,
                                    "Amount added successfully!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

    private static class BluetoothHandler extends Handler {
        private final WeakReference<PrintActivity> myWeakReference;

        //Creating weak reference of BluetoothPrinterActivity class to avoid any leak
        BluetoothHandler(PrintActivity weakReference) {
            myWeakReference = new WeakReference<>(weakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            PrintActivity bluetoothPrinterActivity = myWeakReference.get();
            if (bluetoothPrinterActivity != null) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                switch (data.getInt("flag")) {
                    case Contants.FLAG_STATE_CHANGE:
                        int state = data.getInt("state");
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + state);
                        switch (state) {
                            case HsBluetoothPrintDriver.CONNECTED_BY_BLUETOOTH:
                                txtPrinterStatus.setText(R.string.title_connected_to);
                                txtPrinterStatus.append(device.getName());
                                Constants.isPrinterConnected = true;
                                Toast.makeText(CONTEXT, "Connection successful.", Toast.LENGTH_SHORT).show();
                                break;
                            case HsBluetoothPrintDriver.FLAG_SUCCESS_CONNECT:
                                txtPrinterStatus.setText(R.string.title_connecting);
                                break;

                            case HsBluetoothPrintDriver.UNCONNECTED:
                                txtPrinterStatus.setText(R.string.no_printer_connected);
                                break;
                        }
                        break;
                    case Contants.FLAG_SUCCESS_CONNECT:
                        txtPrinterStatus.setText(R.string.title_connecting);
                        break;
                    case Contants.FLAG_FAIL_CONNECT:
                        Toast.makeText(CONTEXT, "Connection failed.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
            }
        }
    }

    View.OnClickListener mBtnConnectBluetoothDeviceOnClickListener = new View.OnClickListener() {
        Intent serverIntent = null;

        public void onClick(View arg0) {

            try {
                //If bluetooth is disabled then ask user to enable it again
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                } else {//If the connection is lost with last connected bluetooth printer
                    if (BLUETOOTH_PRINTER.IsNoConnection()) {
                        serverIntent = new Intent(PrintActivity.this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    } else { //If an existing connection is still alive then ask user to kill it and re-connect again
                        alertDlgBuilder.setTitle(getResources().getString(R.string.alert_title));
                        alertDlgBuilder.setMessage(getResources().getString(R.string.alert_message));
                        alertDlgBuilder.setNegativeButton(getResources().getString(R.string.alert_btn_negative), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }
                        );
                        alertDlgBuilder.setPositiveButton(getResources().getString(R.string.alert_btn_positive), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BLUETOOTH_PRINTER.stop();
                                        serverIntent = new Intent(PrintActivity.this, DeviceListActivity.class);
                                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                                    }
                                }
                        );
                        alertDlgBuilder.show();

                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    BLUETOOTH_PRINTER.start();
                    BLUETOOTH_PRINTER.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    initializeBluetoothDevice();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (BLUETOOTH_PRINTER.IsNoConnection())
                BLUETOOTH_PRINTER.stop();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
