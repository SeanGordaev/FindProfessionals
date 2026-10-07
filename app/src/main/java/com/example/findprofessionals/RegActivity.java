package com.example.findprofessionals;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.Year;
import java.util.Calendar;

public class RegActivity extends AppCompatActivity {

    private Calendar calendar;
    private EditText editBirthday;
    private int year, month, day;

    private boolean isProfessional;

//    private ActivityResultLauncher<Intent> launcher;
//    private Uri selectImageUri;
//    private String pickStr;

    private ProgressDialog progressDialog;

    public void init() {
        editBirthday = findViewById(R.id.editBirthday);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        editBirthday.setText(String.format("%d/%d/%d", day, month, year));

        isProfessional = false;
//        pickStr = "profile.jpg";

        String[] arr_whouare = new String[]{"Professional", "Client"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegActivity.this, android.R.layout.simple_spinner_item, arr_whouare);

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
    }
}