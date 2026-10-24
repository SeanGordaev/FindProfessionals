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

    private EditText editFirstName, editLastName, editEmail, editAddress, editPhone, editPassword;

    private EditText editBirthday;

    private boolean isProfessional;


    private RadioGroup radioGroupRole;

    private Button btnContinue;
    private ProgressDialog progressDialog;

    private Button btnBackToLogin;

    public void init() {
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editEmail = findViewById(R.id.editEmail);
        editLastName = findViewById(R.id.editLastName);
        editFirstName = findViewById(R.id.editFirstName);
        editBirthday = findViewById(R.id.editBirthday);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnContinue = findViewById(R.id.btnContinue);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        isProfessional = false;

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

        init(); // initial

        // Selected user's birthday
        editBirthday.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String date = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                month + 1,
                                year
                        );
                        editBirthday.setText(date);
                    },
                    today.get(Calendar.YEAR) - 18,
                    today.get(Calendar.MONTH + 1),
                    today.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMaxDate(
                    System.currentTimeMillis()
            );

            dialog.show();
        });

        // Selected if user is professional or client
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

        // Save the user's data
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String firstName = editFirstName.getText().toString();
                String lastName = editLastName.getText().toString();
                String email = editEmail.getText().toString();
                String address = editAddress.getText().toString();
                String phone = editPhone.getText().toString();
                String password = editPassword.getText().toString();
                String birthday = editBirthday.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            String uid = auth.getCurrentUser().getUid();
                            User user = new User(email, firstName, lastName, address, isProfessional, phone, birthday, uid);
                            StaticMethods.setUser(user);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegActivity.this, "Your data has been saved!!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegActivity.this,SetImageActivity.class));
                                        finish();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();

                                    Toast.makeText(RegActivity.this, "Something goes wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });



                        }
                    }
                });
            }
        });


        // Navigate to login
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegActivity.this, LoginActivity.class));

                finish();
            }
        });

    }
}