package com.xmall75.steamdealsalert.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.DealRepository;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.data.local.DealsPreferences;
import com.xmall75.steamdealsalert.databinding.ActivityMainBinding;
import com.xmall75.steamdealsalert.notification.NotificationHelper;
import com.xmall75.steamdealsalert.service.DealsForegroundService;
import com.xmall75.steamdealsalert.worker.SyncRunner;
import com.xmall75.steamdealsalert.worker.WorkScheduler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DealRepository repository;
    private DealAdapter adapter;
    private DealsPreferences preferences;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> notificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) sync();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new DealAdapter();
        binding.rvDeals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDeals.setAdapter(adapter);

        preferences = new DealsPreferences(this);

        binding.btnSync.setOnClickListener(v -> sync());
        binding.btnTestNotif.setOnClickListener(v -> sendTestNotification());

        startMonitoringService();

        repository = new DealRepository(getApplicationContext());
        repository.observeActive().observe(this, this::render);

        binding.btnSync.setOnClickListener(v -> sync());
        binding.btnSettings.setOnClickListener(v -> showSettingsDialog());
        binding.btnTestNotif.setOnClickListener(v -> sendTestNotification());

        if (needsNotificationPermission()) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            sync();
        }
    }

    private boolean needsNotificationPermission() {
        return Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void sync() {
        binding.tvStatus.setText(R.string.status_syncing);
        binding.btnSync.setEnabled(false);

        io.execute(() -> {
            String status;
            try {
                int sent = SyncRunner.syncAndNotify(getApplicationContext());
                status = getString(R.string.status_ok, sent);
            } catch (IOException e) {
                status = getString(R.string.status_error, e.getMessage());
            }
            final String result = status;
            runOnUiThread(() -> {
                binding.tvStatus.setText(result);
                binding.btnSync.setEnabled(true);
            });
        });
    }

    private void sendTestNotification() {
        if (needsNotificationPermission()) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }

        if (!NotificationHelper.canNotify(this)) {
            binding.tvStatus.setText(R.string.status_no_permission);
            return;
        }

        io.execute(() -> {
            try {
                DealEntity fake = new DealEntity();
                fake.steamAppId = "220";
                fake.title = "Game Deals Example";
                fake.normalPrice = 19.99;
                fake.ratingPercent = 90;
                fake.ratingText = "Very Positive";
                fake.thumb = "";

                NotificationHelper.notifyDeal(getApplicationContext(), fake);

                runOnUiThread(() -> binding.tvStatus.setText("Test Notification Sent!"));
            } catch (Exception e) {
                runOnUiThread(() -> binding.tvStatus.setText("Gagal: " + e.getMessage()));
            }
        });
    }

    private void render(List<DealEntity> deals) {
        if (deals == null || deals.isEmpty()) {
            binding.rvDeals.setVisibility(View.GONE);
            binding.tvEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.rvDeals.setVisibility(View.VISIBLE);
            binding.tvEmpty.setVisibility(View.GONE);
            adapter.setDeals(deals);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }

    private void startMonitoringService() {
        Intent serviceIntent = new Intent(this, DealsForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filter_settings, null);

        TextInputEditText etMinPrice = dialogView.findViewById(R.id.etMinPrice);
        TextInputEditText etMaxPrice = dialogView.findViewById(R.id.etMaxPrice);
        TextInputEditText etMinRating = dialogView.findViewById(R.id.etMinRating);
        TextInputEditText etMaxRating = dialogView.findViewById(R.id.etMaxRating);
        TextInputEditText etInterval = dialogView.findViewById(R.id.etInterval);

        etMinPrice.setText(String.valueOf(preferences.getMinPrice()));
        etMaxPrice.setText(String.valueOf(preferences.getMaxPrice()));
        etMinRating.setText(String.valueOf(preferences.getMinRating()));
        etMaxRating.setText(String.valueOf(preferences.getMaxRating()));
        etInterval.setText(String.valueOf(preferences.getSyncIntervalMinutes()));

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Filter & Sync Settings")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    try {
                        float minP = Float.parseFloat(etMinPrice.getText().toString());
                        float maxP = Float.parseFloat(etMaxPrice.getText().toString());
                        int minR = Integer.parseInt(etMinRating.getText().toString());
                        int maxR = Integer.parseInt(etMaxRating.getText().toString());
                        int interval = Integer.parseInt(etInterval.getText().toString());

                        preferences.saveFilterSettings(minP, maxP, minR, maxR, interval);

                        WorkScheduler.schedule(this, interval, true);

                        sync();
                    } catch (NumberFormatException e) {
                        binding.tvStatus.setText("Format input tidak valid!");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
