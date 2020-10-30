package kz.adamant.studenttimetable;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String getFormattedDate(Date date, boolean withDayDiff) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy, EEEE");
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        Calendar toDiff = Calendar.getInstance();
        toDiff.setTime(date);
        toDiff.set(Calendar.HOUR, 0);
        toDiff.set(Calendar.MINUTE, 0);
        toDiff.set(Calendar.SECOND, 0);
        toDiff.set(Calendar.MILLISECOND, 0);
        long dayDiff = getDifferenceDays(now.getTime(), toDiff.getTime());
        Log.d("TAG", "getFormattedDate: " + dayDiff);
        String dayDiffString;
        if (dayDiff > 1) {
            dayDiffString = dayDiff + " days left";
        }
        else if (dayDiff < -1) {
            dayDiffString = Math.abs(dayDiff) + " days passed";
        }
        else if (dayDiff == 0){
            dayDiffString = "Today";
        }
        else if (dayDiff == 1) {
            dayDiffString = "Tomorrow";
        }
        else  {
            dayDiffString = "Yesterday";
        }
        String formattedDate = df.format(date) + ", " + dayDiffString;
        return formattedDate;
    }
    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
