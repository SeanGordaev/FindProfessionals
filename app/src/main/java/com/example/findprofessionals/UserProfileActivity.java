package com.example.findprofessionals;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserProfileActivity extends AppCompatActivity {

    // Data
    private User currentUser;

    // Navigation
    private BottomNavigationView btnNavigation;

    // Header
    private ImageView profileImage;
    private TextView profileName, profileAccountType;

    // Rating
    private ImageView[] profileRatingStars;

    // Personal information
    private TextView profileEmail, profilePhone, profileAddress, profileBirthday, profileGender;

    // Professional information
    private LinearLayout professionalSection;
    private TextView profileDescription, profileTags, profileStartDate;

    // Actions
    private MaterialButton editProfileButton;


    private void Init() {
        StaticMethods.getPs().saveActivity("UserProfileActivity");
        StaticMethods.getPs().saveLastActivity("NavActivity");


        // Data
        currentUser = StaticMethods.getUser();

        // Navigation
        btnNavigation = findViewById(R.id.btnNavigation);

        // Header
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileAccountType = findViewById(R.id.profileAccountType);

        // Personal information
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        profileAddress = findViewById(R.id.profileAddress);
        profileBirthday = findViewById(R.id.profileBirthday);
        profileGender = findViewById(R.id.profileGender);

        // Professional information
        professionalSection = findViewById(R.id.professionalSection);

        profileDescription = findViewById(R.id.profileDescription);
        profileTags = findViewById(R.id.profileTags);
        profileStartDate = findViewById(R.id.profileStartDate);


        profileRatingStars = new ImageView[] {
                findViewById(R.id.star1),
                findViewById(R.id.star2),
                findViewById(R.id.star3),
                findViewById(R.id.star4),
                findViewById(R.id.star5)
        };


        editProfileButton = findViewById(R.id.editProfileButton);


    }
    private void setupNavigationBar() {
        btnNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                logout();
                return true;
            }
            if (itemId == R.id.navigation_come_back) {
                String lastActivity = StaticMethods.getPs().getLastActivity();
                Intent intent = StaticMethods.getIntel(this, lastActivity);
                goToPage(intent);
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
        startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
        finish();
    }

    private void setField() {
        // Header
        profileName.setText("Welcome, " + currentUser.getName() + " " + currentUser.getLastName() + "!");
        profileAccountType.setText(currentUser.getStatus() ? "Professional" : "Client");

        // Personal Information
        profileEmail.setText(currentUser.getEmail());
        profilePhone.setText(currentUser.getPhone());
        profileAddress.setText(currentUser.getAddress());
        profileBirthday.setText(currentUser.getBirthday());
        profileGender.setText(currentUser.getGender() ? "Male" : "Female");

        // Professional Information
        profileTags.setText(currentUser.getTags());
        profileDescription.setText(currentUser.getDescription());
        profileStartDate.setText(currentUser.getDataStartOfJob());


        if (currentUser.getStatus()) {
            professionalSection.setVisibility(View.VISIBLE);
        }
    }


    private void setUserImg() {
        String imageId = currentUser.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_img").child(imageId);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(UserProfileActivity.this).load(uri).into(profileImage);
            }
        });
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
                profileRatingStars[i].setImageResource(R.drawable.ic_star_full);
            }
            else if (starValue >= 0.5f) {
                profileRatingStars[i].setImageResource(R.drawable.ic_star_half);
            }
            else {
                profileRatingStars[i].setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();
        setupNavigationBar();


        setField();
        setUserImg();
        setRating();


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticMethods.getPs().saveLastActivity("UserProfileActivity");
                goToPage(new Intent(UserProfileActivity.this, UpdateUserInfoActivity.class));
            }
        });
    }
}