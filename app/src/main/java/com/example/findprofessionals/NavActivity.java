package com.example.findprofessionals;

import android.content.Intent;
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
    private TextView titleNav;
    private CardView crd1, crd2, crd3, crd4, crd5, crd6;
    private BottomNavigationView bottom_navigation;

    public  void Init() {
        profile_imageNav = findViewById(R.id.profile_imageNav);
        titleNav = findViewById(R.id.titleNav);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        crd1 = findViewById(R.id.crd1);
        crd2 = findViewById(R.id.crd2);
        crd3 = findViewById(R.id.crd3);
        crd4 = findViewById(R.id.crd4);
        crd5 = findViewById(R.id.crd5);
        crd6 = findViewById(R.id.crd6);

        crd1.setOnClickListener(this);
        crd2.setOnClickListener(this);
        crd3.setOnClickListener(this);
        crd4.setOnClickListener(this);
        crd5.setOnClickListener(this);
        crd6.setOnClickListener(this);
    }

    public void GetUserDetails() {
        titleNav.setText("Welcome " + StaticMethods.getUser().getName() + " " + StaticMethods.getUser().getLastName() + "!");

        String imageId = StaticMethods.getUser().getUID();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_img").child(imageId);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(NavActivity.this).load(uri).into(profile_imageNav);
            }
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

    }
}