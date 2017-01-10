package com.hwanghee.tennistogether;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-09.
 */

public class GameResultDialog extends DialogFragment {
    View rootView;
    int[] ids = {
            R.id.gameresult_set1_a, R.id.gameresult_set1_b, R.id.gameresult_set1_tiea, R.id.gameresult_set1_tieb,
            R.id.gameresult_set2_a, R.id.gameresult_set2_b, R.id.gameresult_set2_tiea, R.id.gameresult_set2_tieb,
            R.id.gameresult_set3_a, R.id.gameresult_set3_b, R.id.gameresult_set3_tiea, R.id.gameresult_set3_tieb,
            R.id.gameresult_set4_a, R.id.gameresult_set4_b, R.id.gameresult_set4_tiea, R.id.gameresult_set4_tieb,
            R.id.gameresult_set5_a, R.id.gameresult_set5_b, R.id.gameresult_set5_tiea, R.id.gameresult_set5_tieb};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_result_dialog, container, false);

//        getArguments().getBoolean("type");

        rootView.findViewById(R.id.gameresult_set1_a).requestFocus();

        rootView.findViewById(R.id.gameresult_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> scores = new ArrayList<>();
                for (int i=0 ; i<ids.length ; i++) {
                    scores.add(gets(ids[i]));
                }
                String result = MyParser.stringify(scores);
                ((GameInformation)getActivity()).updateScore(result);
                dismiss();
            }
        });

        return rootView;
    }

    int gets(int id) {
        String input = ((EditText)rootView.findViewById(id)).getText().toString();
        if(input.length()>0) return Integer.parseInt(input);
        return -1;
    }
}
