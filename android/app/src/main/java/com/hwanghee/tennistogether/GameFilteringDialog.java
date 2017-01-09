package com.hwanghee.tennistogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * Created by q on 2017-01-09.
 */

public class GameFilteringDialog extends DialogFragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_filtering_dialog, container, false);

        ((CheckBox)rootView.findViewById(R.id.gamefilter_single)).setChecked(getArguments().getBoolean("single"));
        ((CheckBox)rootView.findViewById(R.id.gamefilter_double)).setChecked(getArguments().getBoolean("double"));
        if(getArguments().getBoolean("status")) {
            ((RadioButton)rootView.findViewById(R.id.gamefilter_all)).setChecked(true);
        } else {
            ((RadioButton)rootView.findViewById(R.id.gamefilter_joinable)).setChecked(true);
        }

        rootView.findViewById(R.id.gamefilter_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("single", ((CheckBox)rootView.findViewById(R.id.gamefilter_single)).isChecked());
                intent.putExtra("double", ((CheckBox)rootView.findViewById(R.id.gamefilter_double)).isChecked());

                intent.putExtra("status", ((RadioButton)rootView.findViewById(R.id.gamefilter_all)).isChecked());

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                dismiss();
            }
        });

        return rootView;
    }
}
