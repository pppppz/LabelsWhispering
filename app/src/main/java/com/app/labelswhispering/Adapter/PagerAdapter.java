package com.app.labelswhispering.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.labelswhispering.Medicine_box_fragment;
import com.app.labelswhispering.Schedule_Fragment;
import com.app.labelswhispering.User_Fragment;
import com.app.labelswhispering.main_search_fragment;

public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new main_search_fragment();
            case 1:
                return new Medicine_box_fragment();
            case 2:
                return new Schedule_Fragment();
            case 3:
                return new User_Fragment();
            default:
                return new main_search_fragment();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}