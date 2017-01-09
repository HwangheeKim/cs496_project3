package com.hwanghee.tennistogether;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    ImageView PEuserImage;
    TextView PEuserName;
    EditText PEuserPhone;
    EditText PEuserGroup;
    String InputPhone;
    String InputGroup;
    Button EditButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edition);



        PEuserImage = (ImageView) findViewById(R.id.PEiv);
        PEuserName = (TextView) findViewById(R.id.PEtv);
        PEuserPhone = (EditText) findViewById(R.id.PEet1);
        PEuserGroup = (EditText) findViewById(R.id.PEet2);
        EditButton = (Button) findViewById(R.id.PEbt);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        loadInfoView();
        PEuserName.setText(MainActivity.userName);
        PEuserPhone.setText(getIntent().getExtras().getString("Phone"));
        PEuserGroup.setText(getIntent().getExtras().getString("Group"));
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputPhone = PEuserPhone.getText().toString();
                InputGroup = PEuserGroup.getText().toString();

                editProfile();
            }
        });

    }
    public void loadInfoView(){

        Ion.with(PEuserImage.getContext()).load(MainActivity.serverURL+"/user/"+ MainActivity.userID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("setImage", result.toString());
                Picasso.with(PEuserImage.getContext())
                        .load(result.get("picture").getAsString())
                        .into(PEuserImage);
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
                //       Toast.makeText(getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                        // Toast.makeText(getApplicationContext(), MainActivity.serverURL, Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
