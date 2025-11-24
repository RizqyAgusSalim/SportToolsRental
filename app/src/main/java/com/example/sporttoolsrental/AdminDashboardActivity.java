package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView cardManageTools, cardManageBookings, cardManageUsers, cardReports;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sessionManager = new SessionManager(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        cardManageTools = findViewById(R.id.cardManageTools);
        cardManageBookings = findViewById(R.id.cardManageBookings);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardReports = findViewById(R.id.cardReports);
        btnLogout = findViewById(R.id.btnLogout);

        // Set welcome message
        String name = sessionManager.getUserName();
        tvWelcome.setText("Selamat datang, " + name + "!");

        // Card click listeners
        cardManageTools.setOnClickListener(v -> {
            // TODO: Navigate to Manage Tools Activity
        });

        cardManageBookings.setOnClickListener(v -> {
            // TODO: Navigate to Manage Bookings Activity
        });

        cardManageUsers.setOnClickListener(v -> {
            // TODO: Navigate to Manage Users Activity
        });

        cardReports.setOnClickListener(v -> {
            // TODO: Navigate to Reports Activity
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

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(AdminDashboardActivity.this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}