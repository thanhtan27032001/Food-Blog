package com.example.foodblog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFormat {
    public static String getPublicDate(String date, CharSequence minute, CharSequence minutes,
                                       CharSequence hour, CharSequence hours, CharSequence yesterday){
        Date now = new Date();
        Date realDate;
        try {
            realDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(date.substring(0, 19));
        }
        catch (Exception e){
            realDate = null;
        }
        if(realDate != null){
            if (realDate.getYear() == now.getYear() && realDate.getMonth() == now.getMonth()){
                // Hôm nay
                if (realDate.getDate() == now.getDate()){
                    // vài phút trước
                    if(realDate.getHours() == now.getHours()){
                        return now.getMinutes() - realDate.getMinutes() > 1
                                ? now.getMinutes() - realDate.getMinutes() + " " + minutes
                                : now.getMinutes() - realDate.getMinutes() + " " + minute;
                    }
                    // vài giờ trước
                    else {
                        return now.getHours() - realDate.getHours() > 1
                                ? now.getHours() - realDate.getHours() + " " + hours
                                : now.getHours() - realDate.getHours() + " " + hour;
                    }
                }
                // Hôm qua
                else if(realDate.getDate() == now.getDate()-1){
                    return "" + yesterday;
                }
            }
        }
        return realDate != null ? new SimpleDateFormat("dd/MM/yyyy").format(realDate) : "";
    }
}
