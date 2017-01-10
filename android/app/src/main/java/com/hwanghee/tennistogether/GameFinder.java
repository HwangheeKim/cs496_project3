package com.hwanghee.tennistogether;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.facebook.AccessToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.net.URLDecoder;

import static android.app.Activity.RESULT_OK;

public class GameFinder extends Fragment {
    public GameAdapter mAdapter;
    public static int GAME_FILTERING = 0x0201;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView gridView;

    private boolean[] options = {true, true, true};
    // Single Game, Double Game, All/Joinable Game

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_game_finder, container, false);

        mAdapter = new GameAdapter();
        gridView = (GridView)view.findViewById(R.id.gamefinder_list);
        gridView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.gamefinder_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGameData(view);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        loadGameData(view);

        FloatingActionButton searchGame = (FloatingActionButton)view.findViewById(R.id.gamefinder_search);
        searchGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                GameFilteringDialog dialogFragment = new GameFilteringDialog();
                Bundle args = new Bundle();
                args.putBoolean("single", options[0]);
                args.putBoolean("double", options[1]);
                args.putBoolean("status", options[2]);
                dialogFragment.setArguments(args);
                dialogFragment.setTargetFragment(GameFinder.this, GAME_FILTERING);
                dialogFragment.show(fm, "SAMPLE FRAGMENT");
            }
        });

        return view;
    }

    // Load from server and apply filter
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

                            if((mAdapter.getItem(i).getType()==true && options[0]==false) ||
                                    (mAdapter.getItem(i).getType()==false && options[1]==false) ||
                                    (isJoinable(record)==false && options[2]==false)) {
                                mAdapter.getItem(i).setVisible(false);
                                Log.d("SOMETHING", "HAS BEEN SET INVISIBLE");
                            } else {
                                mAdapter.getItem(i).setVisible(true);
                            }

                            if(alreadyInGame(record)) {
                                mAdapter.getItem(i).setJoined(true);
                            }
                            else mAdapter.getItem(i).setJoined(false);
                        }

                        mAdapter.removeInvisible();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == MainActivity.ADAPTER_RELOAD) {
            loadGameData(this.getView());
        } else if(requestCode == GAME_FILTERING) {
            options[0] = data.getExtras().getBoolean("single");
            options[1] = data.getExtras().getBoolean("double");
            options[2] = data.getExtras().getBoolean("status");
            loadGameData(this.getView());
        }
    }

    // Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
