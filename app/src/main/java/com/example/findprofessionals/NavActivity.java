package com.example.findprofessionals;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageReference;

public class NavActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profile_imageNav;
    private TextView userNameNav;
    private ImageView[] ratingStars;
    private CardView crd1, crd2, crd3, crd4;
    private BottomNavigationView bottom_navigation;
    private final User USER = StaticMethods.getUser();

    public  void Init() {
        StaticMethods.getPs().saveActivity("NavActivity");
        StaticMethods.getPs().saveLastActivity("NavActivity");

        profile_imageNav = findViewById(R.id.profile_imageNav);
        userNameNav = findViewById(R.id.userNameNav);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        userNameNav.setText("Welcome, " + USER.getName() + " " + USER.getLastName() + "!"); // line 39

        ratingStars = new ImageView[] {
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };


        crd1 = findViewById(R.id.crd1);
        crd2 = findViewById(R.id.crd2);
        crd3 = findViewById(R.id.crd3);
        crd4 = findViewById(R.id.crd4);

        crd1.setOnClickListener(this);
        crd2.setOnClickListener(this);
        crd3.setOnClickListener(this);
        crd4.setOnClickListener(this);


        if (!USER.getStatus()) {
            crd2.setVisibility(View.GONE);
        }


    }
    private void setupNavigationBar() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                logout();
                return true;
            }
            return false;
        });
    }
    private void goToPage(Intent intent) {
        if (intent != null) {
            startActivity(intent);
            finish();
        } else {
            logout();
        }
    }
    private void logout() {
        startActivity(new Intent(NavActivity.this, LoginActivity.class));
        finish();
    }



    private float calculateAverageScore() {
        int times = USER.getScoreTimes();
        if (times == 0) return 0f;
        return (float) USER.getScore() / times;
    }
    private float roundToHalf(float value) {
        float clamped = Math.max(0f, Math.min(5f, value));
        return Math.round(clamped * 2f) / 2f;
    }

    private void updateRatingStars() {
        float rating = roundToHalf(calculateAverageScore());

        for (int i = 0; i < ratingStars.length; i++) {
            float starValue = rating - i;

            int drawable;
            if (starValue >= 1f) {
                drawable = R.drawable.ic_star_full;
            } else if (starValue >= 0.5f) {
                drawable = R.drawable.ic_star_half;
            } else {
                drawable = R.drawable.ic_star_empty;
            }
            ratingStars[i].setImageResource(drawable);
        }
    }
    public void GetUserDetails() {

        if (USER == null) {
            return;
        }

        String imageId = USER.getUid();


        if (imageId == null || imageId.trim().isEmpty()) {
            return;
        }

        StorageReference storageReference =
                DbHelper.get_storage_reference("profile_img").child(imageId);

        storageReference.getDownloadUrl()
                .addOnSuccessListener(uri ->
                        Glide.with(NavActivity.this)
                                .load(uri)
                                .into(profile_imageNav)
                );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();
        setupNavigationBar();
        GetUserDetails();
        updateRatingStars();
    }

    @Override
    public void onClick(View view) {
        if (view == crd1) {
            Intent intent = new Intent(this, UserProfileActivity.class);
            goToPage(intent);
        } else if (view == crd3 || view == crd2) {
            StaticMethods.setSearchProfessionals(view == crd3);
            Intent intent = new Intent(this, SearchActivity.class);
            goToPage(intent);
        } else if (view == crd4) {
            Intent intent = new Intent(this, RequestActivity.class);
            goToPage(intent);
        }
    }
}