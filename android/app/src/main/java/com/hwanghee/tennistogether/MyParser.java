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
        char[] charDate = new char [10];
        playtime.getChars(0, 10, charDate, 0);
        String strDate = "";
        strDate = new String(charDate, 0, charDate.length);

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
