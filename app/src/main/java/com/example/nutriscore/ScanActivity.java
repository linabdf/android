package com.example.nutriscore;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;

public class ScanActivity extends AppCompatActivity {

    private ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    Intent intent = new Intent(this, ProductActivity.class);
                    intent.putExtra("BARCODE", result.getContents());
                    startActivity(intent);
                }

            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Scannez un code-barres");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
            barcodeLauncher.launch(options);
        });

        Button btnProfile = findViewById(R.id.btnProfileEdit);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileEditActivity.class);
            startActivity(intent);
        });

    }
}