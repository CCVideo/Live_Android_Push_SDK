package com.bokecc.sdk.mobile.push.example.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

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
}
