package com.example.findprofessionals;

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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeachActivity extends AppCompatActivity {

    private BottomNavigationView btnNavigation;
    private ChipGroup categoryCardGroup;

    private TextView subTitle;

    private boolean isSearchForPro;

    private LinearLayout emptyState;
    private RecyclerView searchResults;

    private ProfessionalsAdapter professionalsAdapter;
    private RequestAdapter postsAdapter;


    private ArrayList<User> filltred_Pro = new ArrayList();
    private ArrayList<Request> filltred_Req = new ArrayList();

    private ArrayList<User> fullList_Pro = new ArrayList<>();
    private ArrayList<Request> fullList_Req = new ArrayList<>();


    private void Init() {
        categoryCardGroup = findViewById(R.id.categoryCardGroup);
        subTitle = findViewById(R.id.subTitle);

        isSearchForPro = StaticMethods.isSearchProfessionals();

        subTitle.setText(isSearchForPro ? "Find professionals" : "Find requests");

        emptyState = findViewById(R.id.emptyState);
        searchResults = findViewById(R.id.searchResults);

        professionalsAdapter = new ProfessionalsAdapter(new ArrayList<>());
        postsAdapter = new RequestAdapter(new ArrayList<>());

        searchResults.setAdapter(isSearchForPro ? professionalsAdapter : postsAdapter);

        searchResults.setLayoutManager(new LinearLayoutManager(this));

        btnNavigation = findViewById(R.id.btnNavigation);

    }

    private void InitCategories() {

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

    private ArrayList<String> GetCategoryGroup() {

        ArrayList<String> selected = new ArrayList<>();

        for (int i = 0; i < categoryCardGroup.getChildCount(); i++) {

            Chip chip = (Chip) categoryCardGroup.getChildAt(i);

            if (chip.isChecked()) {
                selected.add(chip.getText().toString());
            }
        }

        return selected;
    }

    private void InitListForFilter() {
        DatabaseReference ref;

        if (isSearchForPro) {
            ref = FirebaseDatabase.getInstance().getReference("Users");
        } else {
            ref = FirebaseDatabase.getInstance().getReference("Requests");
        }

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

                    android.util.Log.d("SEARCH_DEBUG",
                            "Firebase children: " + snapshot.getChildrenCount());

                    android.util.Log.d("SEARCH_DEBUG",
                            "Loaded professionals: " + fullList_Req.size());

                    UpdateResult();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
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

    private void UpdateResult() {

        ArrayList<String> categories = GetCategoryGroup();

        if (isSearchForPro) {

            filltred_Pro.clear();

            if (categories.isEmpty()) {
                professionalsAdapter.updateData(new ArrayList<>(fullList_Pro));
            } else {

                for (User user : fullList_Pro) {

                    if (user == null) continue;

                    if (containsSome(user.getTags(), categories)) {
                        filltred_Pro.add(user);
                    }

                }

                professionalsAdapter.updateData(new ArrayList<>(filltred_Pro));
            }


            UpdateEmptyState(categories.isEmpty() ? fullList_Pro.size() : filltred_Pro.size());

        } else {

            filltred_Req.clear();

            if (categories.isEmpty()) {
                postsAdapter.updateData(new ArrayList<>(fullList_Req));
            } else {
                for (Request request : fullList_Req) {

                    if (request == null) continue;

                    if (categories.isEmpty() || containsSome(request.getCategory(), categories)) {

                        filltred_Req.add(request);
                    }
                }
                postsAdapter.updateData(new ArrayList<>(filltred_Req));
            }

            UpdateEmptyState(categories.isEmpty() ? fullList_Req.size() : filltred_Req.size());
        }
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


    private void InitBar() {
        btnNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Получаем ID нажатой иконки
            if (itemId == R.id.navigation_Logout) {
                startActivity(new Intent(SeachActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            if (itemId == R.id.navigation_come_back) {
                Intent intent = getIntent();
                if (intent != null && intent.hasExtra("BACK_TO_CLASS")) {
                    String className = intent.getStringExtra("BACK_TO_CLASS");
                    try {
                        Class<?> backClass = Class.forName(className);
                        Intent backIntent = new Intent(SeachActivity.this, backClass);
                        backIntent.putExtra("BACK_TO_CLASS", SeachActivity.class.getName());
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

        InitListForFilter();

        InitBar();
    }
}