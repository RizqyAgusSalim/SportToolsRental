package com.example.sporttoolsrental;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etNIM, etPhone;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi views
        etName = findViewById(R.id.etName);
        etNIM = findViewById(R.id.etNIM);
        etPhone = findViewById(R.id.etPhone);
        btnNext = findViewById(R.id.btnNext);

        // Tombol "Lanjut" → pindah ke DashboardActivity
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String nim = etNIM.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                // Validasi input
                if (name.isEmpty() || nim.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kirim data ke DashboardActivity
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("nim", nim);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });
    }

    // 🔙 Ketika tombol "Back" ditekan, kembali ke SplashActivity
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        // Hapus semua activity di atas SplashActivity agar tidak menumpuk
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Tutup MainActivity agar tidak menumpuk di back stack
    }
}
