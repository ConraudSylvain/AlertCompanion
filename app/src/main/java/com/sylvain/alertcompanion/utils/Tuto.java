package com.sylvain.alertcompanion.utils;

import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.sylvain.alertcompanion.ui.MainActivity;
import com.sylvain.alertcompanion.ui.fragmentTuto.ViewPagerTutoAdapter;

public class Tuto {

    private static ViewPager viewPagerTuto;

    public static void startTuto(MainActivity activity, ViewPager viewPager, ImageView buttonSos){
        viewPagerTuto = viewPager;
        buttonSos.setEnabled(false);
            viewPager.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ViewPagerTutoAdapter(activity.getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, viewPager));
    }

    public static void endTuto(MainActivity mainActivity){
        viewPagerTuto.setVisibility(View.GONE);
        mainActivity.buttonSos.setEnabled(true);
        mainActivity.configureDrawerlayout();
    }
}
