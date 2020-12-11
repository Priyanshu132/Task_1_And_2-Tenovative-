package com.example.task_1;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<RecycleViewClass> list;
    Context context;
    private RecyclerViewClickListner recyclerViewClickListner;

    public Adapter(ArrayList<RecycleViewClass> list, Context context, RecyclerViewClickListner listner) {
        this.list = list;
        this.context = context;
        this.recyclerViewClickListner = listner;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
        return new Adapter.ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.name.setText(list.get(position).getName());
        holder.name.setTextColor(Color.parseColor("#1b1da6"));
        holder.likes.setText(list.get(position).getLikes() + " Likes");
        if (Integer.parseInt(list.get(position).getStatus()) == 1) {
            holder.likes.setTextColor(Color.parseColor("#4187D0"));
            holder.thumb.setImageResource(R.drawable.ic_baseline_thumb_up_alt);
        }
        Picasso.with(context).load(list.get(position).getImageLink()).into(holder.cir);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, likes;
        ImageView cir, thumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            cir = itemView.findViewById(R.id.image);
            likes = itemView.findViewById(R.id.like);
            thumb = itemView.findViewById(R.id.thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            recyclerViewClickListner.onClick(v, getAdapterPosition());
        }
    }




    public interface RecyclerViewClickListner {
        void onClick(View v, int position);
    }
}

