package com.example.findprofessionals;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUserInfoActivity extends AppCompatActivity {

    // User image
    private CircleImageView imgProfile;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> launcher;

    // Personal information
    private EditText editName;
    private EditText editLastName;
    private EditText editPhone;
    private EditText editAddress;
    private EditText editBirthday;
    private RadioGroup editGenderGroup;
    private boolean isMale;

    // Professional Information
    private LinearLayout professionalSection;
    private EditText editDescription, editStartOfJob;
    private MaterialAutoCompleteTextView editTags;
    private final ArrayList<String> selectedTags = new ArrayList<>();


    // Action buttons
    private MaterialButton saveProfileButton;
    private MaterialButton cancelEditButton;

    // Dialog
    private ProgressDialog progressDialog;


    private void init() {
        StaticMethods.getPs().saveActivity("UpdateUserInfoActivity");

        imgProfile = findViewById(R.id.imgProfile);

        // Personal information
        editName = findViewById(R.id.editName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editGenderGroup = findViewById(R.id.editGenderGroup);
        editBirthday = findViewById(R.id.editBirthday);

        isMale = StaticMethods.getUser().getGender(); // by default is the same gender that user choices on reg

        // Professional Information
        professionalSection = findViewById(R.id.professionalSection);
        editDescription = findViewById(R.id.editDescription);
        editTags = findViewById(R.id.editTags);
        editStartOfJob = findViewById(R.id.editStartOfJob);

        professionalSection.setVisibility(StaticMethods.getUser().getStatus() ? View.VISIBLE : View.GONE);


        // Action buttons
        saveProfileButton = findViewById(R.id.saveProfileButton);
        cancelEditButton = findViewById(R.id.cancelEditButton);


        //Create ProgressDialog
        progressDialog = new ProgressDialog(UpdateUserInfoActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        selectedImageUri = null;
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });
    }
    private void initDataButtons(EditText dataButton) {
        dataButton.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    UpdateUserInfoActivity.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        dataButton.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    },
                    today.get(Calendar.YEAR) - 18,
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.show();
        });
    }
    private void initTagsField() {
        // Set Tags Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, StaticMethods.getTags());
        editTags.setAdapter(adapter);

        // Show dropdown
        editTags.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editTags.post(() -> editTags.showDropDown());
            }
        });

        // Handle selected tag
        editTags.setOnItemClickListener((parent, view, position, id) -> {

            String selectedTag = adapter.getItem(position);

            if (selectedTag != null && !selectedTags.contains(selectedTag)) {
                selectedTags.add(selectedTag);
            }

            // Display all selected tags
            editTags.setText(String.join(", ", selectedTags), false);

        });
    }
    private void initLauncher() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                selectedImageUri = result.getData().getData();
                imgProfile.setImageURI(selectedImageUri);
            }
        });
    }

    private String getValue(EditText value, String defaultValue) {
        String newValue = value.getText().toString();
        return newValue.trim().isEmpty() ? defaultValue : newValue;
    }

    private User updateUser() {
        User user = StaticMethods.getUser();

        // Update person information
        user.setName(getValue(editName, user.getName()));
        user.setLastName(getValue(editLastName, user.getLastName()));
        user.setPhone(getValue(editPhone, user.getPhone()));
        user.setAddress(getValue(editAddress, user.getAddress()));
        user.setBirthday(getValue(editBirthday, user.getAddress()));
        user.setGender(isMale);

        // Update professional information
        if (user.getStatus()) {
            user.setDescription(getValue(editDescription, user.getDescription()));
            user.setDescription(getValue(editTags, user.getTags()));
            user.setDescription(getValue(editStartOfJob, user.getDataStartOfJob()));
        }

        if (selectedImageUri != null) {
            StorageReference storageRef = DbHelper.get_storage_reference("profile_img").child(user.getUid());
            StorageMetadata metadata = new StorageMetadata.Builder().setContentType(getContentResolver().getType(selectedImageUri)).build();

            storageRef.putFile(selectedImageUri, metadata);
        }

        return user;
    }

    private void UpdateData() {
        progressDialog.show();

        User user = updateUser();

        DbHelper.get_db_firebase("Users").child(user.getUid()).setValue(user)
                .addOnSuccessListener(v -> {
                    DismissAndToast("Data was saved");

                    StaticMethods.getPs().saveUser(user);

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


        init();
        initDataButtons(editBirthday);
        initDataButtons(editStartOfJob);
        initTagsField();
        initLauncher();

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