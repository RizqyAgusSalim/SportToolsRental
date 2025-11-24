package com.example.sporttoolsrental;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvRole;
    private String role;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get role from intent
        role = getIntent().getStringExtra("role");

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvRole = findViewById(R.id.tvRole);

        // Set title based on role
        if (role.equals("admin")) {
            tvRole.setText("Login Admin");
        } else {
            tvRole.setText("Login Mahasiswa");
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });

        // Handle Back Button - Modern Way
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.login(email, password, role);

        if (cursor.moveToFirst()) {
            // Login successful
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String userEmail = cursor.getString(2);
            String userRole = cursor.getString(6);
            String nim = cursor.getString(5);

            // Create session
            sessionManager.createLoginSession(id, name, userEmail, userRole, nim);

            Toast.makeText(this, "Login berhasil! Selamat datang, " + name,
                    Toast.LENGTH_SHORT).show();

            // Redirect to dashboard
            Intent intent;
            if (role.equals("admin")) {
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, DashboardActivity.class);
            }
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}