package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {


    private BottomNavigationView bottom_navigation;
    private TextInputEditText requestTitle, requestDescription, requestLocation;
    private MaterialAutoCompleteTextView requestCategory;
    private MaterialButton btnPublishRequest;
    private ProgressDialog progressDialog;

    private final ArrayList<String> selectedTags = new ArrayList<>();



    private void Init() {
        StaticMethods.getPs().SaveActivity("RequestActivity");

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

    private void InitBar() {
        bottom_navigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                startActivity(new Intent(RequestActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_come_back) {
                Intent intent = getIntent();
                if (intent != null && intent.hasExtra("BACK_TO_CLASS")) {
                    String className = intent.getStringExtra("BACK_TO_CLASS");
                    try {
                        Class<?> backClass = Class.forName(className);
                        Intent backIntent = new Intent(RequestActivity.this, backClass);
                        backIntent.putExtra("BACK_TO_CLASS", RequestActivity.class.getName());
                        startActivity(backIntent);
                    } catch (ClassNotFoundException e) {
                    }
                }
                finish();
                return true;
            }
            return false;
        });
    }

    private boolean allFieldFull() {
        return !requestTitle.getText().toString().isEmpty() &&
                !requestCategory.getText().toString().isEmpty() &&
                !requestDescription.getText().toString().isEmpty() &&
                !requestLocation.getText().toString().isEmpty();
    }
    private Request CreateRequest(String RID) {
        if (allFieldFull()) {
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
        InitBar();

        btnPublishRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uniqueID = FirebaseDatabase.getInstance().getReference().push().getKey() + ":" + StaticMethods.getUser().getUID();
                Request request = CreateRequest(uniqueID);

                if (request == null) {
                    Toast.makeText(RequestActivity.this, "Fill All Field!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();

                    DatabaseReference ref = DbHelper.get().getReference("Requests");
                    ref.child(uniqueID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(RequestActivity.this, "Your data has been saved!!", Toast.LENGTH_SHORT).show();
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
}