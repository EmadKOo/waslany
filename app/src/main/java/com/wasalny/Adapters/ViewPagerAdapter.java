package com.wasalny.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wasalny.Fragments.CurrentJourneyFragment;
import com.wasalny.Fragments.CurrentsOffersFragment;
import com.wasalny.Fragments.PreviousOffersFragment;
import com.wasalny.Fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new CurrentsOffersFragment();
            case 1:
                return new CurrentJourneyFragment();
            case 2:
                return new PreviousOffersFragment();
            case 3:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Offers";
            case 1: return "Current Journey";
            case 2: return "Previous Offers";
            case 3: return "Profile";
            default:return null;
        }
    }
}


