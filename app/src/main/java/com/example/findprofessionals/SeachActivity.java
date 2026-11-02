package com.example.findprofessionals;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class SeachActivity extends AppCompatActivity {

    private ChipGroup professionalCategoryGroup;
    private ArrayList<String> selected = new ArrayList<>();
    private TextView subTitle;

    private void Init() {
        professionalCategoryGroup = findViewById(R.id.professionalCategoryGroup);
        subTitle = findViewById(R.id.subTitle);
    }

    private void InitCategories() {

        professionalCategoryGroup.removeAllViews();

        String[] categories = StaticMethods.getTags();

        for (String category : categories) {

            Chip chip = new Chip(this);

            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chip.isChecked()) {
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#536DFE")));
                        chip.setTextColor(Color.WHITE);
                    } else {
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F0F1F7")));
                        chip.setTextColor(Color.parseColor("#211B3A"));
                    }
                }
            });

            professionalCategoryGroup.addView(chip);
        }
    }

    private ArrayList<String> GetCategoryGroup() {

        ArrayList<String> selected = new ArrayList<>();

        for (int i = 0; i < professionalCategoryGroup.getChildCount(); i++) {

            Chip chip = (Chip) professionalCategoryGroup.getChildAt(i);

            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }

        return selected;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seach);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Init();
        InitCategories();
    }
}