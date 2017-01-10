package com.hwanghee.tennistogether;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;

public class GameRegisterFragment extends Fragment {
    View rootView;
    Calendar current = Calendar.getInstance();
    int year, month, day, hour, minute;
    TextView dateView;
    TextView timeView;
    String coordinate;
    double latitude;
    double longitude;

    String address = new String();
    RadioGroup radio;
    private SimpleLocation location;

    public GameRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_game_register, container, false);

        dateView = (TextView)rootView.findViewById(R.id.gameregister_date);
        timeView = (TextView)rootView.findViewById(R.id.gameregister_time);
        year = current.get(Calendar.YEAR);
        month = current.get(Calendar.MONTH)+1;
        day = current.get(Calendar.DAY_OF_MONTH);
        hour = current.get(Calendar.HOUR_OF_DAY);
        minute = current.get(Calendar.MINUTE);

        dateView.setText("" + year + ". " + month + ". " + day + ".");
        timeView.setText("" + hour + " : " + minute);

        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        View doneBtn = rootView.findViewById(R.id.gameregister_done);
        View cancelBtn = rootView.findViewById(R.id.gameregister_cancel);
        radio = (RadioGroup) rootView.findViewById(R.id.RadioGroup1);
        location = new SimpleLocation(this.getContext());


        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.getContext());
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

//        coordinate = latitude + "," +longitude;
//        Toast.makeText(getContext(), coordinate , Toast.LENGTH_SHORT).show();


        rootView.findViewById(R.id.gameregister_date_change).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setTargetFragment(GameRegisterFragment.this, 0);
                dialogFragment.show(fm, "fragment_game_date");
            }
        });

        rootView.findViewById(R.id.gameregister_time_change).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                FragmentManager fm2 = getFragmentManager();
                MyDialogFragment2 dialogFragment2 = new MyDialogFragment2();
                dialogFragment2.setTargetFragment(GameRegisterFragment.this, 0);
                dialogFragment2.show(fm2, "fragment_game_time");
            }
        });

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inPutText = editText.getText().toString();
                address = inPutText;
                setVariables();
                ((MainActivity)getActivity()).loadGameFinder();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).loadGameFinder();
            }
        });
        return rootView;
    }

    public void setVariables() {
        JsonObject json = new JsonObject();
        try {
            json.addProperty("type", radio.getCheckedRadioButtonId() == R.id.radioButtonGR1);
            json.addProperty("court", URLEncoder.encode(address, "utf-8"));
            json.addProperty("player1", MainActivity.userID);
            json.addProperty("latitute", latitude);
            json.addProperty("longitute", longitude);
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ", Locale.getDefault());
            Date ndate;
            ndate = new Date(year-1900, month, day-30, hour, minute);
            dateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateformat.format(ndate);

            json.addProperty("playtime", dateformat.format(ndate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ion.with(rootView.getContext()).load(MainActivity.serverURL + "/game/register")
                .setJsonObjectBody(json).asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                    }
                });
    }

    public void setDate(int yr, int monthOfYear, int dayOfMonth) {
        Log.d("setDate", "#" + yr + "#" + monthOfYear + "#" + dayOfMonth);
        this.year = yr;
        this.month = monthOfYear+1;
        this.day = dayOfMonth;

        dateView.setText("" + year + ". " + month + ". " + day + ".");
    }

    public void setTime(int hr, int mn) {
        Log.d("setHour", "#" + hr + "#" + mn);
        this.hour = hr;
        this.minute = mn;

        timeView.setText("" + hour + " : " + minute);
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
                    ((GameRegisterFragment)getTargetFragment()).setDate(yearOfCentury, monthOfYear, dayOfMonth);
                }
            });
            Button finishButton = (Button) v.findViewById(R.id.datebutton);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getActivity().getSupportFragmentManager().beginTransaction().remove(MyDialogFragment.this).commit();
                    dismiss();
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
            timePicker.setIs24HourView(true);
            Calendar c = Calendar.getInstance();
            timePicker.setCurrentHour(c.get(Calendar.HOUR));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    ((GameRegisterFragment)getTargetFragment()).setTime(hourOfDay, minute);
                    Log.d("SETSETSETHOUR", "#" + hourOfDay + "#" + minute);
                }
            });

            Button finishButton2 = (Button)v.findViewById(R.id.timebutton);
            finishButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getActivity().getSupportFragmentManager().beginTransaction().remove(MyDialogFragment2.this).commit();
                    dismiss();
                }
            });
            return v;
        }
    }
}
