package com.example.sporttoolsrental;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SportToolsRental.db";
    private static final int DATABASE_VERSION = 1;

    // Table Users (Mahasiswa & Admin)
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_PHONE = "phone";
    private static final String COL_NIM = "nim"; // untuk mahasiswa
    private static final String COL_ROLE = "role"; // "mahasiswa" atau "admin"

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_PHONE + " TEXT, " +
                COL_NIM + " TEXT, " +
                COL_ROLE + " TEXT NOT NULL)";
        db.execSQL(createTable);

        // Insert admin default
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

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
        return result != -1;
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
}