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
import com.sylvain.alertcompanion.utils.Permission;


public class PermissionFragment extends Fragment {

    public PermissionFragment() {
        // Required empty public constructor
    }
    static PermissionFragment newInstance() {
        PermissionFragment fragment = new PermissionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonOk = view.findViewById(R.id.fragment_permission_button_ok);
        buttonOk.setOnClickListener(v -> Permission.dexterPermission(getActivity()));
    }
}
