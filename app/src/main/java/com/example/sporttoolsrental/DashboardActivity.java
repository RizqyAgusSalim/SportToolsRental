package com.example.sporttoolsrental;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private ListView lvTools;
    private Button btnRental;
    private ArrayList<ToolItem> toolsList;
    private ArrayList<String> rentalList;
    private ToolAdapter adapter;
    private String userName, userNIM, userPhone;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Ambil data dari intent
        userName = getIntent().getStringExtra("name");
        userNIM = getIntent().getStringExtra("nim");
        userPhone = getIntent().getStringExtra("phone");

        // Inisialisasi views
        tvWelcome = findViewById(R.id.tvWelcome);
        lvTools = findViewById(R.id.lvTools);
        btnRental = findViewById(R.id.btnRental);

        // Set welcome message
        tvWelcome.setText("Selamat Datang, " + userName + "!");

        // Inisialisasi daftar alat dengan foto dan detail lengkap
        toolsList = new ArrayList<>();
        toolsList.add(new ToolItem("Bola Sepak", "Bola sepak standar FIFA size 5", 10, 8, 2, R.drawable.oip));
        toolsList.add(new ToolItem("Bola Basket", "Bola basket Molten GG7X", 8, 6, 2, R.drawable.oip3));
        toolsList.add(new ToolItem("Bola Voli", "Bola voli Mikasa MVA200", 6, 5, 1, R.drawable.oip2));
        toolsList.add(new ToolItem("Raket Badminton", "Raket Yonex Voltric Z-Force II", 12, 10, 2, R.drawable.oip4));
        toolsList.add(new ToolItem("Raket Tenis", "Raket Wilson Pro Staff", 5, 4, 1, R.drawable.oip5));
        toolsList.add(new ToolItem("Net Voli", "Net voli standar internasional", 3, 3, 0, R.drawable.oip6));
        toolsList.add(new ToolItem("Cone Latihan", "Cone marker untuk latihan", 20, 18, 2, R.drawable.oip7));
        toolsList.add(new ToolItem("Bet Pingpong", "Bet pingpong Butterfly", 10, 8, 2, R.drawable.oip8));

        // Inisialisasi daftar peminjaman
        rentalList = new ArrayList<>();

        // Setup ListView dengan custom adapter
        adapter = new ToolAdapter(this, toolsList);
        lvTools.setAdapter(adapter);
        lvTools.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Set item click listener
        lvTools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
            }
        });

        // Button pinjam
        btnRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition == -1) {
                    Toast.makeText(DashboardActivity.this,
                            "Pilih alat yang ingin dipinjam!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ToolItem selectedTool = toolsList.get(selectedPosition);

                if (selectedTool.available == 0) {
                    Toast.makeText(DashboardActivity.this,
                            "Alat tidak tersedia!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tambah ke daftar peminjaman
                String rentalInfo = selectedTool.name + " - " + selectedTool.description +
                        "\nPeminjam: " + userName + " (NIM: " + userNIM + ")\nTelepon: " + userPhone;
                rentalList.add(rentalInfo);

                // Tampilkan dialog konfirmasi
                showRentalDialog(selectedTool.name);
            }
        });

        // Button lihat peminjaman
        Button btnViewRental = findViewById(R.id.btnViewRental);
        btnViewRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRentalListDialog();
            }
        });
    }

    private void showRentalDialog(String toolName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✅ Peminjaman Berhasil!");
        builder.setMessage("Anda telah meminjam: " + toolName +
                "\n\nSilakan ambil di ruang UKM Olahraga.");
        builder.setPositiveButton("OK", null);
        builder.show();

        selectedPosition = -1;
        lvTools.clearChoices();
    }

    private void showRentalListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📋 Daftar Peminjaman Anda");

        if (rentalList.isEmpty()) {
            builder.setMessage("Belum ada peminjaman.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rentalList.size(); i++) {
                sb.append((i + 1)).append(". ").append(rentalList.get(i)).append("\n\n");
            }
            builder.setMessage(sb.toString());
        }

        builder.setPositiveButton("Tutup", null);
        builder.show();
    }

    // Inner class untuk data tool
    class ToolItem {
        String name;
        String description;
        int available;
        int good;
        int damaged;
        int imageRes;

        ToolItem(String name, String description, int available, int good, int damaged, int imageRes) {
            this.name = name;
            this.description = description;
            this.available = available;
            this.good = good;
            this.damaged = damaged;
            this.imageRes = imageRes;
        }
    }

    // Custom Adapter untuk menampilkan item dengan foto
    class ToolAdapter extends ArrayAdapter<ToolItem> {

        ToolAdapter(Context context, ArrayList<ToolItem> tools) {
            super(context, 0, tools);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.item_list_tool, parent, false);
            }

            ToolItem tool = getItem(position);

            ImageView ivToolImage = convertView.findViewById(R.id.ivToolImage);
            TextView tvToolName = convertView.findViewById(R.id.tvToolName);
            TextView tvToolDesc = convertView.findViewById(R.id.tvToolDesc);
            TextView tvToolStatus = convertView.findViewById(R.id.tvToolStatus);

            if (tool != null) {
                ivToolImage.setImageResource(tool.imageRes);
                tvToolName.setText(tool.name);
                tvToolDesc.setText(tool.description);
                tvToolStatus.setText("Tersedia: " + tool.available + " | Baik: " + tool.good + " | Rusak: " + tool.damaged);
            }

            return convertView;
        }
    }
}