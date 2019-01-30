package com.memedomain.cachecleaner;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StorageInformation {
    public ArrayList<PackageInfoStruct> res;
    private Context context;

    public StorageInformation(Context context) {
        this.context = context;
    }

    public void getpackageSize() {
        AppDetails cAppDetails = new AppDetails((Activity) context);
        res = cAppDetails.getPackages();

        if (res == null) {
            return;
        }
        for (PackageInfoStruct re : res) {
            PackageManager pm = context.getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, re.pname, new cachePackState());
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private class cachePackState extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
            Log.w("Package Name", pStats.packageName + "");
            Log.i("Cache Size", pStats.cacheSize + "");

            Log.v("Data Size", pStats.dataSize + "");
            long packageSize = pStats.dataSize + pStats.cacheSize;
            Log.v("Total Cache Size", " " + packageSize);
            Log.v("APK Size", pStats.codeSize + "");
        }

    }
}
