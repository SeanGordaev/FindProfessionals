package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class SetImageActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private Button btnChooseImage, btnFinisReg;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;


    private ActivityResultLauncher<Intent> launcher;
    private Uri selectedImageUri;
    private String imgDecodableString;

    private ProgressDialog progressDialog;

    public void Init() {
        imgProfile = findViewById(R.id.imgProfile);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnFinisReg = findViewById(R.id.btnFinisReg);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        UpdateUserImage();

        //Create ProgressDialog
        progressDialog = new ProgressDialog(SetImageActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

    }

    public void UpdateUserImage() {
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                selectedImageUri = result.getData().getData();
                imgProfile.setImageURI(selectedImageUri);
            }
        });
    }

    private void UploadImage(String uid) {

        if (selectedImageUri == null) {
            return;
        }

        progressDialog.show();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_img").child(uid);
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType(getContentResolver().getType(selectedImageUri)).build(); // metadata, like format (png, jpg and other)

        storageRef.putFile(selectedImageUri, metadata).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                UpdateUserData(imageUrl);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(SetImageActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        });
    }

    private void UpdateUserData(String uri) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        StaticMethods.getUser().setImage(uri);

        ref.child(StaticMethods.getUser().getUID()).setValue(StaticMethods.getUser()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SetImageActivity.this, "Your data has been saved!!", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetImageActivity.this, "Something goes wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        progressDialog.dismiss();
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
                UpdateUserImage();
            }
        });

        btnFinisReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage(StaticMethods.getUser().getUID());
                startActivity(new Intent(SetImageActivity.this, NavActivity.class));
                finish();
            }
        });

    }
}