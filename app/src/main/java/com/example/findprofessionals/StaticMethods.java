package com.example.findprofessionals;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StaticMethods {
    private static DatabaseReference get_db_reference(String tableName) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        return db.getReference(tableName);
    }

    public static StorageReference get_storage_reference(String path) {
        return FirebaseStorage.getInstance().getReference(path);
    }

}