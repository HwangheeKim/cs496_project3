package com.hwanghee.tennistogether;

import android.content.Intent;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by q on 2017-01-06.
 */

public class GameAdapter extends RecyclerView.Adapter<GameViewHolder> {
    private ArrayList<GameData> gameDatas;

    public GameAdapter() {gameDatas = new ArrayList<GameData>();}

    public GameAdapter(ArrayList<GameData> gameDatas) {
        this.gameDatas = gameDatas;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        GameData gameData = gameDatas.get(position);
        holder.courtText.setText(gameData.getCourt());
        holder.playtimeText.setText(gameData.getPlaytime());
        setImage(holder.player1Image, gameDatas.get(position).getPlayer1());
        setImage(holder.player2Image, gameDatas.get(position).getPlayer2());
        setImage(holder.player3Image, gameDatas.get(position).getPlayer3());
        setImage(holder.player4Image, gameDatas.get(position).getPlayer4());
        if(gameDatas.get(position).getType()) {
            holder.player2Image.setVisibility(View.GONE);
            holder.player4Image.setVisibility(View.GONE);
        } else {
            holder.player2Image.setVisibility(View.VISIBLE);
            holder.player4Image.setVisibility(View.VISIBLE);
        }
    }

    private void setImage(final ImageView imageView, String userID) {
        if (userID==null || userID.length() == 0) return;


        Ion.with(imageView.getContext()).load(MainActivity.serverURL+"/user/"+userID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("setImage", result.toString());
                Picasso.with(imageView.getContext())
                        .load(result.get("picture").getAsString())
                        .into(imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameDatas.size();
    }

    public void add(String gameID, boolean isSingle, String playtime, String court, boolean isMatched, boolean winner, String score,
                    String uid1, String uid2, String uid3, String uid4) {
        gameDatas.add(new GameData(gameID, isSingle, playtime, uid1, uid2, uid3, uid4, court, winner, isMatched, score));
        notifyItemInserted(gameDatas.size()-1);
    }

    public void clear() { gameDatas.clear(); notifyDataSetChanged(); }

    public GameData get(int position) { return gameDatas.get(position); }
}

class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    protected TextView courtText;
    protected TextView playtimeText;
    protected ImageView player1Image;
    protected ImageView player2Image;
    protected ImageView player3Image;
    protected ImageView player4Image;

    public GameViewHolder(View itemView) {
        super(itemView);

        courtText = (TextView)itemView.findViewById(R.id.game_court);
        playtimeText = (TextView)itemView.findViewById(R.id.game_time);
        player1Image = (ImageView)itemView.findViewById(R.id.game_img1);
        player2Image = (ImageView)itemView.findViewById(R.id.game_img2);
        player3Image = (ImageView)itemView.findViewById(R.id.game_img3);
        player4Image = (ImageView)itemView.findViewById(R.id.game_img4);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), GameInformation.class);
        intent.putExtra("gameID", GameFinder.mAdapter.get(getAdapterPosition()).getGameID());
        v.getContext().startActivity(intent);
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

