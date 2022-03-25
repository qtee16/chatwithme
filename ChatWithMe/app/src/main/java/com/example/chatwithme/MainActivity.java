package com.example.chatwithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatwithme.Adapter.FragmentAdapter;
import com.example.chatwithme.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding bd;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());

        mAuth = FirebaseAuth.getInstance();

        bd.viewPagerMain.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        bd.tabLayoutMain.setupWithViewPager(bd.viewPagerMain);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(MainActivity.this);
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.setting:
//                Toast.makeText(MainActivity.this, "Setting", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent2);
                break;
            case R.id.logout:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
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