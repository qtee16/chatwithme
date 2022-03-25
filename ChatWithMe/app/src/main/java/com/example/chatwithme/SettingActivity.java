package com.example.chatwithme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatwithme.Model.User;
import com.example.chatwithme.databinding.ActivityChatDetailBinding;
import com.example.chatwithme.databinding.ActivitySettingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding bd;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    final int REQUEST_CODE_IMAGE = 123;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        database.getReference().child("Users").child(mAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        username = user.getUsername();
                        Picasso.get()
                                .load(user.getProfilePic())
                                .placeholder(R.drawable.man)
                                .into(bd.imgAvtSetting);

                        bd.tvUsernameSetting.setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        bd.imgBackSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bd.imgAddSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        bd.imgEditSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    public void showEditDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_username);

        EditText edtName = (EditText) dialog.findViewById(R.id.edtUsernameDialog);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdateDialog);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelDialog);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(SettingActivity.this, "Must fill your username", Toast.LENGTH_SHORT).show();
                } else {
                    database.getReference().child("Users")
                            .child(mAuth.getUid())
                            .child("username")
                            .setValue(name);
                    bd.tvUsernameSetting.setText(name);
                    Toast.makeText(SettingActivity.this, "You've just changed your username", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null) {
            Uri sFile = data.getData();
            bd.imgAvtSetting.setImageURI(sFile);

            final StorageReference reference = storage.getReference().child("profile_pic")
                    .child(mAuth.getUid());

            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Users").child(mAuth.getUid())
                                    .child("profilePic").setValue(uri.toString());
                        }
                    });
                }
            });
        }
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