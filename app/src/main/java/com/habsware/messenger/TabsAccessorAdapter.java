package com.habsware.messenger;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    /*
        Getting the Fragment
    */
    @Override
    public Fragment getItem(int i) {
        switch(i){

            case 0:
                return new ContactsFragment();
            case 1:
                return new GroupFragment();
            case 2:
                return new RequestsFragment();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    /*
        Getting the page title for the tabs
    */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){

            case 0:
                return "Contacts";
            case 1:
                return "Groups";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }
}
