package com.hwanghee.tennistogether;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UserInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        getIntent().getExtras().getString("userID");
    }
}
