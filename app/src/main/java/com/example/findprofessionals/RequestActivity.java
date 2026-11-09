package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {


    private BottomNavigationView bottom_navigation;
    private EditText requestTitle, requestDescription, requestLocation;
    private MaterialAutoCompleteTextView requestCategory;
    private MaterialButton btnPublishRequest;

    private final ArrayList<String> selectedTags = new ArrayList<>();

    private ProgressDialog progressDialog;


    private void Init() {
        StaticMethods.getPs().saveActivity("RequestActivity");

        bottom_navigation = findViewById(R.id.bottom_navigation);
        requestTitle = findViewById(R.id.requestTitle);
        requestCategory = findViewById(R.id.requestCategory);
        requestDescription = findViewById(R.id.requestDescription);
        requestLocation = findViewById(R.id.requestLocation);
        btnPublishRequest = findViewById(R.id.btnPublishRequest);

        // Set Tags Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                StaticMethods.getTags()
        );

        requestCategory.setAdapter(adapter);

        // Show dropdown
        requestCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                requestCategory.post(() -> requestCategory.showDropDown());
            }
        });

        // Handle selected tag
        requestCategory.setOnItemClickListener((parent, view, position, id) -> {

            String selectedTag = adapter.getItem(position);

            if (selectedTag != null && !selectedTags.contains(selectedTag)) {
                selectedTags.add(selectedTag);
            }

            // Display all selected tags
            requestCategory.setText(String.join(", ", selectedTags), false);

            // Clear filtering text is not needed; keep field ready for next selection
        });


        //Create ProgressDialog
        progressDialog = new ProgressDialog(RequestActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
    }
    private void initBtnPublish() {
        btnPublishRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uniqueID = FirebaseDatabase.getInstance().getReference().push().getKey() + ":" + StaticMethods.getUser().getUid(); // UniqueID:UserID
                Request request = CreateRequest(uniqueID); // Create request

                if (request == null)
                    Toast.makeText(RequestActivity.this, "Fill All Field!", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.show();

                    DbHelper.get_db_firebase("Requests").child(uniqueID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(RequestActivity.this, "Your request was saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();

                            Toast.makeText(RequestActivity.this, "Something goes wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }


    private void setupNavigationBar() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                logout();
                return true;
            }
            if (itemId == R.id.navigation_come_back) {
                String lastActivity = StaticMethods.getPs().getLastActivity();
                Intent intent = StaticMethods.getIntel(this, lastActivity);
                goToPage(intent);
                return true;
            }
            return false;
        });
    }
    private void goToPage(Intent intent) {
        if (intent != null) {
            startActivity(intent);
            finish();
        } else {
            logout();
        }
    }
    private void logout() {
        startActivity(new Intent(RequestActivity.this, LoginActivity.class));
        finish();
    }



    private Request CreateRequest(String RID) {
        if (isFormComplete()) {
            return new Request(
                    RID,
                    StaticMethods.getUser().getName() + " " + StaticMethods.getUser().getLastName(),
                    StaticMethods.getUser().getPhone(),
                    requestTitle.getText().toString(),
                    requestCategory.getText().toString(),
                    requestDescription.getText().toString(),
                    requestLocation.getText().toString()
            );
        }
        return null;
    }

    private boolean isFormComplete() {
        return !isEmpty(requestTitle) || !isEmpty(requestDescription) || !isEmpty(requestLocation) || !selectedTags.isEmpty();
    }
    private boolean isEmpty(EditText field) {
        return field.getText().toString().trim().isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();
        setupNavigationBar();

        initBtnPublish();
    }
}