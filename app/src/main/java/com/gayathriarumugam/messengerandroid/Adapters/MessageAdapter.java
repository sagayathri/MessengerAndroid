package com.gayathriarumugam.messengerandroid.Adapters;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.AsyncPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gayathriarumugam.messengerandroid.Model.Message;
import com.gayathriarumugam.messengerandroid.R;

import java.util.ArrayList;

import android.content.Context;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static int MESSAGE_LEFT = 0;
    private static int MESSAGE_RIGHT = 1;

    RecyclerView recyclerView;

    private ArrayList<Message> mData;
    private LayoutInflater mInflater;
    private String currentUser;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> data, String currentUser) {
        this.mData = data;
        this.context = context;
        this.currentUser = currentUser;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MESSAGE_RIGHT) {
            //If message is sent by current user
            view = mInflater.inflate(R.layout.message_row_right, parent, false);
        }
        else {
            //If message is sent by other users
            view = mInflater.inflate(R.layout.message_row_left, parent, false);
        }
        return new MessageAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Message message = mData.get(position);
        if (message.getContent() != null) {
            holder.sentText.setText(message.getContent());
            holder.sentText.setVisibility(View.VISIBLE);
            holder.sentImage.setVisibility(View.GONE);
        }
        else if (message.getDownloadURL() != null) {
            holder.sentText.setVisibility(View.GONE);
            holder.sentImage.setVisibility(View.VISIBLE);

            //Loads images from url
            Glide.with(context)
                .load(message.getDownloadURL())
                    .placeholder(R.drawable.loading)
                .centerCrop()
                .into(holder.sentImage);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setmData(ArrayList<Message> mData) {
        this.mData = mData;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentText;
        ImageView sentImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sentText = itemView.findViewById(R.id.sentText);
            sentImage = itemView.findViewById(R.id.sentImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mData.get(position).getSender().equals(currentUser) ) {
            return MESSAGE_RIGHT;
        }
        else {
            return MESSAGE_LEFT;
        }
    }
}
