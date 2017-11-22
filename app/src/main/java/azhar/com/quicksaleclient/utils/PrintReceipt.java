package azhar.com.quicksaleclient.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.DecimalFormat;
import java.util.HashMap;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.activity.PrintActivity;


/**
 * This class is responsible to generate a static sales receipt and to print that receipt
 */
@SuppressWarnings("unchecked")
public class PrintReceipt {

    public static boolean printBill(Context context, HashMap<String, Object> soldOrders, Bitmap bitmap) {
        if (PrintActivity.BLUETOOTH_PRINTER.IsNoConnection()) {
            return false;
        }

        //normal font 0x00
        //bold font 0x08
        //bold with medium text 0x20
        //bold with large text 0x10

        //LF = Line feed
        PrintActivity.BLUETOOTH_PRINTER.Begin();
        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        PrintActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);    //30 * 0.125mm
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x10);//bold with large text


        //BT_Write() method will initiate the printer to start printing.
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("* GST Invoice *");

        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Mubarak Traders"); // company name
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("\n#12/2, Ground floor, " +
                "\nSuper saw mill compound," +
                "\nAMC road, KG Halli," +
                "\nBangalore-45"); // address
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nMobile : 9449802606"); // address
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nGSTIN : 29ABDFM2567J1ZW\n"); // GST


        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);
        PrintActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);

        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);
        int paperTotal = 21;
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Bill No: " + soldOrders.get("bill_no"));
        int billLength = 9 + soldOrders.get("bill_no").toString().length();
        paperTotal = paperTotal - billLength;

        while (paperTotal >= 0) {
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
            paperTotal--;
        }

        PrintActivity.BLUETOOTH_PRINTER.BT_Write(String.valueOf(soldOrders.get("sold_date")));

        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_cust));

        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nName    : " + soldOrders.get("customer_name"));

        if (soldOrders.containsKey("customer_gst"))
            PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nGSTIN   : " + soldOrders.get("customer_gst"));
        else
            PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nGSTIN   : " + "Un-reg");

        if (soldOrders.containsKey("customer_address"))
            PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nAddress : " + soldOrders.get("customer_address"));
        else
            PrintActivity.BLUETOOTH_PRINTER.BT_Write("\nAddress : Bangalore");


        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
        PrintActivity.BLUETOOTH_PRINTER.LF();

        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
        PrintActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);    //50 * 0.125mm
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font

        //header
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Items(HSN)  ");
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Price  ");
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("QTY  ");
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("  Total\n");
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));

        HashMap<String, Object> productQTY = (HashMap<String, Object>) soldOrders.get("sold_items");
        HashMap<String, Object> productRate = (HashMap<String, Object>) soldOrders.get("sold_items_rate");
        HashMap<String, Object> productHSN = (HashMap<String, Object>) soldOrders.get("sold_items_hsn");
        HashMap<String, Object> productTotal = (HashMap<String, Object>) soldOrders.get("sold_items_total");


        for (String prodKey : productQTY.keySet()) {

            int wholeTotalLength = 5;
            int itemLength = prodKey.length();
            int remLength = wholeTotalLength - itemLength;
            PrintActivity.BLUETOOTH_PRINTER.BT_Write("\n" + prodKey + "(" + productHSN.get(prodKey) + ")");
            while (remLength >= 0) {
                PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
                remLength--;
            }

            wholeTotalLength = 4;
            int priceLength = productRate.get(prodKey).toString().length();
            remLength = wholeTotalLength - priceLength;
            while (remLength >= 0) {
                PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
                remLength--;
            }
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(productRate.get(prodKey).toString());

            wholeTotalLength = 4;
            int qtyLength = productQTY.get(prodKey).toString().length();
            remLength = wholeTotalLength - qtyLength;
            while (remLength >= 0) {
                PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
                remLength--;
            }
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(productQTY.get(prodKey).toString());

            wholeTotalLength = 8;
            int totalLength = productTotal.get(prodKey).toString().length();
            remLength = wholeTotalLength - totalLength;
            while (remLength >= 0) {
                PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
                remLength--;
            }
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(productTotal.get(prodKey).toString());

        }

        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));

        DecimalFormat dformat = new DecimalFormat("#.##");
        double netTotal = Double.parseDouble(soldOrders.get("net_total").toString());
        netTotal = Double.valueOf(dformat.format(netTotal));
        double grossTotal = Double.valueOf(dformat.format(netTotal / 1.05));
        double gst = Double.valueOf(dformat.format((netTotal - Math.round(grossTotal)) / 2));
        dformat = new DecimalFormat();
        grossTotal = Math.round(grossTotal);

        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
        PrintActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);    //50 * 0.125mm
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font


        dformat.setMinimumFractionDigits(0);
        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Total Amount : " + dformat.format(netTotal) + " ");

        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


        dformat.setMinimumFractionDigits(2);
        PrintActivity.BLUETOOTH_PRINTER.LF();

        int spaceTotal = 10;
        String str_gross = dformat.format(grossTotal);
        int grossLength = str_gross.length();
        int total = spaceTotal - grossLength;
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("Gross Amount : ");
        while (total >= 0) {
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
            total--;
        }
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(str_gross + " ");
        PrintActivity.BLUETOOTH_PRINTER.LF();


        String str_gst = dformat.format(gst);
        int length = str_gst.length();
        total = spaceTotal - length;
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(" CGST @ 2.5% : ");
        while (total >= 0) {
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
            total--;
        }
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(str_gst + " ");
        PrintActivity.BLUETOOTH_PRINTER.LF();

        total = spaceTotal - length;
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(" SGST @ 2.5% : ");
        while (total >= 0) {
            PrintActivity.BLUETOOTH_PRINTER.BT_Write(" ");
            total--;
        }
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(str_gst + " ");
        PrintActivity.BLUETOOTH_PRINTER.LF();

        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
        PrintActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//Center
        PrintActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x10);//bold with large text 0x10
        PrintActivity.BLUETOOTH_PRINTER.BT_Write("\n* Thank You *\n");


        PrintActivity.BLUETOOTH_PRINTER.LF();
        PrintActivity.BLUETOOTH_PRINTER.LF();
        return true;
    }
}
