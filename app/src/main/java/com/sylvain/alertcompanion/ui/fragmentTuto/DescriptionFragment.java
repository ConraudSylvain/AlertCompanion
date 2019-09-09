package com.sylvain.alertcompanion.ui.fragmentTuto;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sylvain.alertcompanion.R;


public class DescriptionFragment extends Fragment {

    public DescriptionFragment() {
        // Required empty public constructor
    }

    static DescriptionFragment newInstance() {
        return new DescriptionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonNext = view.findViewById(R.id.fragment_description_button_arrow);
        buttonNext.setOnClickListener(v -> ViewPagerTutoAdapter.mViewPager.setCurrentItem(ViewPagerTutoAdapter.mViewPager.getCurrentItem() + 1));
    }
}
