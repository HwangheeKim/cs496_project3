package com.hwanghee.tennistogether;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;

public class UserInformation extends AppCompatActivity {
    ImageView UserImage;
    TextView groupText;
    TextView recordText;
    TextView phoneText;
    FloatingActionButton infoEditButton;
    String userIDclicked;
    Layout layout;
    String userName;
    String userPhone;
    String userGroup;
    Intent intent;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        //InfoData infoData = infoDatas.get(position);

        toolbar = (Toolbar) findViewById(R.id.userinfo_toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(0x00461e54);
        setSupportActionBar(toolbar);

        groupText = (TextView) findViewById(R.id.userinfo_group);
        phoneText = (TextView) findViewById(R.id.userinfo_phone);
        recordText = (TextView) findViewById(R.id.userinfo_record);
        UserImage = (ImageView) findViewById(R.id.userInfoImage);
        infoEditButton = (FloatingActionButton) findViewById(R.id.userinfo_fab);
        userIDclicked = getIntent().getExtras().getString("userID");

        ((AppBarLayout)findViewById(R.id.userinfo_appbar)).setExpanded(true);

        this.loadInfoView();
        this.loadInfoData();

        intent = new Intent(getApplicationContext(), ProfileEdition.class);

        if(MainActivity.userID.equals(userIDclicked)) {
            infoEditButton.setVisibility(View.VISIBLE);

            infoEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent.putExtra("Phone", userPhone);
                    intent.putExtra("Group", userGroup);
                    startActivityForResult(intent, 35);
                }
            });
        } else {
            infoEditButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 35) {
            updateview();
//            setContentView(R.layout.activity_user_information);
        }
    }

    public void updateview(){
        loadInfoView();
        loadInfoData();

        intent = new Intent(getApplicationContext(), ProfileEdition.class);


        if(MainActivity.userID.equals(userIDclicked)) {
            findViewById(R.id.userinfo_fab).setVisibility(View.VISIBLE);

            infoEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(intent, 35);
                }
            });
        } else {
            findViewById(R.id.userinfo_fab).setVisibility(View.GONE);
        }
//        Toast.makeText(getApplicationContext(), userIDclicked , Toast.LENGTH_SHORT).show();
    }


    public void loadInfoView(){

        Ion.with(UserImage.getContext()).load(MainActivity.serverURL+"/user/"+ userIDclicked)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("setImage", result.toString());
                Picasso.with(UserImage.getContext())
                        .load(result.get("picture").getAsString())
                        .into(UserImage);
            }
        });
    }

    public void loadInfoData() {
//        infoAdapter.clear();
        Ion.with(getApplicationContext())
                .load(MainActivity.serverURL + "/user/" + userIDclicked)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

//                        userRecord = jasonResult.get("_phone").getAsString();
                        String nameDecoded = "";
                        try {
                            nameDecoded = URLDecoder.decode(result.get("name").getAsString(), "utf-8");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        String groupDecoded = "";
                        try {
                            groupDecoded = URLDecoder.decode(result.get("group").getAsString(), "utf-8");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        userName = result.get("name").getAsString();
                        userPhone = result.get("phone").getAsString();
//                        Toast.makeText(getApplicationContext(), userPhone, Toast.LENGTH_SHORT).show();
                        userGroup = groupDecoded;
                        phoneText.setText(userPhone);
                        groupText.setText(groupDecoded);
                        getSupportActionBar().setTitle(nameDecoded);
                    }
                });

    }
}
