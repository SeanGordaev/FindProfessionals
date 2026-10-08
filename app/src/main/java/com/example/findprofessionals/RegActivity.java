package com.example.findprofessionals;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.time.Year;
import java.util.Calendar;

public class RegActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editAddress, editPhone, editPassword;

    private Calendar calendar;
    private EditText editBirthday;
    private int year, month, day;

    private boolean isProfessional;

    private ImageButton imgProf;
    private ActivityResultLauncher<Intent> launcher;
    private Uri selectedImageUri;
    private String imgDecodableString;


    private RadioGroup radioGroupRole;

    private Button btnRegister;
    private ProgressDialog progressDialog;

    public void init() {
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editEmail);
        editEmail = findViewById(R.id.editEmail);
        editLastName = findViewById(R.id.editLastName);
        editFirstName = findViewById(R.id.editFirstName);
        editBirthday = findViewById(R.id.editBirthday);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnRegister = findViewById(R.id.btnRegister);


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        editBirthday.setText(String.format("%d/%d/%d", day, month, year));

        isProfessional = false;
        imgDecodableString = "img.png";


        // for user-image
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                selectedImageUri = result.getData().getData();
                imgProf.setImageURI(selectedImageUri);

                String[] filePathColumn={ MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                // String imgDecodableString contains the path of selected Image
                imgDecodableString = cursor.getString(columnIndex).substring(cursor.getString(columnIndex).lastIndexOf("/")+1);

                cursor.close();
            }
        });



        //Create ProgressDialog
        progressDialog = new ProgressDialog(RegActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();


//todo        btnBirth.setOnClickListener(new View.OnClickListener() {
//           public void OnClick(View view) {
//               DatePickerDialog datePickerDialog = new DatePickerDialog(RegActivity.this,
//                       (view1, year, month, dayOfMonth) ->
//                       {
//                           btnBirth.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
//                       }, year, month, day);
//               datePickerDialog.show();
//           }
//        });


        radioGroupRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull RadioGroup radioGroup, int i) {
                if (i == R.id.radioCustomer) {
                    isProfessional = false;
                }
                if (i == R.id.radioProfessional) {
                    isProfessional = true;
                }
            }
        });


        imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                launcher.launch(intent);
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();


                String firstName = editFirstName.getText().toString();
                String lastName = editLastName.getText().toString();
                String email = editEmail.getText().toString();
                String adress = editAddress.getText().toString();
                String phone = editPhone.getText().toString();
                String password = editPassword.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                    }
                });

            }
        });



        // ToDo: Add user img, change birth to dialog with selected data
    }
}