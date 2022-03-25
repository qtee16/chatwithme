package com.example.chatwithme.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatwithme.Model.Message;
import com.example.chatwithme.Model.User;
import com.example.chatwithme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<Message> arrayMsg;
    Context context;
    String receiverId;
    FirebaseDatabase database;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;

    public ChatAdapter(ArrayList<Message> arrayMsg, Context context) {
        this.arrayMsg = arrayMsg;
        this.context = context;
    }

    public ChatAdapter(ArrayList<Message> arrayMsg, Context context, String receiverId) {
        this.arrayMsg = arrayMsg;
        this.context = context;
        this.receiverId = receiverId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayMsg.get(position).getuId().equals(mAuth.getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = arrayMsg.get(position);


        if (holder.getClass() == SenderViewHolder.class) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Delete");
                    dialog.setMessage("Do you want to delete this message ?");

                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
                            String receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            database.getReference().child("Chats")
                                    .child(senderRoom)
                                    .child(message.getCheckId())
                                    .setValue(null);

                            database.getReference().child("Chats")
                                    .child(receiverRoom)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot snap : snapshot.getChildren()) {
                                                Message msg = snap.getValue(Message.class);
                                                if (msg.getCheckId().equals(message.getCheckId())) {
                                                    snap.getRef().removeValue();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    });

                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    dialog.show();
                    return false;
                }
            });
            ((SenderViewHolder) holder).tvSenderChat.setText(message.getMessage());

            Date date = new Date(message.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(date);
            ((SenderViewHolder) holder).tvSenderTime.setText(time.toString());

        } else {
            ((ReceiverViewHolder) holder).tvReceiverChat.setText(message.getMessage());

            Date date = new Date(message.getTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(date);
            ((ReceiverViewHolder) holder).tvReceiverTime.setText(time.toString());

            database = FirebaseDatabase.getInstance();

            database.getReference().child("Users")
                    .child(receiverId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get().load(user.getProfilePic())
                                    .placeholder(R.drawable.man)
                                    .into(((ReceiverViewHolder) holder).imgAvtReceiverChat);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return arrayMsg.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverChat, tvReceiverTime;
        CircleImageView imgAvtReceiverChat;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReceiverChat      = itemView.findViewById(R.id.tvReceiverChat);
            tvReceiverTime      = itemView.findViewById(R.id.tvReceiverTime);
            imgAvtReceiverChat  = itemView.findViewById(R.id.imgAvtReceiverChat);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderChat, tvSenderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSenderChat = itemView.findViewById(R.id.tvSenderChat);
            tvSenderTime = itemView.findViewById(R.id.tvSenderTime);
        }
    }
}
