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
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Year;
import java.util.Calendar;
import java.util.Locale;

public class RegActivity extends AppCompatActivity {

    // Info field
    private EditText editFirstName, editLastName, editEmail, editAddress, editPhone, editPassword, editBirthday;
    private RadioGroup radioGroupRole;
    private boolean isProfessional;

    // Navigation
    private Button btnContinue, btnBackToLogin;

    // Visual
    private ProgressDialog progressDialog;

    public void Init() {
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editLastName = findViewById(R.id.editLastName);
        editFirstName = findViewById(R.id.editFirstName);
        editBirthday = findViewById(R.id.editBirthday);
        radioGroupRole = findViewById(R.id.radioGroupRole);

        isProfessional = false;

        btnContinue = findViewById(R.id.btnContinue);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        //Create ProgressDialog
        progressDialog = new ProgressDialog(RegActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }
    private void InitButtonBackToLogin() {
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private void initBirthdayField() {
        // Selected user's birthday
        editBirthday.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    RegActivity.this,
                    android.R.style.Theme_DeviceDefault_Light_Dialog,
                    (view, year, month, dayOfMonth) -> {
                        editBirthday.setText(String.format(Locale.getDefault(),
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
    private void InitRadioGroup() {
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
    }
    private void initContinueButton() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFormComplete()) {
                    stopAndMessage("Please fill in all fields and select a role");
                } else {
                    registerUser();
                }
            }
        });
    }

    private User createTheUser(String email, String UID) {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String address = editAddress.getText().toString();
        String phone = editPhone.getText().toString();
        String birthday = editBirthday.getText().toString();

        User user = new User(email, firstName, lastName, address, isProfessional, phone, birthday, UID);
        StaticMethods.setUser(user);

        return user;
    }
    private void registerUser() {
        progressDialog.show();

        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    // Create new user
                    String uid = auth.getCurrentUser().getUid();
                    User user = createTheUser(email, uid);

                    DbHelper.get_db_firebase("Users").child(uid).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                // Put the new user in firebase
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegActivity.this, "Your data has been saved!!", Toast.LENGTH_SHORT).show();

                                        StaticMethods.getPs().saveUser(user);

                                        startActivity(new Intent(RegActivity.this,SetImageActivity.class));
                                        finish();
                                    }

                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    stopAndMessage("Something goes wrong: " + e.getMessage());
                                }
                            });

                } else {
                    stopAndMessage("This email is already registered");
                }
            }
        });
    }

    private void stopAndMessage(String message) {
        progressDialog.dismiss();
        Toast.makeText(RegActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isFormComplete() {
        return !isEmpty(editFirstName)
                && !isEmpty(editLastName)
                && !isEmpty(editEmail)
                && !isEmpty(editAddress)
                && !isEmpty(editPhone)
                && !isEmpty(editPassword)
                && !isEmpty(editBirthday)
                && radioGroupRole.getCheckedRadioButtonId() != -1;
    }
    private boolean isEmpty(EditText field) {
        return field.getText().toString().trim().isEmpty();
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

        Init(); // initial
        initBirthdayField(); // now user choice his birthday
        InitRadioGroup(); // set isProfessional=true if user is professional, if client then isProfessional=false
        InitButtonBackToLogin(); // now user can come back to log in
        initContinueButton(); // Save user's data and move the user to next step of creating account


    }
}