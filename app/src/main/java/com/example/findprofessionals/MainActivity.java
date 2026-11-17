package com.example.findprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private AnimationClass myAnimation;
    private FrameLayout container;

    @Override
    protected void onPause() {
        super.onPause();
        myAnimation.Cancel_Animation(); // stop animation
    }

    @Override
    protected void onResume() {
        super.onResume();

        myAnimation.Start_Animation(); // start animation
        new Thread(this.myAnimation).start(); // create new thread for animation
    }



    private void GoToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
    private void GoToNextPage() {
        StaticMethods.InitPageState(this);

        String lastActivity = StaticMethods.getPs().getActivity();
        if (lastActivity == null) {
            GoToLogin();
            return;
        }

        Intent intent = StaticMethods.getIntel(this, lastActivity);

        if (intent != null) {
            StaticMethods.setUser(StaticMethods.getPs().getUser());
            startActivity(intent);
            finish();
        } else {
            GoToLogin();
        }
    }
    public void GoAnimation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GoToNextPage();
            }
        }, 9000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        container = findViewById(R.id.main);

        myAnimation = new AnimationClass(this); // create new instance of animation class
        container.addView(myAnimation); // add animation class to FrameLayout

        GoAnimation();

    }
}