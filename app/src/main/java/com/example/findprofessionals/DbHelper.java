package com.example.findprofessionals;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DbHelper {
    private static final String DB_URL = "https://projectyodbetfirebase-default-rtdb.firebaseio.com/";
    public static FirebaseDatabase get() {
        return FirebaseDatabase.getInstance(DB_URL);
    }

    private static DatabaseReference get_db_reference(String tableName) {
        FirebaseDatabase db = get();
        return db.getReference(tableName);
    }

    public static StorageReference get_storage_reference(String path) {
        return FirebaseStorage.getInstance().getReference(path);
    }
}