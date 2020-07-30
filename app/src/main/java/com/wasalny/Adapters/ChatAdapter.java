package com.wasalny.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wasalny.Model.Chat;
import com.wasalny.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    public static final int receiver = 0;
    public static final int sender = 1;

    public int flag = 1;
    public int flagSend;
    private Context mContext;
    private List<Chat> mChat;

    public ChatAdapter(Context mContext, List<Chat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
    }

    private static final String TAG = "ChatAdapter";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == sender) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            flagSend = 1;
            return new ChatAdapter.MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            flagSend = 0;
            return new ChatAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.show_message.setText(mChat.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (mChat.get(position).getFlag() == flag) {
            return sender;
        } else {
            return receiver;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
        }
    }
}
