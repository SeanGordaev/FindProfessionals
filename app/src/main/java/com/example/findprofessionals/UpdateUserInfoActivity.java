package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UpdateUserInfoActivity extends AppCompatActivity {


    // Personal information
    private EditText editName;
    private EditText editLastName;
    private EditText editPhone;
    private EditText editAddress;

    // Gender
    private RadioGroup editGenderGroup;

    // Action buttons
    private MaterialButton saveProfileButton;
    private MaterialButton cancelEditButton;


    private boolean isMale;

    private ProgressDialog progressDialog;

    private void Init() {
        StaticMethods.getPs().SaveActivity("UpdateUserInfoActivity");

        // Personal information
        editName = findViewById(R.id.editName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);

        // Gender
        editGenderGroup = findViewById(R.id.editGenderGroup);

        // Action buttons
        saveProfileButton = findViewById(R.id.saveProfileButton);
        cancelEditButton = findViewById(R.id.cancelEditButton);


        isMale = StaticMethods.getUser().getGender();

        //Create ProgressDialog
        progressDialog = new ProgressDialog(UpdateUserInfoActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    private String poor(EditText value, String defaultValue) {
        String newValue = value.getText().toString();
        return newValue.isEmpty() ? defaultValue : newValue;
    }

    private User updateUser() {
        User user = StaticMethods.getUser();
        user.setName(poor(editName, user.getName()));
        user.setLastName(poor(editLastName, user.getLastName()));
        user.setPhone(poor(editPhone, user.getPhone()));
        user.setAddress(poor(editAddress, user.getAddress()));

        user.setGender(isMale);

        return user;
    }

    private void UpdateData() {
        progressDialog.show();

        User user = updateUser();

        DbHelper.get_db_firebase("Users").child(user.getUID()).setValue(user)
                .addOnSuccessListener(v -> {
                    DismissAndToast("Data was saved");
                    startActivity(new Intent(UpdateUserInfoActivity.this, UserProfileActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> DismissAndToast("Something goes wrong: " + e.getMessage()));


    }

    private void DismissAndToast(String msg) {
        progressDialog.dismiss();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_user_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Init();

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateData();
            }
        });

        cancelEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateUserInfoActivity.this, UserProfileActivity.class));
                finish();
            }
        });

        editGenderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup radioGroup, int i) {
                if (i == R.id.editMale) {
                    isMale = true;
                }
                if (i == R.id.editFemale) {
                    isMale = false;
                }
            }
        });
    }
}