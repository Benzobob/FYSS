package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fyss.R;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy1;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy2;
import com.fyss.controller.ui.dashboard.fy.fragment.FragDashFy3;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterFy extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapterFy(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
       // return PlaceholderFragment.newInstance(position + 1);
        Fragment frag = null;
        switch(position){
            case 0:
                frag = FragDashFy2.newInstance();
                break;
            case 1:
                frag = FragDashFy1.newInstance();
                break;
            case 2:
                frag = FragDashFy3.newInstance();
        }
        return frag;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}