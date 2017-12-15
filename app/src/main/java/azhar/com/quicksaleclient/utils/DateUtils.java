package azhar.com.quicksaleclient.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.listeners.DateListener;

/**
 * Created by azharuddin on 6/12/17.
 */

public class DateUtils {
    public void currentDate(TextView datePicker) throws ParseException {
        Date currentDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String pYear = String.valueOf(year);
        String pMonth = (month) > 9
                ? String.valueOf(month)
                : "0" + (month);
        String pDay = day > 9
                ? String.valueOf(day)
                : "0" + day;
        datePicker.setText("");
        datePicker.append(pDay + "/" + pMonth + "/" + pYear);

    }

    @SuppressLint("SimpleDateFormat")
    public void datePicker(final Activity activity, final TextView datePicker, final String isFrom) {
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //picked date
                                String pYear = String.valueOf(year);
                                String pMonth = (monthOfYear + 1) > 9
                                        ? String.valueOf(monthOfYear + 1)
                                        : "0" + (monthOfYear + 1);
                                String pDay = dayOfMonth > 9
                                        ? String.valueOf(dayOfMonth)
                                        : "0" + dayOfMonth;

                                //current date
                                String cYear = String.valueOf(mYear);
                                String cMonth = (mMonth + 1) > 9
                                        ? String.valueOf(mMonth + 1)
                                        : "0" + (mMonth + 1);
                                String cDay = mDay > 9
                                        ? String.valueOf(mDay)
                                        : "0" + mDay;

                                SimpleDateFormat formatter = new SimpleDateFormat(activity.getString(R.string.dateformat));
                                try {
                                    Date pickedDate = formatter.parse((pDay + "-" + pMonth + "-" + pYear));
                                    Date currentDate = formatter.parse((cDay + "-" + cMonth + "-" + cYear));
                                    if (pickedDate.compareTo(currentDate) <= 0) {
                                        datePicker.setText("");
                                        datePicker.append(pDay + "/" + pMonth + "/" + pYear);
                                        datePicker.clearFocus();

                                        //set data to an listener
                                        dateListener.getDate(datePicker.getText().toString());

                                    } else {
                                        datePicker.setError("Choose Valid date");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private static DateListener dateListener;

    public void dateListener(DateListener dateListener) {
        DateUtils.dateListener = dateListener;
    }
}
