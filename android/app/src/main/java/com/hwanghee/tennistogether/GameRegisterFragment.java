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

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import im.delight.android.location.SimpleLocation;

public class GameRegisterFragment extends Fragment {
    View rootView;
    Calendar current = Calendar.getInstance();
    int year, month, day, hour, minute;
    TextView GRtv1;
    TextView GRtv2;
    TextView GRtv3;
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

        GRtv1 = (TextView) rootView.findViewById(R.id.textViewYY);
        GRtv2 = (TextView) rootView.findViewById(R.id.textViewMM);
        GRtv3 = (TextView) rootView.findViewById(R.id.textViewDD);
        year = current.get(Calendar.YEAR);
        month = current.get(Calendar.MONTH);
        day = current.get(Calendar.DAY_OF_MONTH);
        GRtv1.setText(Integer.toString(year));
        GRtv2.setText(Integer.toString(month + 1));
        GRtv3.setText(Integer.toString(day));

        final EditText editText = (EditText) rootView.findViewById(R.id.editText);
        Button addressBtn = (Button) rootView.findViewById(R.id.addressbtn);
        radio = (RadioGroup) rootView.findViewById(R.id.RadioGroup1);
        location = new SimpleLocation(this.getContext());

        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this.getContext());
        }

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        Toast.makeText(getContext(), Double.toString(latitude) , Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), Double.toString(longitude) , Toast.LENGTH_SHORT).show();


        RelativeLayout date = (RelativeLayout) rootView.findViewById(R.id.RL1);
        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                FragmentManager fm = getFragmentManager();
                MyDialogFragment dialogFragment = new MyDialogFragment();
                dialogFragment.setTargetFragment(GameRegisterFragment.this, 0);
                dialogFragment.show(fm, "fragment_game_date");
            }
        });

        RelativeLayout time = (RelativeLayout) rootView.findViewById(R.id.RL2);
        time.setOnClickListener(new View.OnClickListener() {
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

        addressBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inPutText = editText.getText().toString();
                address = inPutText;
                setVariables();
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

            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ", Locale.getDefault());
            Date ndate;
            ndate = new Date(year-1900, month+1, day-30, hour, minute);
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
                        Snackbar.make(rootView, "Game has been registered!", Snackbar.LENGTH_SHORT).show();
                        // TODO : After add, what screen ?
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

        TextView GRtv4 = (TextView) rootView.findViewById(R.id.textViewHH);
        TextView GRtv5 = (TextView) rootView.findViewById(R.id.textViewMM2);

        GRtv4.setText(Integer.toString(hour));
        GRtv5.setText(Integer.toString(minute));
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
