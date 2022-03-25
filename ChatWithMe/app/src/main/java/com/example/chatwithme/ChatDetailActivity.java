package com.example.chatwithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chatwithme.Adapter.ChatAdapter;
import com.example.chatwithme.Model.Message;
import com.example.chatwithme.Model.User;
import com.example.chatwithme.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding bd;
    FirebaseAuth mAuth;
    FirebaseDatabase database;

    ArrayList<Message> arrayMsg;
    ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();

        final String senderId   = mAuth.getUid();
        String receiverId       = intent.getStringExtra("userId");
        String username         = intent.getStringExtra("username");
        String profilePic       = intent.getStringExtra("profilePic");

        arrayMsg = new ArrayList<>();

        bd.rvChatDetail.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        bd.rvChatDetail.setLayoutManager(linearLayoutManager);

        String senderRoom   = senderId + receiverId;
        String receiverRoom = receiverId + senderId;

        bd.imgBackChatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailActivity.this, MainActivity.class);
                finish();
            }
        });

        database.getReference().child("Users")
                .child(receiverId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        bd.tvUsernameChatDetail.setText(user.getUsername());

                        if (user.getStatus().equals("online")) {
                            bd.imgStatusChatDetail.setVisibility(View.VISIBLE);
                        } else {
                            bd.imgStatusChatDetail.setVisibility(View.INVISIBLE);
                        }

                        Picasso.get()
                                .load(profilePic)
                                .placeholder(R.drawable.man)
                                .into(bd.imgAvtChatDetail);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayMsg.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Message msg = snap.getValue(Message.class);
                            msg.setMessageId(snap.getKey());
                            arrayMsg.add(msg);
                        }
                        adapter = new ChatAdapter(arrayMsg, ChatDetailActivity.this, receiverId);
                        bd.rvChatDetail.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        bd.imgSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = bd.edtEnterMsg.getText().toString().trim();
                if (!msg.isEmpty()) {
                    Message message = new Message(senderId, msg);
                    message.setTimestamp(new Date().getTime());
                    bd.edtEnterMsg.setText("");

                    String msgId = database.getReference().child("Chats")
                                    .child(senderRoom)
                                    .push()
                                    .getKey();

                    message.setCheckId(msgId);

                    database.getReference().child("Chats")
                            .child(senderRoom)
                            .child(msgId)
                            .setValue(message)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.getReference().child("Chats")
                                            .child(receiverRoom)
                                            .push()
                                            .setValue(message)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                }
                            });
                }

            }
        });

    }

    private void setStatus(String status) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }
}