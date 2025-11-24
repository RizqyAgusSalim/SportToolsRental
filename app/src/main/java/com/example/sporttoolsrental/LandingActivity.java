package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LandingActivity extends AppCompatActivity {

    private Button btnMahasiswa, btnAdmin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            redirectToDashboard();
            return;
        }

        btnMahasiswa = findViewById(R.id.btnMahasiswa);
        btnAdmin = findViewById(R.id.btnAdmin);

        btnMahasiswa.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            intent.putExtra("role", "mahasiswa");
            startActivity(intent);
        });

        btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
            intent.putExtra("role", "admin");
            startActivity(intent);
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
}