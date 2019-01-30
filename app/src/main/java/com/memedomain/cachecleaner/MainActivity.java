package com.memedomain.cachecleaner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.*;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private MyTouchListener mTouch;
    private int i = 0;
    private boolean checked = false;
    private int progressStatus = 0;

    private String uuid;

    int SPLASH_DISPLAY_TIME = 1000;
    private ShakeListener mShaker;

    private List<AppStruct> appStructs;
    private List<ResolveInfo> infos;

    private Toast toastObject;
    final static public String BASE_URL = "https://1c605919.ngrok.io/api/";
//    final private String BASE_URL = "http://localhost:8080/api/";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared = this.getPreferences(Context.MODE_PRIVATE);
        if (!shared.contains(getString(R.string.pref_file_key))) {
            uuid = String.valueOf(new Date().hashCode());
            Log.d(TAG, "onCreate: setHash: " + uuid);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(getString(R.string.pref_file_key), uuid);
            editor.apply();
        } else {
            uuid = shared.getString(getString(R.string.pref_file_key), null);
            Log.d(TAG, "onCreate: getHash: " + uuid);
        }


        initTouch();
        sensorManager();
    }

    private void sensorManager() {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                vibrator.vibrate(100);
                showToast("Shake detected");
                trash_em_all();
            }
        });
    }

    private void initTouch() {

        appStructs = new LinkedList<>();
        infos = new LinkedList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Log.d(TAG, "onCreate: After recyclerView init");
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        infos = getPackageManager().queryIntentActivities(mainIntent, 0);
//        showToast("Number of apps: " + infos.size());

        appStructs.clear();
        mAdapter = new RecyclerVievAdapted(appStructs, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAppStructs();
            }
        });

        mTouch = new MyTouchListener(getApplicationContext(), mRecyclerView, new MyTouchListener.OnTouchActionListener() {
            @Override
            public void onLeftSwipe(View view, int position) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                MainActivity.this.finish();
                overridePendingTransition(R.anim.fadein_left, R.anim.fadeout_left);
            }

            @Override
            public void onRightSwipe(View view, int position) {

            }

            @Override
            public void onClick(View view, int position) {
                List<AppStruct> items = ((RecyclerVievAdapted) mAdapter).getList();
                AppStruct item = items.get(position);
                Toast.makeText(getApplicationContext(), "Number of clicked row: " + position, Toast.LENGTH_SHORT).show();
                String packageName = item.info.activityInfo.packageName;
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null));
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, );
//        } else {
//            //TODO
//        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        String id = android.telephony.TelephonyManager.getDeviceId();
//        showToast(getDeviceIMEI());
    }

    @Override
    protected void onResume() {
        mShaker.resume();
        super.onResume();
        updateAppStructs();
//        showToast("onResume");
    }

    @Override
    protected void onPause() {
        mShaker.pause();
        super.onPause();
//        showToast("onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        showToast("onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        showToast("onDestroy");
    }

    @SuppressLint("StaticFieldLeak")
    private synchronized void updateAppStructs() {
        appStructs.clear();
        mAdapter.notifyDataSetChanged();
        progressBar.setMax(infos.size());
        mRecyclerView.removeOnItemTouchListener(mTouch);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                PackageManager pm = getPackageManager();
                try {
                    Method getPackageSizeInfo = pm.getClass().getMethod(
                            "getPackageSizeInfo", String.class, IPackageStatsObserver.class);

                    progressStatus = 0;
                    progressBar.setProgress(progressStatus);

                    for (final ResolveInfo inf : infos) {
                        getPackageSizeInfo.invoke(pm, inf.activityInfo.packageName, new IPackageStatsObserver.Stub() {
                            @Override
                            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {

                                i++;
                                AppStruct app = new AppStruct();
                                app.cacheSize = pStats.cacheSize;
                                app.info = inf;
                                if (pStats.cacheSize > 0) {
                                    appStructs.add(app);
                                }
                                if (i >= infos.size()) {
                                }
                                progressStatus++;
                                progressBar.setProgress(progressStatus);
                            }
                        });
                    }
                    while (i < infos.size()) {
                        Thread.sleep(10);
                    }
                    i = 0;


                } catch (NoSuchMethodException | SecurityException | IllegalArgumentException
                        | IllegalAccessException | InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                swipeContainer.setRefreshing(false);
                Collections.sort(appStructs);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.addOnItemTouchListener(mTouch);
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh:
                updateAppStructs();
            case R.id.doThey:
                appStructs.clear();
                mAdapter.notifyDataSetChanged();
                mRecyclerView.removeOnItemTouchListener(mTouch);
            case R.id.trash:
                trash_em_all();
            case R.id.settings:
                showInputDialog();
        }
        return true;
    }

    private void trash_em_all() {
        showToast(getResources().getString(R.string.remove_cache));

        String macAddress = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
        Log.d(TAG, "trash_em_all: " + macAddress);
        long temp = 0;
        for (AppStruct app : appStructs) {
            temp += app.cacheSize;
        }
        addSala(new Sala(uuid, (int) temp));

        Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    private void showInputDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.input_dialog, null);
        dialogBuilder.setView(dialogView);

        final CheckBox input = dialogView.findViewById(R.id.checkbox);
        input.setChecked(checked);

        dialogBuilder.setTitle(R.string.dialogTitle);
//        dialogBuilder.setMessage("Some message");
        dialogBuilder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.isChecked()) {
                    checked = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    checked = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void addSala(Sala sala) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        final OpenLibraryClient openLibraryClient = retrofit.create(OpenLibraryClient.class);

        Call<Sala> call = openLibraryClient.postSalas(sala);

        call.enqueue(new Callback<Sala>() {
            @Override
            public void onResponse(Call<Sala> call, Response<Sala> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Code: " + response.message());
                    return;
                }
                Log.d(TAG, "onResponse: Positive " + response.body().getId());
            }

            @Override
            public void onFailure(Call<Sala> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void showToast(String msg) {
        if (toastObject != null)
            toastObject.cancel();
        toastObject = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toastObject.show();
    }

    private void showToast(int id) {
        String msg = getResources().getString(id);
        if (toastObject != null)
            toastObject.cancel();
        toastObject = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        toastObject.show();
    }
}

class AppStruct implements Comparable<AppStruct> {
    ResolveInfo info;
    long cacheSize;

    @Override
    public int compareTo(@NonNull AppStruct o) {
        return Long.compare(o.cacheSize, this.cacheSize);
    }
}

class ShakeListener implements SensorListener {

    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private SensorManager sensorManager;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private OnShakeListener mShakeListener;
    private Context mContext;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    public interface OnShakeListener {
        public void onShake();
    }

    ShakeListener(Context context) {
        mContext = context;
        resume();
    }

    void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    void resume() {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            throw new UnsupportedOperationException(mContext.getResources().getString(R.string.except_one));
        }
        boolean supported = sensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        if (!supported) {
            sensorManager.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            throw new UnsupportedOperationException(mContext.getResources().getString(R.string.except_two));
        }
    }

    void pause() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, SensorManager.SENSOR_ACCELEROMETER);
            sensorManager = null;
        }
    }


    @Override
    public void onSensorChanged(int sensor, float[] values) {

        if (sensor != SensorManager.SENSOR_ACCELEROMETER) return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X] + values[SensorManager.DATA_Y] + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    if (mShakeListener != null) {
                        mShakeListener.onShake();
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
