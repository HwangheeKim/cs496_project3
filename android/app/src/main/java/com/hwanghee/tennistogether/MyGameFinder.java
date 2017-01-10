package com.hwanghee.tennistogether;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;

public class MyGameFinder extends Fragment {
    View rootView;
    public MyGameAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_my_game_finder, container, false);
        gridView = (GridView)rootView.findViewById(R.id.mygamefinder_list);
        mAdapter = new MyGameAdapter();
        gridView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.mygamefinder_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGameData(rootView);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        loadGameData(rootView);

        return rootView;
    }

    public void loadGameData(View view) {
        mAdapter.clear();
        Ion.with(view.getContext())
                .load(MainActivity.serverURL + "/game/joined/" + MainActivity.userID)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for(int i=0 ; i<result.size() ; i++) {
                            JsonObject record = result.get(i).getAsJsonObject();
                            Log.d("Game/all", result.get(i).toString());
                            String courtDecoded = "";
                            try {
                                courtDecoded = URLDecoder.decode(record.get("court").getAsString(), "utf-8");
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            mAdapter.add(record.get("_id").getAsString(), record.get("type").getAsBoolean(),
                                    record.get("playtime").getAsString(), courtDecoded,
                                    record.get("isMatched").getAsBoolean(), record.get("winner").getAsBoolean(),
                                    record.get("score").getAsString(), record.get("player1").getAsString(),
                                    record.get("player2").getAsString(), record.get("player3").getAsString(),
                                    record.get("player4").getAsString());
                        }
                    }
                });
    }
}
