package com.example.chatwithme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatwithme.Adapter.UserAdapter;
import com.example.chatwithme.Model.User;
import com.example.chatwithme.R;
import com.example.chatwithme.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    FragmentChatsBinding bd;
    ArrayList<User> arrayUser = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bd = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        UserAdapter adapter = new UserAdapter(arrayUser, getContext());
        bd.rvChat.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        bd.rvChat.setLayoutManager(linearLayoutManager);

        database.getReference()
                .child("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayUser.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (!snap.getKey().equals(mAuth.getUid())) {
                                User user = snap.getValue(User.class);
                                user.setUserId(snap.getKey());
                                arrayUser.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return bd.getRoot();
    }
}