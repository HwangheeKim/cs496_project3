package com.hwanghee.tennistogether;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
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
        } else {
            findViewById(R.id.gameinfo_player2).setVisibility(View.VISIBLE);
            findViewById(R.id.gameinfo_player4).setVisibility(View.VISIBLE);
        }

        Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/game/"+data.getGameID())
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                updateView(result);
            }
        });
    }

    private void updateView(JsonObject result) {
        String player;

        player = result.get("player1").getAsString();
        if(player.length()>0) {
            Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/user/"+player).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // TODO : What if there doesn't exist such user?
                            // TODO : Start from here!
                        }
                    });
        }
    }

    public void tt(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
