package com.hwanghee.tennistogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ProfileEdition extends AppCompatActivity {
    String InputPhone;
    String InputGroup;
    TextView userName;
    EditText groupEdit;
    EditText phoneEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);

        userName = (TextView) findViewById(R.id.edition_name);
        groupEdit = (EditText) findViewById(R.id.edition_group);
        phoneEdit = (EditText) findViewById(R.id.edition_phone);

        userName.setText(MainActivity.userName);
        phoneEdit.setText(getIntent().getExtras().getString("Phone"));
        groupEdit.setText(getIntent().getExtras().getString("Group"));

        findViewById(R.id.edition_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputPhone = phoneEdit.getText().toString();
                InputGroup = groupEdit.getText().toString();

                editProfile();
            }
        });
    }

    public void editProfile(){
        JsonObject json = new JsonObject();

        try {
            json.addProperty("userID", MainActivity.userID);
            json.addProperty("name", URLEncoder.encode(MainActivity.userName, "utf-8"));
            json.addProperty("phone", InputPhone);
            json.addProperty("group", URLEncoder.encode(InputGroup, "utf-8"));
            json.addProperty("email", MainActivity.userEmail);
            json.addProperty("picture", "https://graph.facebook.com/" + MainActivity.userID + "/picture?height=500");

        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(getApplicationContext()).load(MainActivity.serverURL + "/user/enroll")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Intent intent = new Intent();
                        setResult(35, intent);
                        finish();
                    }
                });
    }
}
