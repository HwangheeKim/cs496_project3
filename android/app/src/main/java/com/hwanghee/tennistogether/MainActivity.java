package com.hwanghee.tennistogether;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements GameFinder.OnFragmentInteractionListener, MyGameFinder.OnFragmentInteractionListener,
                    MyInfo.OnFragmentInteractionListener{

    static int REQUEST_LOGIN = 0xabcd;
    static int ADAPTER_RELOAD = 0xabcc;
    static String userName = "Username";
    static String userID = "";
    static String userEmail = "something@some.thing";
    static String serverURL = "http://52.78.101.202:3000";
    static String userToken = "";

    private GameFinder gameFinder;
    private MyGameFinder myGameFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        gameFinder = new GameFinder();
        myGameFinder = new MyGameFinder();

        // Initial Screen
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, gameFinder).commit();

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(isLoggedIn()) {
                    updateMyprofile(false);
                }
            }
        });

        findViewById(R.id.drawer_gamefinder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, gameFinder).commit();
                ((DrawerLayout)findViewById(R.id.main_drawer)).closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.drawer_addgame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DrawerLayout)findViewById(R.id.main_drawer)).closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.drawer_mygames).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, myGameFinder).commit();
                ((DrawerLayout)findViewById(R.id.main_drawer)).closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.drawer_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DrawerLayout)findViewById(R.id.main_drawer)).closeDrawer(Gravity.LEFT);
            }
        });

        findViewById(R.id.drawer_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn()) {
                    Snackbar.make(findViewById(R.id.main_container), "Already Logged In!", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                }
                ((DrawerLayout)findViewById(R.id.main_drawer)).closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_LOGIN) {
            updateMyprofile(true);
        } else if(requestCode == ADAPTER_RELOAD) {
            gameFinder.loadGameData(getCurrentFocus());
//            myGameFinder.loadGameData(getCurrentFocus());
        }
    }

    private boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void updateMyprofile(final boolean isFirsttime) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Request Result", response.toString());

                        try {
                            userID = response.getJSONObject().getString("id");
                            userName = URLDecoder.decode(response.getJSONObject().getString("name"), "utf-8");
                            userEmail = response.getJSONObject().getString("email");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(!isFirsttime) {
                            Snackbar.make(findViewById(R.id.main_container), "Welcome Back, " + userName + "!", Snackbar.LENGTH_SHORT).show();
                        }
                        enrollMe();
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email, gender, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void enrollMe() {
        JsonObject json = new JsonObject();

        try {
            json.addProperty("userID", userID);
            json.addProperty("userToken", FirebaseInstanceId.getInstance().getToken());
            json.addProperty("name", URLEncoder.encode(userName, "utf-8"));
            json.addProperty("email", userEmail);
            json.addProperty("picture", "https://graph.facebook.com/" + userID + "/picture?height=500");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(getApplicationContext()).load(serverURL+"/user/enroll")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        ImageView drawerAvatar = (ImageView)findViewById(R.id.drawer_avatar);
                        String imgurl = "https://graph.facebook.com/" + userID + "/picture?height=500";
                        Picasso.with(getApplicationContext()).load(imgurl).into(drawerAvatar);

                        TextView drawerUserName = (TextView)findViewById(R.id.drawer_username);
                        drawerUserName.setText(userName);
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
