package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;
    int i = 0;
    private int progressStatus = 0;

    int SPLASH_DISPLAY_TIME=10;

    List<Adres> adresy;
    List<AppStruct> appStructs;
    List<ResolveInfo> infos;

    private Toast toastObject;
    final private String BASE_URL = "https://887361cc.ngrok.io/api/";
//    final private String BASE_URL = "http://localhost:8080/api/";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adresy = new LinkedList<>();
        appStructs = new LinkedList<>();
        infos = new LinkedList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Log.d(TAG, "onCreate: After recyclerView init");
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        infos = getPackageManager().queryIntentActivities(mainIntent, 0);
        showToast("Number of apps: " + infos.size());

        mAdapter = new RecyclerVievAdapted(appStructs, MainActivity.this);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                showToast("Swipe Left detected");
//                System.out.println("SMTH");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        intent.putExtra("id", "1");
                        startActivity(intent);
                        MainActivity.this.finish();
//                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }, SPLASH_DISPLAY_TIME);
            }
        });
    }

    private synchronized void updateAppStructs() throws InterruptedException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appStructs.clear();
                PackageManager pm = getPackageManager();
                try {
                    Method getPackageSizeInfo = pm.getClass().getMethod(
                            "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                    progressStatus = 0;

//                    progressBar.setProgress(progressStatus);
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
                                    Log.d(TAG, "onGetStatsCompleted: After add");
                                    appStructs.add(app);
                                }
                                if (i >= infos.size()) {
                                    Log.d(TAG, "onGetStatsCompleted: Notified all " + i);
                                }
                                progressStatus++;
                            }
                        });
                    }
                    while (i < infos.size()) {
                        Thread.sleep(10);
                    }
                    i = 0;
                    Collections.sort(appStructs);
                    mAdapter.notifyDataSetChanged();


                } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    Log.e(TAG, "updateAppStructs: ", e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        });
        Collections.sort(appStructs);
        Log.d(TAG, "updateAppStructs: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            Log.d(TAG, "onOptionsItemSelected: After clear: " + appStructs.size());
            try {
                updateAppStructs();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onOptionsItemSelected: After update: " + appStructs.size());
        } else if (id == R.id.doThey) {
            appStructs.clear();
            mAdapter.notifyDataSetChanged();
//            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
//            showInputDialog();
        }
        return true;
    }

    private void showInputDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.input_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.queryInput);

        dialogBuilder.setTitle("Do they live in that country?");
        dialogBuilder.setMessage("Some message");
        dialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                doTheyLiveInCountry(input.getText().toString());
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void getAddresses() {
        adresy.clear();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        final OpenLibraryClient openLibraryClient = retrofit.create(OpenLibraryClient.class);

        Call<List<Adres>> call = openLibraryClient.getAdreses();

        call.enqueue(new Callback<List<Adres>>() {
            @Override
            public void onResponse(Call<List<Adres>> call, Response<List<Adres>> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Code: " + response.code());
                    return;
                }
                List<Adres> temp = response.body();
                for (Adres adres : temp) {
                    adresy.add(mAdapter.getItemCount(), adres);
                    Log.d(TAG, "onResponse: Success\nObject Woj:" + adres.getWojewodztwo());
//                    showToast("Object Woj: " + adres.getWojewodztwo());
                }

                mAdapter.notifyDataSetChanged();
//                refresh();
            }

            @Override
            public void onFailure(Call<List<Adres>> call, Throwable t) {
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
