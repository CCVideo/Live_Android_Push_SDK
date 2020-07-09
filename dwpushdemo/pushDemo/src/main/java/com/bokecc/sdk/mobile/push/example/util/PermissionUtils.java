package com.bokecc.sdk.mobile.push.example.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class PermissionUtils {
    private static String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * 检测是集合权限是否开启
     *
     * @param ctx
     * @return true-已开启权限，false-没有权限
     */
    public static boolean permitPermissions(Context ctx) {
        for (String permission : permissions) {
            if (permitPermission(ctx, permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean permitPermission(Context ctx, String permission) {
        if (ctx == null) return false;
        return ContextCompat.checkSelfPermission(ctx, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查悬浮窗权限
     *
     * @param context Activity
     * @return true 已开启   false 未开启
     */
    public static boolean checkOverlayPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            SettingsCompat.setDrawOverlays(context, true);
            if (!SettingsCompat.canDrawOverlays(context)) {
                Toast.makeText(context, "需要悬浮窗权限", Toast.LENGTH_LONG).show();
                SettingsCompat.manageDrawOverlays(context);
                return false;
            }
        }
        return true;
    }

}
