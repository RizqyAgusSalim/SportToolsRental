package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);

        TextView tvAppName = findViewById(R.id.tvTitle);
        tvAppName.setText("Sport Tools Rental");

        // Delay and redirect
        new Handler().postDelayed(() -> {
            // Check if user is logged in
            if (sessionManager.isLoggedIn()) {
                redirectToDashboard();
            } else {
                // Go to Landing Activity to choose role
                Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
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