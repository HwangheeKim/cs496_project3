package com.hwanghee.tennistogether;

import android.util.Log;
import android.widget.Space;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.data;

public class UserInformation extends AppCompatActivity {
    ImageView UserImage;
    TextView UItv1;
    TextView UItv2;
    TextView UItv3;
    TextView UItv4;
    Button InfoEditButton;
    Space UserSpace;

//    GameInformation에서 사람을 누르면 UserInformation Fragment로 전환이 되고
//    누른 Layout의 유저넘버를 Intent로 받아와서 해당 유저 ID가 GameFinder에서 받아온 ID와 같으면
//    수정 버튼을 활성화 시키고 다르면
//    해당 유저의 사진 이름 전화번호 (개인 장비)를 받아오고 게임 데이터를 분석해서 전적을 출력한다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);



        UItv1 = (TextView) findViewById(R.id.userInfoText1);
        UItv2 = (TextView) findViewById(R.id.userInfoText2);
        UItv3 = (TextView) findViewById(R.id.userInfoText3);
        UItv4 = (TextView) findViewById(R.id.userInfoText4);
        UserImage = (ImageView) findViewById(R.id.userInfoImage);
        InfoEditButton = (Button) findViewById(R.id.InfoEditButton);
        UserSpace = (Space) findViewById(R.id.UserSpace);
        UserSpace.getResources().getDrawable(R.drawable.border);
        String userIDclicked = getIntent().getExtras().getString("UserID");

        if(MainActivity.userID == userIDclicked) {
            findViewById(R.id.InfoEditButton).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.InfoEditButton).setVisibility(View.GONE);
        }

        Log.d("userIDclicked: " ,  userIDclicked);
        Log.d("MainActivity.userID: " ,  MainActivity.userID);
    }
}
