package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.model.Posts;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder>{

    private ArrayList<Posts> posts;
    private RecyclerViewClickListener mListener;


    public PostsAdapter(ArrayList<Posts> posts, RecyclerViewClickListener listener) {
        this.posts = posts;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{ //implements View.OnClickListener{
        private TextView body, title;
        private RecyclerViewClickListener mListener;

        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
           // view.setOnClickListener(this);
            title =  view.findViewById(R.id.titleText);
            body =  view.findViewById(R.id.bodyText);
        }

       /* @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }*/
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Posts post = posts.get(position);

        String t = post.getTitle();
        String b = post.getBody();

        holder.title.setText(t);
        holder.body.setText(b);


      /*  if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#EDE7F6"));
        }*/


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
