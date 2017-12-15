package azhar.com.quicksaleclient.utils;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by azharuddin on 26/5/17.
 */

public class Constants {
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static final String ENV = "development";
    //public static final String ENV = "production";

    public static final String KEY = "key";

    /*Table Name*/
    public static final String USERS = "users";
    public static final String TAKEN = "taken";
    public static final String ORDER = "order";
    public static final String PRODUCTS = "products";
    public static final String SALES_MAN = "sales_man";
    public static final String CUSTOMER = "customer";
    public static final String BILLING = "billing";

    public static final String EDIT = "edit";
    public static final String CHECK = "check";

    public static final String MY_NAME = "my_name";
    public static final String MY_PHONE = "my_phone";

    public static final int SALES_MAN_CODE = 123;
    public static boolean isPrinterConnected = false;

    /*Taken -> process*/
    public static final String CLOSED = "close";
    public static final String START = "start";
    public static final String STARTED = "started";

    /*SalesMan-Table*/
    public static final String SALES_MAN_NAME = "sales_man_name";
    public static final String SALES_MAN_PHONE = "sales_man_phone";

    /*Taken-Table */
    public static final String TAKEN_ID = "_id";
    public static final String TAKEN_PROCESS = "process";
    public static final String TAKEN_DATE = "date";
    public static final String TAKEN_SALES = "sales";
    public static final String TAKEN_SALES_MAN_NAME = "salesManName";
    public static final String TAKEN_SALES_QTY = "qty";
    public static final String TAKEN_SALES_QTY_STOCK = "qtyStock";
    public static final String TAKEN_SALES_PRODUCT_NAME = "name";
    public static final String TAKEN_ROUTE = "route";

    /*Products-Table*/
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_HSN = "hsn";
    public static final String PRODUCT_RATE = "rate";

    /*Customer-Table*/
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_GST = "customer_gst";

    /*Billing-Table store*/
    public static final String BILL_ID = "_id";
    public static final String BILL_DATE = "date";
    public static final String BILL_NO = "billNo";
    public static final String BILL_SALES_MAN_NAME = "salesManName";
    public static final String BILL_CUSTOMER = "customer";
    public static final String BILL_SALES = "sales";
    public static final String BILL_SALES_PRODUCT_NAME = "name";
    public static final String BILL_SALES_PRODUCT_QTY = "qty";
    public static final String BILL_SALES_PRODUCT_RATE = "rate";
    public static final String BILL_SALES_PRODUCT_HSN = "hsn";
    public static final String BILL_SALES_PRODUCT_TOTAL = "total";
    public static final String BILL_NET_TOTAL = "netTotal";
    public static final String BILL_ROUTE = "route";
    public static final String BILL_AMOUNT_RECEIVED = "amountReceived";

    /*Order-Table store*/
    public static final String ORDER_ID = "_id";
    public static final String ORDER_PROCESS = "process";
    public static final String ORDER_DATE = "date";
    public static final String ORDER_NO = "orderNo";
    public static final String ORDER_SALES_MAN_NAME = "salesManName";
    public static final String ORDER_CUSTOMER = "customer";
    public static final String ORDER_CUSTOMER_NAME = "customer_name";
    public static final String ORDER_CUSTOMER_GST = "customer_gst";
    public static final String ORDER_SALES = "sales";
    public static final String ORDER_SALES_PRODUCT_NAME = "name";
    public static final String ORDER_SALES_PRODUCT_QTY = "qty";
    public static final String ORDER_SALES_PRODUCT_RATE = "rate";
    public static final String ORDER_SALES_PRODUCT_HSN = "hsn";
    public static final String ORDER_SALES_PRODUCT_TOTAL = "total";

    @SuppressLint("SimpleDateFormat")
    public static String currentDate() throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy")
                .format(new Date(System.currentTimeMillis()));
    }
}
