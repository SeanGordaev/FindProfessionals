package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    private void init() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }

    private void EndWithTheLogin(User user) {

        progressDialog.dismiss();

        StaticMethods.setUser(user);
        StaticMethods.getPs().saveUser(user);

        startActivity(new Intent(LoginActivity.this, NavActivity.class));
        finish();
    }

    private void SaveUserData(String uid) {

        DbHelper.get_db_firebase("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists())
                    ShowProfileError("User not found");
                else {
                    User user = snapshot.getValue(User.class);

                    if (user == null)
                        ShowProfileError("Failed to load client profile");
                    else
                        EndWithTheLogin(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ShowProfileError("Database error: " + error.getMessage());
            }
        });
    }

    private void SignIn() {


        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                FirebaseUser user = authResult.getUser(); // find the user with the same email and password

                if (user == null)
                    ShowProfileError("User doesn't exist");
                else {
                    String uid = user.getUid();
                    SaveUserData(uid);
                }

            }).addOnFailureListener(e -> { ShowProfileError("Login failed: " + e.getMessage()); }
        );
    }

    private void ShowProfileError(String message) {
        progressDialog.dismiss();
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        StaticMethods.setUser(null);
        StaticMethods.getPs().clearUser();
        FirebaseAuth.getInstance().signOut();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegActivity.class));
                finish();
            }
        });
    }
}