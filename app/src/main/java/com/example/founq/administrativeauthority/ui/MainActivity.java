package com.example.founq.administrativeauthority.ui;

import android.Manifest;
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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.founq.administrativeauthority.R;
import com.example.founq.administrativeauthority.util.CheckPermissions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnClick;

    //声明一个permissions数组，将需要的权限都放在里面
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA};
    //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPermissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(Build.VERSION.SDK_INT >= 23 ){
            initPermission();
        }
//        CheckPermissions checkPermissions = new CheckPermissions(this,permissions);

        btnClick = findViewById(R.id.btn_clickk);
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PermissionsDispatcherActivity.class));
            }
        });
    }


    private void initPermission() {
        mPermissionList.clear();
        for(int i = 0; i < permissions.length;i++){
            if(ContextCompat.checkSelfPermission(this,permissions[i] )!= PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.size() > 0 ){
            ActivityCompat.requestPermissions(this,permissions,mRequestCode);
        } else {
            Toast.makeText(MainActivity.this,"经检查发现所有权限都被允许了",Toast.LENGTH_SHORT).show();
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
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("有权限被禁用，修改请手动设置")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            Toast.makeText(MainActivity.this,"仍有权限未被允许",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }
}
