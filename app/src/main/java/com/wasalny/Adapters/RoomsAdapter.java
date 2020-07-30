package com.wasalny.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.wasalny.Activities.ChatActivity;
import com.wasalny.Model.Room;
import com.wasalny.R;

import java.util.ArrayList;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder>{

    ArrayList<Room> roomsArrayList;
    Context context;
    Intent intent;

    public RoomsAdapter(ArrayList<Room> roomsArrayList, Context context) {
        this.roomsArrayList = roomsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.room_holder, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        holder.roomTitle.setText(roomsArrayList.get(i).getRoomTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, ChatActivity.class);
                intent.putExtra("room", roomsArrayList.get(i));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView roomTitle;
        ImageView roomImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
        }
    }
}