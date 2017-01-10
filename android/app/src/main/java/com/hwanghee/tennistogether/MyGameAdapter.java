package com.hwanghee.tennistogether;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by q on 2017-01-10.
 */

public class MyGameAdapter extends BaseAdapter {
    private ArrayList<GameData> gameDatas;
    int[] colors = {0xff165c30, 0xff244f17, 0xff18611f, 0xff1f2e0b, 0xff565c16, 0xff125c4f };

    public MyGameAdapter() {
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
            convertView = inflater.inflate(R.layout.item_mygame, parent, false);
        }

        convertView.findViewById(R.id.mygame_item).setBackgroundColor(colors[position%colors.length]);
        convertView.findViewById(R.id.mygame_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), GameInformation.class);
                intent.putExtra("gameID", gameDatas.get(position).getGameID());
                ((Activity)v.getContext()).startActivityForResult(intent, MainActivity.ADAPTER_RELOAD);
            }
        });

        ((TextView)convertView.findViewById(R.id.mygame_court)).setText(gameDatas.get(position).getCourt());


        ((TextView)convertView.findViewById(R.id.mygame_date)).setText(MyParser.getDate(gameDatas.get(position).getPlaytime()));
        ((TextView)convertView.findViewById(R.id.mygame_time)).setText(MyParser.getTime(gameDatas.get(position).getPlaytime()));

        if(gameDatas.get(position).getType()) { // If the game is single
            convertView.findViewById(R.id.mygame_p3).setVisibility(View.GONE);
            convertView.findViewById(R.id.mygame_p4).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.mygame_p3).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.mygame_p4).setVisibility(View.VISIBLE);
        }

        int joinedPlayer = playerJoined(position);
        switch (joinedPlayer){
            case 1:
                ((ImageView)convertView.findViewById(R.id.mygame_p2)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.mygame_p3)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.mygame_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 2:
                ((ImageView)convertView.findViewById(R.id.mygame_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.mygame_p3)).setImageResource(R.drawable.account_fade);
                ((ImageView)convertView.findViewById(R.id.mygame_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 3:
                ((ImageView)convertView.findViewById(R.id.mygame_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.mygame_p3)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.mygame_p4)).setImageResource(R.drawable.account_fade);
                break;
            case 4:
                ((ImageView)convertView.findViewById(R.id.mygame_p2)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.mygame_p3)).setImageResource(R.drawable.account);
                ((ImageView)convertView.findViewById(R.id.mygame_p4)).setImageResource(R.drawable.account);
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
