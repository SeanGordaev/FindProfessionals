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
    private boolean radioChoiced;

    private ProgressDialog progressDialog;

    private void Init() {

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


        isMale = false;
        radioChoiced = false;


        //Create ProgressDialog
        progressDialog = new ProgressDialog(UpdateUserInfoActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    private boolean AllIsFull() {
        return !radioChoiced &&
                !editName.getText().toString().isEmpty() &&
                !editLastName.getText().toString().isEmpty() &&
                !editPhone.getText().toString().isEmpty() &&
                editAddress.getText().toString().isEmpty();
    }

    private void UpdateData() {
        progressDialog.show();

        User user = StaticMethods.getUser();
        user.setName(editName.getText().toString());
        user.setLastName(editLastName.getText().toString());
        user.setPhone(editPhone.getText().toString());
        user.setAddress(editAddress.getText().toString());

        user.setGender(isMale);

        DbHelper.get().getReference("Users").child(user.getUID()).setValue(user)
                .addOnSuccessListener(v -> {
                    progressDialog.dismiss();
                    startActivity(new Intent(UpdateUserInfoActivity.this, UserProfileActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> fail("Something goes wrong: " + e.getMessage()));

    }

    private void fail(String msg) {
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
                radioChoiced = true;
            }
        });
    }
}