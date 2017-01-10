package com.hwanghee.tennistogether;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by q on 2017-01-06.
 */

public class GameAdapter extends BaseAdapter {
    private ArrayList<GameData> gameDatas;
    int[] colors = {0xff1e1d20, 0xff30395c, 0xff2a2c2b, 0xff14212b, 0xff083643, 0xff0e5066 };

    public GameAdapter() {
        gameDatas = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return gameDatas.size();
    }

    @Override
    public GameData getItem(int position) {
        return gameDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_game, parent, false);
        }

        convertView.findViewById(R.id.game_item).setBackgroundColor(colors[position%colors.length]);
        convertView.findViewById(R.id.game_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), GameInformation.class);
                intent.putExtra("gameID", gameDatas.get(position).getGameID());
                ((Activity)v.getContext()).startActivityForResult(intent, MainActivity.ADAPTER_RELOAD);
            }
        });

        ((TextView)convertView.findViewById(R.id.game_court)).setText(gameDatas.get(position).getCourt());

       
        ((TextView)convertView.findViewById(R.id.game_date)).setText(MyParser.getDate(gameDatas.get(position).getPlaytime()));
        ((TextView)convertView.findViewById(R.id.game_time)).setText(MyParser.getTime(gameDatas.get(position).getPlaytime()));

        if(gameDatas.get(position).getType()) { // If the game is single
            convertView.findViewById(R.id.game_p3).setVisibility(View.GONE);
            convertView.findViewById(R.id.game_p4).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.game_p3).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.game_p4).setVisibility(View.VISIBLE);
        }

        int joinedPlayer = playerJoined(position);
        switch (joinedPlayer){
            case 1:
                ((ImageView)convertView.findViewById(R.id.game_p2)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.game_p3)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.game_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 2:
                ((ImageView)convertView.findViewById(R.id.game_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.game_p3)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.game_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 3:
                ((ImageView)convertView.findViewById(R.id.game_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.game_p3)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.game_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 4:
                ((ImageView)convertView.findViewById(R.id.game_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.game_p3)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.game_p4)).setImageResource(R.drawable.account);
                break;
        }

        return convertView;
    }

    private int playerJoined(int position) {
        int joined = 1;
        if(!gameDatas.get(position).getPlayer2().equals("")) joined++;
        if(!gameDatas.get(position).getPlayer3().equals("")) joined++;
        if(!gameDatas.get(position).getPlayer4().equals("")) joined++;
        return joined;
    }

    public void add(String gameID, boolean isSingle, String playtime, String court, boolean isMatched, boolean winner, String score,
                    String uid1, String uid2, String uid3, String uid4) {
        gameDatas.add(new GameData(gameID, isSingle, playtime, uid1, uid2, uid3, uid4, court, winner, isMatched, score));
        notifyDataSetChanged();
    }

    public void clear() { gameDatas.clear(); notifyDataSetChanged(); }

    public void removeInvisible() {
        Iterator<GameData> iter = gameDatas.iterator();
        while (iter.hasNext()) {
            GameData g = iter.next();
            if (g.isVisible()==false) iter.remove();
        }
    }
}

class GameData {
    private String gameID;
    private boolean type; // true for single, false for double
    private String playtime;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String court;
    private boolean winner; // true for player 1&2 wins, false for others
    private boolean isMatched; // true if the game has been matched
    private String score; // Empty if not finished, something if finished
    private boolean visible;
    private boolean joined;

    public GameData(String gameID, boolean type, String playtime, String player1, String player2, String player3, String player4, String court, boolean winner, boolean isMatched, String score) {
        this.gameID = gameID;
        this.type = type;
        this.playtime = playtime;
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.court = court;
        this.winner = winner;
        this.isMatched = isMatched;
        this.score = score;
        this.visible=true;
        this.joined=false;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "<GameData type:" + type + " playtime:" + playtime + ">";
    }

    public String getGameID() {
        return gameID;
    }
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
    public boolean getType() {
        return type;
    }
    public void setType(boolean type) {
        this.type = type;
    }
    public String getPlaytime() {
        return playtime;
    }
    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }
    public String getPlayer1() {
        return player1;
    }
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }
    public String getPlayer2() {
        return player2;
    }
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }
    public String getPlayer3() {
        return player3;
    }
    public void setPlayer3(String player3) {
        this.player3 = player3;
    }
    public String getPlayer4() {
        return player4;
    }
    public void setPlayer4(String player4) {
        this.player4 = player4;
    }
    public String getCourt() {
        return court;
    }
    public void setCourt(String court) {
        this.court = court;
    }
    public boolean getWinner() {
        return winner;
    }
    public void setWinner(boolean winner) {
        this.winner = winner;
    }
    public boolean isMatched() {
        return isMatched;
    }
    public void setMatched(boolean matched) {
        isMatched = matched;
    }
    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }
}

