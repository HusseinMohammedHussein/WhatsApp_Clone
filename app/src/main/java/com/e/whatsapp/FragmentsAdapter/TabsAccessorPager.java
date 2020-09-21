package com.e.whatsapp.FragmentsAdapter;
// Created by Hussein on 7/23/2020.

import com.e.whatsapp.Fragments.ChatsFragment;
import com.e.whatsapp.Fragments.ContactsFragment;
import com.e.whatsapp.Fragments.GroupFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorPager extends FragmentPagerAdapter {

    public TabsAccessorPager(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new GroupFragment();
            case 2:
                return new ContactsFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Group";
            case 2:
                return "Contacts";
            default:
                return null;
        }

    }
}
