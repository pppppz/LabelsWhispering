package com.app.labelswhispering.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.labelswhispering.DetailFragment.HowKeep_Fragment;
import com.app.labelswhispering.DetailFragment.HowToTake_Fragment;
import com.app.labelswhispering.DetailFragment.If_Forget_Fragment;
import com.app.labelswhispering.DetailFragment.Picture_Fragment;
import com.app.labelswhispering.DetailFragment.Property_Fragment;
import com.app.labelswhispering.DetailFragment.SideEffect_Fragment;

public class PagerInMoreDetailAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerInMoreDetailAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new Property_Fragment();
            case 1:
                return new HowToTake_Fragment();
            case 2:
                return new If_Forget_Fragment();
            case 3:
                return new SideEffect_Fragment();
            case 4:
                return new HowKeep_Fragment();
            case 5:
                return new Picture_Fragment();
            default:
                return new Property_Fragment();
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}