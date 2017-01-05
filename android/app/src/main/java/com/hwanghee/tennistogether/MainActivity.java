package com.hwanghee.tennistogether;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] navItems = {"Menu 1", "Menu 2", "Menu 3", "Menu 4"};
    private ListView navList;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navList = (ListView) findViewById(R.id.main_nav_list);
        container = (FrameLayout) findViewById(R.id.main_container);

        navList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        navList.setOnItemClickListener(new DrawerItemClickListener());

        if(savedInstanceState == null) {
            Fragment matchFinder = new MatchFinder();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.main_container, matchFinder).commit();
        }
    }
}

class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("DrawerItemClickListener", Integer.toString(position));
    }
}
