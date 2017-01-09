package com.hwanghee.tennistogether;

import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;
import android.view.LayoutInflater;
import static android.R.attr.data;
import static android.R.attr.inflatedId;

public class UserInformation extends AppCompatActivity {
    ImageView UserImage;
    TextView UItv1;
    TextView UItv2;
    TextView UItv3;
    TextView UItv4;
    Button InfoEditButton;
    Space UserSpace;
    String userIDclicked;
    Layout layout;
    String userName;
    String userPhone;
    String userGroup;
    String userRecord;
    Intent intent;
//    private ArrayList<InfoData> infoDatas;
//    public UserInformation() {infoDatas = new ArrayList<GameData>();}
//    public UserInformation(ArrayList<InfoData> infoDatas) {
//        this.infoDatas = infoDatas;
//    }
//    GameInformation에서 사람을 누르면 UserInformation Fragment로 전환이 되고
//    누른 Layout의 유저넘버를 Intent로 받아와서 해당 유저 ID가 GameFinder에서 받아온 ID와 같으면
//    수정 버튼을 활성화 시키고 다르면
//    해당 유저의 사진 이름 전화번호 (개인 장비)를 받아오고 게임 데이터를 분석해서 전적을 출력한다.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        //InfoData infoData = infoDatas.get(position);


        UItv1 = (TextView) findViewById(R.id.userInfoText1);
        UItv2 = (TextView) findViewById(R.id.userInfoText2);
        UItv3 = (TextView) findViewById(R.id.userInfoText3);
        UItv4 = (TextView) findViewById(R.id.userInfoText4);
        UserImage = (ImageView) findViewById(R.id.userInfoImage);
        InfoEditButton = (Button) findViewById(R.id.InfoEditButton);
        UserSpace = (Space) findViewById(R.id.UserSpace);
        UserSpace.getResources().getDrawable(R.drawable.border);
        userIDclicked = getIntent().getExtras().getString("userID");




        //Toast.makeText(getApplicationContext(), MainActivity.userID , Toast.LENGTH_SHORT).show();

        this.loadInfoView();
        this.loadInfoData();

//        setContentView(R.layout.activity_user_information);

        intent = new Intent(getApplicationContext(), ProfileEdition.class);


        if(MainActivity.userID.equals(userIDclicked)) {
            findViewById(R.id.InfoEditButton).setVisibility(View.VISIBLE);

            InfoEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });
        } else {
            findViewById(R.id.InfoEditButton).setVisibility(View.GONE);
        }
        Toast.makeText(getApplicationContext(), userIDclicked , Toast.LENGTH_SHORT).show();
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
                        intent.putExtra("Phone", userPhone);
                        intent.putExtra("Group", userGroup);
                        userName = result.get("name").getAsString();
                        userPhone = result.get("phone").getAsString();
                        Toast.makeText(getApplicationContext(), userPhone, Toast.LENGTH_SHORT).show();
                        userGroup = result.get("group").getAsString();
                        UItv1.setText(nameDecoded);
                        UItv2.setText(userPhone);
                        UItv3.setText(groupDecoded);
                    }
                });

    }
}
