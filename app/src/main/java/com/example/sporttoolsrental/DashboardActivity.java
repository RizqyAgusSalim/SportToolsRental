package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvNim, tvPhone;
    private CardView cardRentTools, cardMyBookings, cardHistory, cardProfile;
    private Button btnLogout;
    private SessionManager sessionManager;
    private String userName, userNim, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        tvNim = findViewById(R.id.tvNim);
        tvPhone = findViewById(R.id.tvPhone);
        cardRentTools = findViewById(R.id.cardRentTools);
        cardMyBookings = findViewById(R.id.cardMyBookings);
        cardHistory = findViewById(R.id.cardHistory);
        cardProfile = findViewById(R.id.cardProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Get data from Intent (from MainActivity) or Session
        if (getIntent().hasExtra("name")) {
            // Data from MainActivity (quick access without login)
            userName = getIntent().getStringExtra("name");
            userNim = getIntent().getStringExtra("nim");
            userPhone = getIntent().getStringExtra("phone");
        } else if (sessionManager.isLoggedIn()) {
            // Data from Session (logged in user)
            userName = sessionManager.getUserName();
            userNim = sessionManager.getUserNim();
            userPhone = ""; // Can be fetched from database if needed
        } else {
            // No data, redirect to login
            redirectToLogin();
            return;
        }

        // Display user information
        displayUserInfo();

        // Card click listeners
        cardRentTools.setOnClickListener(v -> {
            // TODO: Navigate to Rent Tools Activity
            showToast("Fitur Sewa Alat (Coming Soon)");
        });

        cardMyBookings.setOnClickListener(v -> {
            // TODO: Navigate to My Bookings Activity
            showToast("Fitur Peminjaman Saya (Coming Soon)");
        });

        cardHistory.setOnClickListener(v -> {
            // TODO: Navigate to History Activity
            showToast("Fitur Riwayat (Coming Soon)");
        });

        cardProfile.setOnClickListener(v -> {
            // TODO: Navigate to Profile Activity
            showToast("Fitur Profil (Coming Soon)");
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        // Handle Back Button - Modern Way
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutDialog();
            }
        });
    }

    private void displayUserInfo() {
        tvWelcome.setText("Selamat datang, " + userName + "!");
        if (userNim != null && !userNim.isEmpty()) {
            tvNim.setText("NIM: " + userNim);
            tvNim.setVisibility(View.VISIBLE);
        } else {
            tvNim.setVisibility(View.GONE);
        }
        if (userPhone != null && !userPhone.isEmpty()) {
            tvPhone.setText("Telepon: " + userPhone);
            tvPhone.setVisibility(View.VISIBLE);
        } else {
            tvPhone.setVisibility(View.GONE);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(DashboardActivity.this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    if (sessionManager.isLoggedIn()) {
                        sessionManager.logout();
                    }
                    Intent intent = new Intent(DashboardActivity.this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}