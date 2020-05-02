package com.gayathriarumugam.messengerandroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gayathriarumugam.messengerandroid.Model.Topic;
import com.gayathriarumugam.messengerandroid.R;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private ArrayList<Topic> mData;
    private LayoutInflater mInflater;
    private Context context;
    private TopicAdapter.ItemClickListener mClickListener;

    public TopicAdapter(Context context, ArrayList<Topic> data) {
        this.context = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.topics_row_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = new Topic(mData.get(position).getId(), mData.get(position).getName());
        holder.topicName.setText(topic.getName());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView topicName;
        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);

            topicName = itemView.findViewById(R.id.topicItemName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(TopicAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

