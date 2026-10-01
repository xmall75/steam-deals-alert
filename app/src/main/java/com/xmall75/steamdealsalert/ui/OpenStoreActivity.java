package com.xmall75.steamdealsalert.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OpenStoreActivity extends Activity {

    public static final String EXTRA_APP_ID = "app_id";

    public static String webUrl(String appId) {
        return "https://store.steampowered.com/app/" + appId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appId = getIntent().getStringExtra(EXTRA_APP_ID);
        if (appId != null && appId.matches("\\d+")) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("steam://store/" + appId)));
            } catch (ActivityNotFoundException e) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl(appId))));
                } catch (ActivityNotFoundException ignored) {
                }
            }
        }
        finish();
    }
}
