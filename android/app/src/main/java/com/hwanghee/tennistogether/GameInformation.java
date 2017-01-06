package com.hwanghee.tennistogether;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

public class GameInformation extends AppCompatActivity {
    private int position;
    private GameData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_information);

        position = Integer.decode(getIntent().getExtras().get("position").toString());
        data = GameFinder.mAdapter.get(position);

        // If it is a single game, disappear two players
        if(data.getType()) {
            findViewById(R.id.gameinfo_player2).setVisibility(View.GONE);
            findViewById(R.id.gameinfo_player4).setVisibility(View.GONE);
        }

        // Show proper name, group, image
        loadUserInfo();

        // make OnClickListener on each players layout

        //

    }

    private void loadUserInfo() {
    }

    public void tt(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
