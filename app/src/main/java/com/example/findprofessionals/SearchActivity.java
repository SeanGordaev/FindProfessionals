package com.example.findprofessionals;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    // Navigation
    private BottomNavigationView btnNavigation;

    // Header and filters
    private TextView subTitle;
    private ChipGroup categoryCardGroup;
    private boolean isSearchForPro;

    // Results display
    private RecyclerView searchResults;
    private LinearLayout emptyState;
    private ProfessionalsAdapter professionalsAdapter;
    private RequestAdapter requestAdapter;

    // Professionals data
    private final ArrayList<User> fullList_Pro = new ArrayList<>();
    private final ArrayList<User> filtered_Pro = new ArrayList<>();

    // Requests data
    private final ArrayList<Request> fullList_Req = new ArrayList<>();
    private final ArrayList<Request> filtered_Req = new ArrayList<>();


    private void init() {
        StaticMethods.getPs().saveActivity("SearchActivity");

        // Navigation
        btnNavigation = findViewById(R.id.btnNavigation);

        // Header and filters
        subTitle = findViewById(R.id.subTitle);
        categoryCardGroup = findViewById(R.id.categoryCardGroup);

        isSearchForPro = StaticMethods.isSearchProfessionals();
        subTitle.setText(isSearchForPro ? "Find professionals" : "Find requests");

        // Results display
        emptyState = findViewById(R.id.emptyState);
        searchResults = findViewById(R.id.searchResults);

        professionalsAdapter = new ProfessionalsAdapter(new ArrayList<>());
        requestAdapter = new RequestAdapter(new ArrayList<>());

        searchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResults.setAdapter(isSearchForPro ? professionalsAdapter : requestAdapter);

    }
    private void initCategories() {

        categoryCardGroup.removeAllViews();

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
                    UpdateResult();
                }
            });

            categoryCardGroup.addView(chip);
        }
    }
    private void initListForFilter() {
        DatabaseReference ref = DbHelper.get_db_firebase(isSearchForPro ? "Users" : "Requests");

        if (isSearchForPro) {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    fullList_Pro.clear();

                    for (DataSnapshot child : snapshot.getChildren()) {

                        User user = child.getValue(User.class);

                        if (user != null && user.getStatus()) {
                            fullList_Pro.add(user);
                        }
                    }

                    UpdateResult();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    fullList_Req.clear();

                    for (DataSnapshot child : snapshot.getChildren()) {

                        Request request = child.getValue(Request.class);

                        if (request != null) {
                            fullList_Req.add(request);
                        }
                    }

                    UpdateResult();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }


    private void setupNavigationBar() {
        btnNavigation.setOnItemSelectedListener(item -> {
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
        startActivity(new Intent(SearchActivity.this, LoginActivity.class));
        finish();
    }


    private void UpdateEmptyState(int resultCount) {

        if (resultCount == 0) {
            emptyState.setVisibility(View.VISIBLE);
            searchResults.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            searchResults.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> getSelectedCategories() {

        ArrayList<String> selected = new ArrayList<>();

        for (int i = 0; i < categoryCardGroup.getChildCount(); i++) {

            Chip chip = (Chip)categoryCardGroup.getChildAt(i);

            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }

        return selected;
    }


    private void UpdateResult() {

        ArrayList<String> categories = getSelectedCategories();

        if (isSearchForPro) {
            filtered_Pro.clear();

            if (categories.isEmpty()) {
                professionalsAdapter.updateData(new ArrayList<>(fullList_Pro));
            } else {

                for (User user : fullList_Pro) {
                    if (user == null) continue;
                    if (containsSome(user.getTags(), categories))
                        filtered_Pro.add(user);
                }

                professionalsAdapter.updateData(new ArrayList<>(filtered_Pro));
            }

            UpdateEmptyState(categories.isEmpty() ? fullList_Pro.size() : filtered_Pro.size());

        } else {

            filtered_Req.clear();

            if (categories.isEmpty()) {
                requestAdapter.updateData(new ArrayList<>(fullList_Req));
            } else {

                for (Request request : fullList_Req) {
                    if (request == null) continue;
                    if (containsSome(request.getCategory(), categories))
                        filtered_Req.add(request);
                }

                requestAdapter.updateData(new ArrayList<>(filtered_Req));
            }

            UpdateEmptyState(categories.isEmpty() ? fullList_Req.size() : filtered_Req.size());
        }
    }


    public boolean containsSome(String text, ArrayList<String> subStrings) {
        if (text == null || subStrings == null) {
            return false;
        }

        for (String s : subStrings) {

            if (text.toLowerCase().contains(s.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        initCategories();
        setupNavigationBar();

        initListForFilter();

    }
}