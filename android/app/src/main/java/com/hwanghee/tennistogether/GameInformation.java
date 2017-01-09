package com.hwanghee.tennistogether;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.socketio.ExceptionCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;

public class GameInformation extends AppCompatActivity {
    String gameID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameID = getIntent().getExtras().getString("gameID");
        setContentView(R.layout.activity_game_information);

        Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/game/"+gameID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if(result==null) {
                            Toast.makeText(getApplicationContext(), "The game has been removed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            updateView(result);
                        }
                    }
                });

    }

    private void updateView(final JsonObject gamedata) {
        // If it is a single game, disappear two players
        if(gamedata.get("type").getAsBoolean()) {
            findViewById(R.id.gameinfo_player2).setVisibility(View.GONE);
            findViewById(R.id.gameinfo_player4).setVisibility(View.GONE);
        } else {
            findViewById(R.id.gameinfo_player2).setVisibility(View.VISIBLE);
            findViewById(R.id.gameinfo_player4).setVisibility(View.VISIBLE);
        }

        updateUserInfo(gamedata, 1);
        updateUserInfo(gamedata, 3);

        if(!gamedata.get("type").getAsBoolean()) {
            updateUserInfo(gamedata, 2);
            updateUserInfo(gamedata, 4);
        }

        try {
            ((TextView)findViewById(R.id.gameinfo_court)).setText(
                    URLDecoder.decode(gamedata.get("court").getAsString(), "utf-8"));
            ((TextView)findViewById(R.id.gameinfo_playtime)).setText(
                    gamedata.get("playtime").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(alreadyInGame(gamedata)) {
            findViewById(R.id.gameinfo_cancel).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.gameinfo_cancel).setVisibility(View.GONE);
        }

        findViewById(R.id.gameinfo_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gamedata.get("player1").getAsString().equals(MainActivity.userID)) {
                    Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/game/drop/"+gameID)
                            .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Toast.makeText(getApplicationContext(), "Game has been canceled", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                } else {
                    Ion.with(getApplicationContext())
                            .load(MainActivity.serverURL+"/game/cancel/"+gameID +"/"+MainActivity.userID)
                            .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            Toast.makeText(getApplicationContext(), "You cancel the game", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    });
                }
            }
        });

        if(gamedata.get("player1").getAsString().equals(MainActivity.userID)) {
            findViewById(R.id.gameinfo_finish).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.gameinfo_finish).setVisibility(View.GONE);
        }

        findViewById(R.id.gameinfo_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                GameResultDialog dialogFragment = new GameResultDialog();
                Bundle args = new Bundle();
                args.putBoolean("type", gamedata.get("type").getAsBoolean());
                dialogFragment.setArguments(args);
                dialogFragment.show(fm, "SAMPLE FRAGMENT");
            }
        });
    }

    public void updateScore(String result) {
        JsonObject json = new JsonObject();
        json.addProperty("score", result);
        json.addProperty("winner", ScoreParser.winner(result));

        Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/game/update/"+gameID)
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    private boolean isLoggedin() { return AccessToken.getCurrentAccessToken() != null; }

    private void updateUserInfo(final JsonObject gamedata, final int playernumber) {
        final String[] jsonname = {"", "player1", "player2", "player3", "player4"};
        final int[] pID = {0, R.id.gameinfo_player1, R.id.gameinfo_player2, R.id.gameinfo_player3, R.id.gameinfo_player4};
        final int[] infoID = {0, R.id.gameinfo_info1, R.id.gameinfo_info2, R.id.gameinfo_info3, R.id.gameinfo_info4};
        final int[] nameID = {0, R.id.gameinfo_name1, R.id.gameinfo_name2, R.id.gameinfo_name3, R.id.gameinfo_name4};
        final int[] groupID = {0, R.id.gameinfo_group1, R.id.gameinfo_group2, R.id.gameinfo_group3, R.id.gameinfo_group4};
        final int[] imgID = {0, R.id.gameinfo_img1, R.id.gameinfo_img2, R.id.gameinfo_img3, R.id.gameinfo_img4};
        final int[] emptyID = {0, R.id.gameinfo_empty1, R.id.gameinfo_empty2, R.id.gameinfo_empty3, R.id.gameinfo_empty4};

        final String player = gamedata.get(jsonname[playernumber]).getAsString();
        if(player.length()>0) {
            findViewById(infoID[playernumber]).setVisibility(View.VISIBLE);
            findViewById(emptyID[playernumber]).setVisibility(View.INVISIBLE);

            Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/user/"+player).asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            try {
                                ((TextView)findViewById(nameID[playernumber])).setText(
                                        URLDecoder.decode(result.get("name").getAsString(), "utf-8"));
                                ((TextView)findViewById(groupID[playernumber])).setText(
                                        URLDecoder.decode(result.get("group").getAsString(), "utf-8"));
                                Picasso.with(getApplicationContext()).load(result.get("picture").getAsString())
                                        .into((ImageView)findViewById(imgID[playernumber]));
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

            findViewById(pID[playernumber]).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), UserInformation.class);
                            intent.putExtra("userID", gamedata.get(jsonname[playernumber]).getAsString());
                            startActivity(intent);
                        }
                    });
        } else {
            findViewById(infoID[playernumber]).setVisibility(View.INVISIBLE);
            findViewById(emptyID[playernumber]).setVisibility(View.VISIBLE);

            findViewById(pID[playernumber]).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isLoggedin()==false) {
                                Toast.makeText(getApplicationContext(), "You have to be logged in!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if(alreadyInGame(gamedata)) {
                                Toast.makeText(getApplicationContext(), "You are already in the game!", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                AlertDialog.Builder alertdialog = new AlertDialog.Builder(new ContextThemeWrapper(GameInformation.this, R.style.AppTheme));
                                alertdialog.setMessage("Want to join this game?");
                                alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "JOINED!", Toast.LENGTH_SHORT).show();
                                        JsonObject json = new JsonObject();
                                        json.addProperty(jsonname[playernumber], MainActivity.userID);

                                        Ion.with(getApplicationContext()).load(MainActivity.serverURL+"/game/update/"+gameID)
                                                .setJsonObjectBody(json).asJsonObject()
                                                .setCallback(new FutureCallback<JsonObject>() {
                                                    @Override
                                                    public void onCompleted(Exception e, JsonObject result) {
                                                        updateView(result);
                                                        Intent intent = new Intent();
                                                        setResult(RESULT_OK, intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                                alertdialog.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                AlertDialog alert = alertdialog.create();
                                alert.setIcon(R.mipmap.ic_launcher);
                                alert.setTitle("JOIN?");
                                alert.show();
                            }
                        }
                    });
        }
    }

    private boolean alreadyInGame(final JsonObject gamedata) {
        if(gamedata.get("player1").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player2").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player3").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player4").getAsString().equals(MainActivity.userID)) return true;
        return false;
    }
}
