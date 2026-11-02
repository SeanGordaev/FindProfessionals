package com.example.findprofessionals;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NavActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profile_imageNav;
    private TextView userNameNav;
    private ImageView[] ratingStars;
    private CardView crd1, crd2, crd3, crd4;
    private BottomNavigationView bottom_navigation;
    private User currentUser;


    public  void Init() {
        profile_imageNav = findViewById(R.id.profile_imageNav);
        userNameNav = findViewById(R.id.userNameNav);
        bottom_navigation = findViewById(R.id.bottom_navigation);

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

        currentUser = StaticMethods.getUser();

        if (!currentUser.getStatus()) {
            crd2.setVisibility(View.GONE);
        }

        setRating();
    }
    private void setRating() {

        float score;
        int userScoreTimes = currentUser.getScoreTimes();
        if (userScoreTimes == 0) {
            score = 0.0f;
        } else {
            score = ((float)currentUser.getScore()) / userScoreTimes;
        }

        float rounded = Math.round(score * 2.0) / 2.0f;
        score = (float) Math.max(0.0, Math.min(5.0, rounded));

        for (int i = 0; i < 5; i++) {

            float starValue = score - i;

            if (starValue >= 1f) {
                ratingStars[i].setImageResource(R.drawable.ic_star_full);
            }
            else if (starValue >= 0.5f) {
                ratingStars[i].setImageResource(R.drawable.ic_star_half);
            }
            else {
                ratingStars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }
    public void GetUserDetails() {
        String imageId = currentUser.getUID();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_img").child(imageId);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(NavActivity.this).load(uri).into(profile_imageNav);
            }
        });

    }


    private void InitBar() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                startActivity(new Intent(NavActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_come_back) {
                Intent intent = getIntent();
                if (intent != null && intent.hasExtra("BACK_TO_CLASS")) {
                    String className = intent.getStringExtra("BACK_TO_CLASS");
                    try {
                        Class<?> backClass = Class.forName(className);
                        Intent backIntent = new Intent(NavActivity.this, backClass);
                        intent.putExtra("BACK_TO_CLASS", NavActivity.class.getName());
                        startActivity(backIntent);
                    } catch (ClassNotFoundException e) {
                    }
                }
                finish();
                return true;
            }
            return false;
        });
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
        GetUserDetails();
        userNameNav.setText("Welcome, " + currentUser.getName() + " " + currentUser.getLastName() + "!");

        bottom_navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                startActivity(new Intent(NavActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onClick(View view) {
        if (view == crd1) {
            Intent intent = new Intent(NavActivity.this, UserProfileActivity.class);
            intent.putExtra("BACK_TO_CLASS", NavActivity.class.getName());
            startActivity(intent);
            finish();
        } else if (view == crd2) {
            StaticMethods.setSearchProfessionals(false);

        } else if (view == crd3) {
            StaticMethods.setSearchProfessionals(true);

        } else if (view == crd4) {
            Intent intent = new Intent(NavActivity.this, RequestActivity.class);
            intent.putExtra("BACK_TO_CLASS", NavActivity.class.getName());
            startActivity(intent);
            finish();
        }
    }
}