package com.bokecc.sdk.mobile.push.example.logging;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.HmacSHA1Signature;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.bokecc.sdk.mobile.push.example.DWApplication;
import com.bokecc.sdk.mobile.push.example.util.PermissionUtils;
import com.bokecc.sdk.mobile.push.global.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by dds on 2020/4/3.
 * 日志上传到阿里云
 */
public class LogReporter2 {
    public static final String TAG = LogReporter2.class.getSimpleName();
    public static Context context;

    public static void initReport(Context ctx) {
        if (!DWApplication.mReportLog) {
            return;
        }
        if (PermissionUtils.permitPermissions(ctx)) {
            context = ctx.getApplicationContext();
            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            sb.append(File.separator);
            sb.append("ccpushlog/");
            final File fileParent = new File(sb.toString());
            new Thread(new Runnable() {
                public void run() {
                    OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
                        public String signContent(String content) {
                            String shaSignature = new HmacSHA1Signature().computeSignature(Constant.getAccessKeySecret(), content);
                            StringBuilder sb = new StringBuilder();
                            sb.append("OSS ");
                            sb.append(Constant.getAccessKeyId());
                            sb.append(":");
                            sb.append(shaSignature);
                            return sb.toString();
                        }
                    };
                    ClientConfiguration conf = new ClientConfiguration();
                    conf.setConnectionTimeout(15000);
                    conf.setSocketTimeout(15000);
                    conf.setMaxConcurrentRequest(5);
                    conf.setMaxErrorRetry(2);
                    Log.d(TAG, "initCrashReport: start");
                    OSSClient mOSS = new OSSClient(context, Constant.getEndpoint(), credentialProvider, conf);
                    File[] fs = fileParent.listFiles();
                    if (fs != null) {
                        for (File file : fs) {
                            LogReporter2.uploadFile(mOSS, toZip(file));
                        }
                    }
                }
            }).start();
        }
    }


    public static File toZip(File srcFile) {
        if (srcFile == null) {
            return null;
        }
        String parentPath = srcFile.getParent();
        String srcName = srcFile.getName();
        String[] nameArray = srcName.split("\\.");
        if (nameArray.length < 2) {
            return null;
        }
        if ("zip".equals(nameArray[1])) {
            return srcFile;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(nameArray[0]);
        sb.append(".zip");
        String zipName = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(parentPath);
        sb2.append("/");
        sb2.append(zipName);
        File zipFile = new File(sb2.toString());
        try {
            OutputStream os2 = new FileOutputStream(zipFile);
            FileInputStream in2 = new FileInputStream(srcFile);
            ZipOutputStream zos2 = new ZipOutputStream(os2);
            zos2.putNextEntry(new ZipEntry(srcName));
            byte[] buf = new byte[1024];
            while (true) {
                int read = in2.read(buf);
                int len = read;
                if (read != -1) {
                    zos2.write(buf, 0, len);
                } else {
                    break;
                }
            }
            in2.close();
            zos2.close();
            os2.close();
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        } catch (Throwable th) {
            throw th;
        }
        srcFile.delete();
        return zipFile;
    }

    /* access modifiers changed from: private */
    public static void uploadFile(OSSClient oos, final File file) {

    }
}