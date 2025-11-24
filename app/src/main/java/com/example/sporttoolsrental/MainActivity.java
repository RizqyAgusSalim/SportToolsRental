package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etNIM, etPhone;
    private Button btnNext;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize session and database
        sessionManager = new SessionManager(this);
        dbHelper = new DatabaseHelper(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            redirectToDashboard();
            return;
        }

        setContentView(R.layout.activity_main);

        // Test Firebase Connection
        checkFirebaseConnection();

        // Initialize Views
        etName = findViewById(R.id.etName);
        etNIM = findViewById(R.id.etNIM);
        etPhone = findViewById(R.id.etPhone);
        btnNext = findViewById(R.id.btnNext);

        // Button Next - Save to Firebase and proceed
        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String nim = etNIM.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || nim.isEmpty() || phone.isEmpty()) {
                Toast.makeText(MainActivity.this,
                        "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to Firebase
            saveToFirebase(name, nim, phone);

            // Navigate to Dashboard
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("nim", nim);
            intent.putExtra("phone", phone);
            startActivity(intent);
        });

        // Handle Back Button - Go to Landing Activity to choose role
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void redirectToDashboard() {
        String role = sessionManager.getUserRole();
        Intent intent;

        if (role.equals("admin")) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, DashboardActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private void saveToFirebase(String name, String nim, String phone) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("mahasiswa")
                .child(nim);

        // Create student data object
        MahasiswaData studentData = new MahasiswaData(name, nim, phone);

        ref.setValue(studentData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this,
                        "Data berhasil disimpan ke Firebase ✔️", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this,
                        "Gagal menyimpan data ke Firebase ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFirebaseConnection() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("test_connection");

        ref.setValue("connected_test").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this,
                        "Firebase Connected ✔️", Toast.LENGTH_SHORT).show();

                ref.get().addOnSuccessListener(data -> {
                    if (data.exists()) {
                        Toast.makeText(MainActivity.this,
                                "Firebase Read Success ✔️", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Firebase Read Failed ❌", Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                Toast.makeText(MainActivity.this,
                        "Firebase NOT Connected ❌", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Data class for Firebase
    public static class MahasiswaData {
        public String name;
        public String nim;
        public String phone;
        public long timestamp;

        public MahasiswaData() {
            // Default constructor required for Firebase
        }

        public MahasiswaData(String name, String nim, String phone) {
            this.name = name;
            this.nim = nim;
            this.phone = phone;
            this.timestamp = System.currentTimeMillis();
        }
    }
}