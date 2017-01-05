package com.hwanghee.tennistogether;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements GameFinder.OnFragmentInteractionListener, CourtFinder.OnFragmentInteractionListener {

    static int REQUEST_LOGIN = 0xabcd;
    static String userName = "Username";
    static String userID = "";
    static String userEmail = "something@some.thing";
    private DrawerLayout drawerLayout;
    private NavigationView navView;

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

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                if(item.getItemId() == R.id.drawer_gamefinder) {
                    Fragment newFragment = new GameFinder();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, newFragment).commit();
                } else if(item.getItemId() == R.id.drawer_courtfinder) {
                    Fragment newFragment = new CourtFinder();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, newFragment).commit();
                } else if(item.getItemId() == R.id.drawer_login) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
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
            userID = data.getStringExtra("fbid");
            userName = data.getStringExtra("name");
            userEmail = data.getStringExtra("email");

            View header = navView.getHeaderView(0);
            ImageView drawerAvatar = (ImageView)header.findViewById(R.id.drawer_avatar);
            String imgurl = "https://graph.facebook.com/" + userID + "/picture?height=500";
            Picasso.with(header.getContext()).load(imgurl).into(drawerAvatar);

            TextView drawerUserName = (TextView)header.findViewById(R.id.drawer_username);
            drawerUserName.setText(userName);

            TextView drawerEmail = (TextView)header.findViewById(R.id.drawer_email);
            drawerEmail.setText(userEmail);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
