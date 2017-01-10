package com.hwanghee.tennistogether;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-09.
 */

public class MyParser {
    public static String stringify(ArrayList<Integer> scores) {
        return TextUtils.join("#", scores);
    }

    public static boolean winner(String score) {
        String[] scores = TextUtils.split(score, "#");
        int balance = 0;
        if(Integer.parseInt(scores[0]) > Integer.parseInt(scores[1])) { balance += 1; }
        if(Integer.parseInt(scores[0]) < Integer.parseInt(scores[1])) { balance -= 1; }

        if(Integer.parseInt(scores[4]) > Integer.parseInt(scores[5])) { balance += 1; }
        if(Integer.parseInt(scores[4]) < Integer.parseInt(scores[5])) { balance -= 1; }

        if(Integer.parseInt(scores[8]) > Integer.parseInt(scores[9])) { balance += 1; }
        if(Integer.parseInt(scores[8]) < Integer.parseInt(scores[9])) { balance -= 1; }

        if(Integer.parseInt(scores[12]) > Integer.parseInt(scores[13])) { balance += 1; }
        if(Integer.parseInt(scores[12]) < Integer.parseInt(scores[13])) { balance -= 1; }

        if(Integer.parseInt(scores[16]) > Integer.parseInt(scores[17])) { balance += 1; }
        if(Integer.parseInt(scores[16]) < Integer.parseInt(scores[17])) { balance -= 1; }

        if(balance > 0) return true ;
        return false;
    }

    public static String getDate(String playtime) {
        // TODO : Implement this method
        // 2017-01-10T15:00:00.000Z
        String strMonth;
        char[] charMonth = new char [2];
        playtime.getChars(5, 7, charMonth, 0);

        if(charMonth[0] == '0'){
            char finedCharMonth = charMonth[1];
            strMonth = Character.toString(finedCharMonth);
        }
        else{
            strMonth = new String(charMonth, 0, charMonth.length);
        }

        String strDay;
        char[] charDay = new char [2];
        playtime.getChars(8, 10, charDay, 0);

        if(charDay[0] == '0'){
            char finedCharDay = charDay[1];
            strDay = Character.toString(finedCharDay);
        }
        else{
            strDay = new String(charDay, 0, charDay.length);
        }



        String strDate = strMonth + "월 " + strDay + "일";
        return strDate;
    }

    public static String getTime(String playtime) {

        char[] charTime = new char [5];
        playtime.getChars(11, 16, charTime, 0);
        String strTime = "";
        strTime = new String(charTime, 0, charTime.length);

        return strTime;
    }
}
