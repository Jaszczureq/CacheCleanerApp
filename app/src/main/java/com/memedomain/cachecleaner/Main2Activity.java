package com.memedomain.cachecleaner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Main2Activity extends AppCompatActivity {
    ConstraintLayout constraintLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        constraintLayout=(ConstraintLayout)findViewById(R.id.constraintLayout);

        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
//            @Override
//            public void onSwipeLeft() {
//                super.onSwipeLeft();
////                showToast("Swipe Left detected");
////                System.out.println("SMTH");
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//////                        intent.putExtra("id", "1");
////                        startActivity(intent);
////                        MainActivity.this.finish();
////                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
////                    }
////                }, SPLASH_DISPLAY_TIME);
//                Intent intent=new Intent(Main2Activity.this, MainActivity.class);
//                startActivity(intent);
//                Main2Activity.this.finish();
//            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent intent=new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                Main2Activity.this.finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
