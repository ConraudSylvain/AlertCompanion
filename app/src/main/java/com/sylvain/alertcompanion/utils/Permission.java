package com.sylvain.alertcompanion.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.ui.fragmentTuto.ViewPagerTutoAdapter;
import java.util.List;


public class Permission {

    public static void dexterPermission(Activity activity){
        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat. checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            ViewPagerTutoAdapter.mViewPager.setCurrentItem(ViewPagerTutoAdapter.mViewPager.getCurrentItem() + 1);
            return;
        }

        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    ViewPagerTutoAdapter.mViewPager.setCurrentItem(2);
                }else{
                    displayAlertDialogExit(activity);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();

                }
        }).onSameThread().check();
    }

    private static void displayAlertDialogExit(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.required_permission))
                .setMessage(activity.getResources().getString(R.string.for_use_application_you_must_allow_all_permission))
                .setPositiveButton(activity.getResources().getString(R.string.allow_permission), (dialog, which) -> dexterPermission(activity))
                .setNegativeButton(activity.getResources().getString(R.string.quit_application), (dialog, which) -> activity.finish()).show();
    }
}
