package com.memedomain.cachecleaner;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StorageInformation {
    long packageSize = 0;
    AppDetails cAppDetails;
    public ArrayList<PackageInfoStruct> res;
    Context context;

    public StorageInformation(Context context) {
        this.context = context;
    }

    public void getpackageSize() {
        cAppDetails = new AppDetails((Activity) context);
        res = cAppDetails.getPackages();

        if (res == null) {
            return;
        }
        for (int i = 0; i < res.size(); i++) {
            PackageManager pm = context.getPackageManager();
            Method getPackageSizeInfo;
            try {
                getPackageSizeInfo = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(pm, res.get(i).pname, new cachePackState());
            } catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private class cachePackState extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            Log.w("Package Name", pStats.packageName + "");
            Log.i("Cache Size", pStats.cacheSize + "");

            Log.v("Data Size", pStats.dataSize + "");
            packageSize = pStats.dataSize + pStats.cacheSize;
            Log.v("Total Cache Size", " " + packageSize);
            Log.v("APK Size", pStats.codeSize + "");
        }

    }
}
