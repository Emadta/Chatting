package com.example.pcc.chatting;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


class SectionPageAdapter extends FragmentPagerAdapter {
    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new FriendsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public  CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Friends";
            default:
                return null;

        }
    }
}
