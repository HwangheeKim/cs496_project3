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
    private DrawerLayout drawerLayout;
    private NavigationView navView;

    private GameFinder gameFinder;
    private MyGameFinder myGameFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer);
        navView = (NavigationView)findViewById(R.id.main_nav_list);
        Fragment newFragment = new GameFinder();

        // Initial Screen
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, newFragment).commit();

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(isLoggedIn()) {
                    updateMyprofile(false);
                }
            }
        });

        gameFinder = new GameFinder();
        myGameFinder = new MyGameFinder();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                if(item.getItemId() == R.id.drawer_gamefinder) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, gameFinder).commit();
                } else if(item.getItemId() == R.id.drawer_courtfinder) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, myGameFinder).commit();
                } else if(item.getItemId() == R.id.drawer_login) {
                    if(isLoggedIn()) {
                        Snackbar.make(navView, "Already Logged in!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivityForResult(intent, REQUEST_LOGIN);
                    }
                } else if(item.getItemId() == R.id.drawer_me) {
                    Fragment newFragment = new MyInfo();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, newFragment).commit();
                }
                drawerLayout.closeDrawers();
                return true;
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
                            Snackbar.make(navView, "Welcome Back, " + userName + "!", Snackbar.LENGTH_SHORT).show();
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
                        View header = navView.getHeaderView(0);
                        ImageView drawerAvatar = (ImageView)header.findViewById(R.id.drawer_avatar);
                        String imgurl = "https://graph.facebook.com/" + userID + "/picture?height=500";
                        Picasso.with(header.getContext()).load(imgurl).into(drawerAvatar);

                        TextView drawerUserName = (TextView)header.findViewById(R.id.drawer_username);
                        drawerUserName.setText(userName);

                        TextView drawerEmail = (TextView)header.findViewById(R.id.drawer_email);
                        drawerEmail.setText(userEmail);
                    }
                });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
