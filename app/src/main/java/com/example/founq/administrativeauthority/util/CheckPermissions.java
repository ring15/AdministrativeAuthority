package com.example.founq.administrativeauthority.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import com.example.founq.administrativeauthority.base.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class CheckPermissions extends AppCompatActivity {

    Context context;
    String[] permissions;


    //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPermissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码

    public CheckPermissions(Context context, String[] permissions) {
        this.context = context;
        this.permissions = permissions;
        if(Build.VERSION.SDK_INT >= 23 ){
            initPermission();
        }
    }


    private void initPermission() {
        mPermissionList.clear();
        for(int i = 0; i < permissions.length;i++){
            if(ContextCompat.checkSelfPermission(context,permissions[i] )!= PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.size() > 0 ){
            ActivityCompat.requestPermissions((Activity) context,permissions,mRequestCode);
        } else {
            Toast.makeText(context,"经检查发现所有权限都被允许了",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;
        if(mRequestCode == requestCode) {
            for(int i = 0; i < grantResults.length; i++){
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
            if(hasPermissionDismiss) {
                showPermissionDialog();
            } else {
                Toast.makeText(this,"设置后所有权限都被允许了",Toast.LENGTH_SHORT).show();
            }
        }
    }

    AlertDialog mPermissionDialog;
    String mPackName = "com.example.founq.administrativeauthority";

    private void showPermissionDialog() {
        if(mPermissionDialog == null){
            mPermissionDialog = new AlertDialog.Builder(context)
                    .setMessage("有权限被禁用，修改请手动设置")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            Toast.makeText(context,"仍有权限未被允许",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

}
