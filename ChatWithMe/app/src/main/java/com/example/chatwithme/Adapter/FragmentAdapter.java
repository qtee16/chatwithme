package com.example.chatwithme.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatwithme.Fragments.CallsFragment;
import com.example.chatwithme.Fragments.ChatsFragment;
import com.example.chatwithme.Fragments.StatusFragment;

public class FragmentAdapter extends FragmentPagerAdapter {
    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new ChatsFragment();
            case 1: return new StatusFragment();
            case 2: return new CallsFragment();
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String title;
        if (position == 0) {
            title = "CHATS";
        } else if (position == 1) {
            title = "STATUS";
        } else {
            title = "CALLS";
        }
        return title;
    }
}
