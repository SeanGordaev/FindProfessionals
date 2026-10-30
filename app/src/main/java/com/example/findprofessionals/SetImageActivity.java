package com.example.findprofessionals;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

public class SetImageActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private Button btnChooseImage, btnFinisReg;
    private RadioGroup radioGroupGender;

    private boolean isMale;

    private ActivityResultLauncher<Intent> launcher;
    private Uri selectedImageUri;

    private ProgressDialog progressDialog;

    private LinearLayout professionalFields;
    private EditText editDescription;
    private EditText editStartDate;

    private void initEditStartDate() {
        if (StaticMethods.getUser().getStatus()) {
            professionalFields.setVisibility(View.VISIBLE);
        }

        editStartDate.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    SetImageActivity.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        editStartDate.setText(String.format(Locale.getDefault(),
                                "%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    today.get(Calendar.YEAR) - 18,
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });
    }

    public void Init() {
        imgProfile = findViewById(R.id.imgProfile);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnFinisReg = findViewById(R.id.btnFinisReg);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        professionalFields = findViewById(R.id.professionalFields);
        editDescription = findViewById(R.id.editDescription);
        editStartDate = findViewById(R.id.editStartDate);

        isMale = true;

        // Professional place
        if (StaticMethods.getUser().getStatus()) {
            professionalFields.setVisibility(View.VISIBLE);
        }

        initEditStartDate();

        // Update User Image
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                selectedImageUri = result.getData().getData();
                imgProfile.setImageURI(selectedImageUri);
            }
        });

        //Create ProgressDialog
        progressDialog = new ProgressDialog(SetImageActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

    }
    private void UploadImage(String uid) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_img").child(uid);
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType(getContentResolver().getType(selectedImageUri)).build();

        storageRef.putFile(selectedImageUri, metadata)
                .addOnSuccessListener(snap -> UpdateUserData())
                .addOnFailureListener(e -> fail("Image upload failed: " + e.getMessage()));
    }
    private void UpdateUserData() {
        User user = StaticMethods.getUser();
        user.setGender(isMale);
        user.setDescription(editDescription.getText().toString());
        user.setDataStartOfJob(editStartDate.getText().toString());

        DbHelper.get().getReference("Users").child(user.getUID()).setValue(user)
                .addOnSuccessListener(v -> {
                    progressDialog.dismiss();
                    startActivity(new Intent(this, NavActivity.class));
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
        setContentView(R.layout.activity_set_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();


        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup radioGroup, int i) {
                if (i == R.id.radioMale) {
                    isMale = true;
                }
                if (i == R.id.radioFemale) {
                    isMale = false;
                }
            }
        });

        btnFinisReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                UploadImage(StaticMethods.getUser().getUID());
            }
        });

    }
}