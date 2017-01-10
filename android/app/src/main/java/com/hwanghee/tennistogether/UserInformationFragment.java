package com.hwanghee.tennistogether;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;

import static com.facebook.FacebookSdk.getApplicationContext;

public class UserInformationFragment extends Fragment {
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
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_information, container, false);

        toolbar = (Toolbar)rootView.findViewById(R.id.userinfof_toolbar);
        toolbar.setTitle("");
        toolbar.setBackgroundColor(0x00461e54);
//        setSupportActionBar(toolbar);

        groupText = (TextView)rootView.findViewById(R.id.userinfof_group);
        phoneText = (TextView)rootView.findViewById(R.id.userinfof_phone);
        recordText = (TextView)rootView.findViewById(R.id.userinfof_record);
        UserImage = (ImageView)rootView.findViewById(R.id.userInfofImage);
        infoEditButton = (FloatingActionButton) rootView.findViewById(R.id.userinfof_fab);

        ((AppBarLayout)rootView.findViewById(R.id.userinfof_appbar)).setExpanded(true);

        this.loadInfoView();
        this.loadInfoData();

        intent = new Intent(getApplicationContext(), ProfileEdition.class);
        infoEditButton.setVisibility(View.VISIBLE);
        infoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("Phone", userPhone);
                intent.putExtra("Group", userGroup);
                startActivityForResult(intent, 35);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 35) {
            updateview();
        }
    }

    public void updateview(){
        loadInfoView();
        loadInfoData();
    }

    public void loadInfoView(){

        Ion.with(UserImage.getContext()).load(MainActivity.serverURL+"/user/"+ MainActivity.userID)
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
        Ion.with(getApplicationContext())
                .load(MainActivity.serverURL + "/user/" + MainActivity.userID)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

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
                        userGroup = groupDecoded;
                        phoneText.setText(userPhone);
                        groupText.setText(groupDecoded);
//                        getSupportActionBar().setTitle(nameDecoded);
                    }
                });

        Ion.with(getApplicationContext())
                .load(MainActivity.serverURL + "/user/record/" + MainActivity.userID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                int win = result.get("win").getAsInt();
                int lose = result.get("lose").getAsInt();

                recordText.setText("" + win + " win   " + lose + " lose");
            }
        });
    }
}
