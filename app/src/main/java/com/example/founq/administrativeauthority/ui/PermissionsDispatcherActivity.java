package com.example.founq.administrativeauthority.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.founq.administrativeauthority.R;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class PermissionsDispatcherActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnGetSingle,btnGetMult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_test);

        btnGetSingle = findViewById(R.id.btn_get_single);
        btnGetMult = findViewById(R.id.btn_get_mult);

        btnGetSingle.setOnClickListener(this);
        btnGetMult.setOnClickListener(this);

    }

    @NeedsPermission(Manifest.permission.READ_SMS)
    public void getSinglePermission() {
        Toast.makeText(this,"获取一个权限",Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission({Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE})
    public void getMultPermission() {
        Toast.makeText(this,"获取多个权限",Toast.LENGTH_SHORT).show();
    }

    @OnShowRationale({Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE})
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage("使用此功能需要WRITE_CONTACTS和CALL_PHONE权限，下一步将继续请求权限")
                .setPositiveButton("下一步", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();//继续执行请求
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                request.cancel();//取消执行请求
            }
        }).show();
    }

    @OnPermissionDenied({Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE})
    public void multiDenied() {
        showPermissionDialog();
    }

    @OnNeverAskAgain({Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE})
    public void multiNeverAsk() {
        Toast.makeText(this, "已拒绝一个或以上权限，并不再询问", Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.READ_SMS)
    public void singleNeverAsk(){
        Toast.makeText(this, "已拒绝READ_SMS权限，并不再询问", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsDispatcherActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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
                            Toast.makeText(PermissionsDispatcherActivity.this,"仍有权限未被允许",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_single:
                PermissionsDispatcherActivityPermissionsDispatcher.getSinglePermissionWithPermissionCheck(this);
                break;
            case R.id.btn_get_mult:
                PermissionsDispatcherActivityPermissionsDispatcher.getMultPermissionWithPermissionCheck(this);
                break;
                default:
                    break;
        }
    }
}
