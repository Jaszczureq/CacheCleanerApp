package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.LinkedList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    ConstraintLayout constraintLayout;
    private static final String TAG = "Main2Activity";
    private ShakeListener mShaker;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<String> strings;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        strings=new LinkedList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        mAdapter = new RecyclerViewAdapter(strings, Main2Activity.this);
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        getSalas();
        sensorManager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
    }


    private void sensorManager() {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                vibrator.vibrate(100);
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                Main2Activity.this.finish();
                overridePendingTransition(R.anim.fadein_right, R.anim.fadeout_right);
            }
        });
    }

    private void getSalas() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        final OpenLibraryClient openLibraryClient = retrofit.create(OpenLibraryClient.class);

        Call<List<Sala>> call = openLibraryClient.getSalas();

        call.enqueue(new Callback<List<Sala>>() {
            @Override
            public void onResponse(Call<List<Sala>> call, Response<List<Sala>> response) {
                if (!response.isSuccessful()) {

                    Log.d(TAG, "onResponse: Code: " + response.message());
                    return;
                }
                for (Sala sala : response.body()) {
                    String temp = sala.getId() + "," + sala.getRodzaj() + "," + sala.getWielkosc();
                    strings.add(temp);
                }
                if (!strings.isEmpty())
                    Log.d(TAG, "onResponse: Positive " + strings.get(0));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Sala>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        mShaker.resume();
        super.onResume();
//        showToast("onResume");
    }

    @Override
    protected void onPause() {
        mShaker.pause();
        super.onPause();
//        showToast("onPause");
    }

}
