package com.example.findprofessionals;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StaticMethods {
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        StaticMethods.user = user;
    }

}