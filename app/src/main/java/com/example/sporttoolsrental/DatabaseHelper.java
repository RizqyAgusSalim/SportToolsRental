package com.example.sporttoolsrental;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "SportToolsRental.db";
    private static final int DATABASE_VERSION = 2; // Upgraded version

    // Table Users (Mahasiswa & Admin)
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_PHONE = "phone";
    private static final String COL_NIM = "nim";
    private static final String COL_ROLE = "role";

    // Table Products (untuk alat olahraga)
    private static final String TABLE_PRODUCTS = "products";
    private static final String COL_PRODUCT_ID = "id";
    private static final String COL_PRODUCT_NAME = "name";
    private static final String COL_PRODUCT_DESC = "description";
    private static final String COL_PRODUCT_PRICE = "price";
    private static final String COL_PRODUCT_CATEGORY = "category";
    private static final String COL_PRODUCT_IMAGE = "imageUrl";
    private static final String COL_PRODUCT_AVAILABLE = "available";

    // Firestore instance
    private final FirebaseFirestore firestore;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT, " +
                COL_NIM + " TEXT, " +
                COL_ROLE + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // Create Products table
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRODUCT_NAME + " TEXT NOT NULL, " +
                COL_PRODUCT_DESC + " TEXT, " +
                COL_PRODUCT_PRICE + " REAL, " +
                COL_PRODUCT_CATEGORY + " TEXT, " +
                COL_PRODUCT_IMAGE + " TEXT, " +
                COL_PRODUCT_AVAILABLE + " INTEGER DEFAULT 1)";
        db.execSQL(createProductsTable);

        // Insert default admin
        ContentValues admin = new ContentValues();
        admin.put(COL_NAME, "Admin");
        admin.put(COL_EMAIL, "admin@sportrental.com");
        admin.put(COL_PASSWORD, "admin123");
        admin.put(COL_PHONE, "081234567890");
        admin.put(COL_ROLE, "admin");
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create Products table if upgrading from version 1
            String createProductsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                    COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PRODUCT_NAME + " TEXT NOT NULL, " +
                    COL_PRODUCT_DESC + " TEXT, " +
                    COL_PRODUCT_PRICE + " REAL, " +
                    COL_PRODUCT_CATEGORY + " TEXT, " +
                    COL_PRODUCT_IMAGE + " TEXT, " +
                    COL_PRODUCT_AVAILABLE + " INTEGER DEFAULT 1)";
            db.execSQL(createProductsTable);
        }
    }

    // ==================== USER OPERATIONS ====================

    // Register Mahasiswa
    public boolean registerMahasiswa(String name, String email, String password,
                                     String phone, String nim) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_PHONE, phone);
        values.put(COL_NIM, nim);
        values.put(COL_ROLE, "mahasiswa");

        long result = db.insert(TABLE_USERS, null, values);

        // Sync to Firestore if registration successful
        if (result != -1) {
            syncUserToFirestore(name, email, phone, nim, "mahasiswa");
        }

        return result != -1;
    }

    // Register Admin
    public boolean registerAdmin(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_PHONE, phone);
        values.put(COL_ROLE, "admin");

        long result = db.insert(TABLE_USERS, null, values);

        // Sync to Firestore
        if (result != -1) {
            syncUserToFirestore(name, email, phone, null, "admin");
        }

        return result != -1;
    }

    // Sync user to Firestore
    private void syncUserToFirestore(String name, String email, String phone, String nim, String role) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("role", role);
        userData.put("timestamp", System.currentTimeMillis());

        if (nim != null) {
            userData.put("nim", nim);
        }

        firestore.collection("users")
                .add(userData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "✅ User synced to Firestore: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "❌ Failed to sync user to Firestore", e);
                    }
                });
    }

    // Login
    public Cursor login(String email, String password, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=? AND " +
                COL_PASSWORD + "=? AND " + COL_ROLE + "=?";
        return db.rawQuery(query, new String[]{email, password, role});
    }

    // Check if email exists
    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get user data
    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_EMAIL + "=?", new String[]{email});
    }

    // ==================== PRODUCT OPERATIONS (SQLite + Firestore) ====================

    // Callback interfaces
    public interface ProductCallback {
        void onSuccess(String message);
        void onFailure(Exception e);
    }

    public interface ProductsCallback {
        void onSuccess(List<Map<String, Object>> products);
        void onFailure(Exception e);
    }

    // Add Product (Local + Cloud)
    public void addProduct(String name, String description, double price,
                           String category, String imageUrl, boolean available,
                           final ProductCallback callback) {
        // Save to local SQLite first
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, name);
        values.put(COL_PRODUCT_DESC, description);
        values.put(COL_PRODUCT_PRICE, price);
        values.put(COL_PRODUCT_CATEGORY, category);
        values.put(COL_PRODUCT_IMAGE, imageUrl);
        values.put(COL_PRODUCT_AVAILABLE, available ? 1 : 0);

        long localResult = db.insert(TABLE_PRODUCTS, null, values);

        if (localResult != -1) {
            // Then sync to Firestore
            Map<String, Object> product = new HashMap<>();
            product.put("name", name);
            product.put("description", description);
            product.put("price", price);
            product.put("category", category);
            product.put("imageUrl", imageUrl);
            product.put("available", available);
            product.put("createdAt", System.currentTimeMillis());

            firestore.collection("products")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "✅ Product synced to Firestore: " + documentReference.getId());
                            if (callback != null) {
                                callback.onSuccess("Product added successfully!");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "❌ Failed to sync product to Firestore", e);
                            if (callback != null) {
                                callback.onFailure(e);
                            }
                        }
                    });
        } else {
            if (callback != null) {
                callback.onFailure(new Exception("Failed to save product locally"));
            }
        }
    }

    // Get All Products from Firestore (Real-time data)
    public void getAllProductsFromFirestore(final ProductsCallback callback) {
        firestore.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Map<String, Object> product = document.getData();
                            product.put("id", document.getId());
                            products.add(product);
                        }
                        Log.d(TAG, "✅ Loaded " + products.size() + " products from Firestore");
                        callback.onSuccess(products);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "❌ Failed to load products from Firestore", e);
                        callback.onFailure(e);
                    }
                });
    }

    // Get All Products from Local SQLite (Offline fallback)
    public Cursor getAllProductsFromLocal() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " +
                COL_PRODUCT_AVAILABLE + " = 1", null);
    }

    // Test Firestore Connection
    public void testFirestoreConnection() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("test", "Connection successful from DatabaseHelper!");
        testData.put("timestamp", System.currentTimeMillis());

        firestore.collection("test")
                .add(testData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "✅ Firestore connection test successful! Doc ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "❌ Firestore connection test failed", e);
                    }
                });
    }

    // Sync local products to Firestore (for migration)
    public void syncAllProductsToFirestore() {
        Cursor cursor = getAllProductsFromLocal();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE));
                boolean available = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_AVAILABLE)) == 1;

                Map<String, Object> product = new HashMap<>();
                product.put("name", name);
                product.put("description", desc);
                product.put("price", price);
                product.put("category", category);
                product.put("imageUrl", imageUrl);
                product.put("available", available);
                product.put("createdAt", System.currentTimeMillis());

                firestore.collection("products").add(product);

            } while (cursor.moveToNext());

            Log.d(TAG, "✅ All local products synced to Firestore");
        }
        cursor.close();
    }
}