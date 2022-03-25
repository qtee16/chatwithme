package com.example.chatwithme.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatwithme.ChatDetailActivity;
import com.example.chatwithme.Model.User;
import com.example.chatwithme.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    ArrayList<User> arrayUser;
    Context context;

    public UserAdapter(ArrayList<User> arrayUser, Context context) {
        this.arrayUser = arrayUser;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = arrayUser.get(position);
        Picasso.get()
                .load(user.getProfilePic())
                .placeholder(R.drawable.man)
                .into(holder.imgAvatarChat);
        holder.tvUsernameChat.setText(user.getUsername());

        if (user.getStatus().equals("online")) {
            holder.imgStatusChat.setVisibility(View.VISIBLE);
        } else {
            holder.imgStatusChat.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("username", user.getUsername());
                intent.putExtra("profilePic", user.getProfilePic());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStatusChat;
        CircleImageView imgAvatarChat;
        TextView tvUsernameChat, tvLastMsg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgStatusChat   = itemView.findViewById(R.id.imgStatusChat);
            imgAvatarChat   = itemView.findViewById(R.id.imgAvatarChat);
            tvUsernameChat  = itemView.findViewById(R.id.tvUsernameChat);
            tvLastMsg       = itemView.findViewById(R.id.tvLastMsg);
        }
    }
}
