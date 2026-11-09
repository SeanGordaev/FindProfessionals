package com.example.findprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

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
        }, 2000);
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

        GoAnimation();
    }
}