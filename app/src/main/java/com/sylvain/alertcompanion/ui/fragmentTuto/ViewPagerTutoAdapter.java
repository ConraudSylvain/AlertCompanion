package com.sylvain.alertcompanion.ui.fragmentTuto;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class ViewPagerTutoAdapter extends FragmentPagerAdapter {

    public static ViewPager mViewPager;

    public ViewPagerTutoAdapter(@NonNull FragmentManager fm, int behavior, ViewPager viewPager) {
        super(fm, behavior);
        mViewPager = viewPager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0 : fragment = WelcomeFragment.newInstance(); break;
            case 1 : fragment = PermissionFragment.newInstance(); break;
            case 2 : fragment = DescriptionFragment.newInstance(); break;
            case 3 : fragment = ContactFragment.newInstance(); break;
            case 4 : fragment = EndTutoFragment.newInstance();break;
            default: throw new IllegalArgumentException();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
