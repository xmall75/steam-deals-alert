package com.xmall75.steamdealsalert.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xmall75.steamdealsalert.R;
import com.xmall75.steamdealsalert.data.DealRepository;
import com.xmall75.steamdealsalert.data.local.DealEntity;
import com.xmall75.steamdealsalert.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DealRepository repository;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new DealRepository(getApplicationContext());
        repository.observeActive().observe(this, this::render);

        binding.btnSync.setOnClickListener(v -> sync());
        sync();
    }

    private void sync() {
        binding.tvStatus.setText(R.string.status_syncing);
        binding.btnSync.setEnabled(false);

        io.execute(() -> {
            String status;
            try {
                List<DealEntity> pending = repository.sync(0);
                status = getString(R.string.status_ok, pending.size());
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

    private void render(List<DealEntity> deals) {
        if (deals == null || deals.isEmpty()) {
            binding.tvDeals.setText(R.string.empty_list);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (DealEntity d : deals) {
            sb.append(String.format(Locale.US, "%s%n  appid %s | normal $%.2f | rating %d%% (%s)%n%n",
                    d.title, d.steamAppId, d.normalPrice, d.ratingPercent,
                    d.ratingText == null ? "-" : d.ratingText));
        }
        binding.tvDeals.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        io.shutdown();
    }
}
