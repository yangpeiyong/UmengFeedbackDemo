package com.yangpeiyong.demo.umengfeedbackdemo;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author yangpeiyong
 */
public class PrettyDate {
    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static String toUTC(Date date){

        final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        return sdf.format(date);
    }
    public static String getPresentDate(Context context,long date){
        String format = "yyyy-MM-dd'T'HH:mm";
        return  getPresentDate(context,date,format);
    }
    public static String getPresentDate(Context context,long date,String format){
        Date currentDate = new Date(System.currentTimeMillis());
        //Calendar current = Calendar.getInstance();
        Date date1 = new Date(date);

        String str="";
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(currentDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date1);

        if(calendar1.get(Calendar.YEAR)!= calendar2.get(Calendar.YEAR)
                ||calendar1.get(Calendar.DAY_OF_MONTH)!=calendar2.get(Calendar.DAY_OF_MONTH)
                ||calendar1.get(Calendar.MONTH)!=calendar2.get(Calendar.MONTH)) {
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            str = sdf.format(date1);
        } else {



            long interval = (currentDate.getTime() - date1.getTime())/1000;
            int hour = (int)(interval/(60*60));
            int minute = ((int)(interval - hour*60*60)/60);
            int second = ((int)(interval - hour*60*60 - minute*60));

            if(hour>0){
                String formatStr = context.getString(R.string.hour_before);
                str = String.format(formatStr,hour);
            } else if(minute>0){
                String formatStr = context.getString(R.string.minute_before);
                str = String.format(formatStr,minute);
            } else if(second>0){
                String formatStr = context.getString(R.string.second_before);
                str = String.format(formatStr,second);
            } else {
                str = context.getString(R.string.current);
            }
        }

        return str;

    }

    public static String getPresentDate(Context context,String date,String format){

        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date1 = sdf.parse(date);
            return getPresentDate(context,date1.getTime(),format);
        } catch (Exception e){
            return "";
        }
    }
    public static String getPresentDate(Context context,String date){

        SimpleDateFormat dateFormat1 = new SimpleDateFormat(ISO_FORMAT);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date1 = dateFormat1.parse(date);
            return getPresentDate(context,date1.getTime());
        } catch (Exception e){
            return "";
        }
    }

    public static Date parseDate(String date){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(ISO_FORMAT);
        dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat1.parse(date);
        } catch (Exception e){
            return null;
        }
    }
}
