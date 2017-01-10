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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;

public class MyGameFinder extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public GameAdapter mAdapter = new GameAdapter();
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MyGameFinder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_game_finder, container, false);

//        mRecyclerView = (RecyclerView)view.findViewById(R.id.gamefinder_list);
//        mLayoutManager = new LinearLayoutManager(view.getContext());
//        mAdapter = new GameAdapter();
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapter);
//
//        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.gamefinder_swiperefresh);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadGameData(view);
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });
//        loadGameData(view);
        return view;
    }

    public void loadGameData(View view) {
        mAdapter.clear();
        Ion.with(view.getContext())
                .load(MainActivity.serverURL + "/game/ongoing")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        for(int i=0 ; i<result.size() ; i++) {
                            JsonObject record = result.get(i).getAsJsonObject();
                            Log.d("Game/all", result.get(i).toString());
                            // TODO : When there's no data on this fields, null pointer exception occurs.
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

                            if(alreadyInGame(record)) {
                                mAdapter.getItem(i).setVisible(true);
                            }
                            else mAdapter.getItem(i).setVisible(false);
                        }
                    }
                });
    }

    private boolean isJoinable(final JsonObject gamedata) {
        if(alreadyInGame(gamedata)) return false;
        if(gamedata.get("type").getAsBoolean()) {
            // Single game
            if(gamedata.get("player3").getAsString().equals("")) return true;
        } else {
            // Double game
            if(gamedata.get("player2").getAsString().equals("")) return true;
            if(gamedata.get("player3").getAsString().equals("")) return true;
            if(gamedata.get("player4").getAsString().equals("")) return true;
        }
        return false;
    }

    private boolean alreadyInGame(final JsonObject gamedata) {
        if(gamedata.get("player1").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player2").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player3").getAsString().equals(MainActivity.userID)) return true;
        if(gamedata.get("player4").getAsString().equals(MainActivity.userID)) return true;
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
