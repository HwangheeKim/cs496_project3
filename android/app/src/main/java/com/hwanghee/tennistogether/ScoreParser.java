package com.hwanghee.tennistogether;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-09.
 */

public class ScoreParser {
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
}
