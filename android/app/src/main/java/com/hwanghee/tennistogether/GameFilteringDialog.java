package com.hwanghee.tennistogether;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by q on 2017-01-09.
 */

public class GameFilteringDialog extends DialogFragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game_filtering_dialog, container, false);

//        Intent intent = new Intent();
//        getTargetFragment().onActivityResult(GameFinder.GAME_FILTERING, Activity.RESULT_OK, intent);
//        dismiss();

        return rootView;
    }
}
