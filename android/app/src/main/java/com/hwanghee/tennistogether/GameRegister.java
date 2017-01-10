package com.hwanghee.tennistogether;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;


//TextView GRtv1 = (TextView) findViewById(R.id.textViewYY);
//        TextView GRtv2 = (TextView) findViewById(R.id.textViewMM);
//        TextView GRtv3 = (TextView) findViewById(R.id.textViewDD);

public class GameRegister extends AppCompatActivity {

    Calendar current = Calendar.getInstance();
    int year, month, day, hour, minute;
    TextView GRtv1;
    TextView GRtv2;
    TextView GRtv3;
    Double lat,lon;
    String latST;
    String lonST;
    Location location;
    LocationManager locationManager;
    String address = new String();
    RadioGroup radio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_register);

        GRtv1 = (TextView) findViewById(R.id.textViewYY);
        GRtv2 = (TextView) findViewById(R.id.textViewMM);
        GRtv3 = (TextView) findViewById(R.id.textViewDD);
        year = current.get(Calendar.YEAR);
        month = current.get(Calendar.MONTH);
        day = current.get(Calendar.DAY_OF_MONTH);
        GRtv1.setText(Integer.toString(year));
        GRtv2.setText(Integer.toString(month+1));
        GRtv3.setText(Integer.toString(day));

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button addressBtn = (Button) findViewById(R.id.addressbtn);
        radio = (RadioGroup) findViewById(R.id.RadioGroup1);




        RelativeLayout date = (RelativeLayout) findViewById(R.id.RL1);
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Toast.makeText(getApplicationContext(), "날짜를 선택해봅시다.", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.show(fm, "fragment_game_date");
            }
        });


        RelativeLayout time = (RelativeLayout) findViewById(R.id.RL2);
        time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Toast.makeText(getApplicationContext(), "시간을 선택해봅시다.", Toast.LENGTH_SHORT).show();
                FragmentManager fm2 = getSupportFragmentManager();
                MyDialogFragment2 dialogFragment2 = new MyDialogFragment2();
                dialogFragment2.show(fm2, "fragment_game_time");
            }
        });




        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButtonGR1: // first button
                        Toast.makeText(getApplicationContext(), "단식을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioButtonGR2: // secondbutton
                        Toast.makeText(getApplicationContext(), "복식을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        if (Build.VERSION.SDK_INT >= 23 && getApplicationContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {

            // Android version is lesser than 6.0 or the permission is already granted.
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        location = locationManager.getLastKnownLocation(bestProvider);
        Toast.makeText(getApplicationContext(), "HIHIHIHI" , Toast.LENGTH_SHORT).show();
        getLocation();


        addressBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inPutText = editText.getText().toString();
                Toast.makeText(GameRegister.this, "입력되었습니다.", Toast.LENGTH_SHORT).show();
                address = inPutText;
                setVariables();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getLocation();
            } else {
                Toast.makeText(this.getApplicationContext(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setVariables() {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("type", radio.getCheckedRadioButtonId() == R.id.radioButtonGR1);
            json.addProperty("court", URLEncoder.encode(address, "utf-8"));
            json.addProperty("player1", MainActivity.userID);

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ", Locale.getDefault());
            Date ndate;
            ndate = new Date(year-1900, month+1, day-30, hour, minute);
            dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateformat.format(ndate);

            json.addProperty("playtime", dateformat.format(ndate)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(getApplicationContext()).load(MainActivity.serverURL + "/game/register")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
//                        Toast.makeText(getApplicationContext(), "POSTED", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void setDate(int yr, int monthOfYear, int dayOfMonth) {
        Log.d("setDate", "#" + yr + "#" + monthOfYear + "#" + dayOfMonth);
        this.year = yr;
        this.month = monthOfYear;
        this.day = dayOfMonth;


        GRtv1.setText(Integer.toString(this.year));
        GRtv2.setText(Integer.toString(this.month+1));
        GRtv3.setText(Integer.toString(this.day));
    }

    public void setTime(int hr, int mn) {
        Log.d("setHour", "#" + hr + "#" + mn);
        this.hour = hr;
        this.minute = mn;

        TextView GRtv4 = (TextView) findViewById(R.id.textViewHH);
        TextView GRtv5 = (TextView) findViewById(R.id.textViewMM2);

        GRtv4.setText(Integer.toString(hour));
        GRtv5.setText(Integer.toString(minute));
    }

    public void checkPermission(){
//        Toast.makeText(getApplicationContext(), Integer.toString(PackageManager.PERMISSION_GRANTED) , Toast.LENGTH_SHORT).show();

    }



    public LatLng getLocation(){


        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            latST = Double.toString(lat);
            lonST = Double.toString(lon);

            Toast.makeText(getApplicationContext(), latST , Toast.LENGTH_SHORT).show();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_game_date, container, false);

            final DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
            Calendar c = Calendar.getInstance();
            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int yearOfCentury, int monthOfYear, int dayOfMonth) {
                    ((GameRegister) getActivity()).setDate(yearOfCentury, monthOfYear, dayOfMonth);
                }
            });
            Button finishButton = (Button) v.findViewById(R.id.datebutton);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(MyDialogFragment.this).commit();
                }
            });
            return v;
        }
    }

    public static class MyDialogFragment2 extends DialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_game_time, container, false);

            final TimePicker timePicker = (TimePicker)v.findViewById(R.id.timePicker);
            Calendar c = Calendar.getInstance();
            timePicker.setCurrentHour(c.get(Calendar.HOUR));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    ((GameRegister)getActivity()).setTime(hourOfDay, minute);
                    Log.d("SETSETSETHOUR", "#" + hourOfDay + "#" + minute);
                }
            });

            Button finishButton2 = (Button)v.findViewById(R.id.timebutton);
            finishButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().getSupportFragmentManager().beginTransaction().remove(MyDialogFragment2.this).commit();
                }
            });
            return v;
        }
    }
}

