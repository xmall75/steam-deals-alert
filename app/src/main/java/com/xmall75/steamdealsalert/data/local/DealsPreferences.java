package com.xmall75.steamdealsalert.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class DealsPreferences {

    private static final String PREF_NAME = "steam_deals_prefs";

    private static final String KEY_MIN_PRICE = "min_price";
    private static final String KEY_MAX_PRICE = "max_price";
    private static final String KEY_MIN_RATING = "min_rating";
    private static final String KEY_MAX_RATING = "max_rating";
    private static final String KEY_SYNC_INTERVAL = "sync_interval";

    private final SharedPreferences prefs;

    public DealsPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // default: min price 0, max price 0.5, min rating 70, max rating 100, interval 60 mins
    public float getMinPrice() { return prefs.getFloat(KEY_MIN_PRICE, 0f); }
    public float getMaxPrice() { return prefs.getFloat(KEY_MAX_PRICE, 0.5f); }
    public int getMinRating() { return prefs.getInt(KEY_MIN_RATING, 70); }
    public int getMaxRating() { return prefs.getInt(KEY_MAX_RATING, 100); }
    public int getSyncIntervalMinutes() { return prefs.getInt(KEY_SYNC_INTERVAL, 60); }

    public void saveFilterSettings(float minPrice, float maxPrice, int minRating, int maxRating, int intervalMinutes) {
        prefs.edit()
                .putFloat(KEY_MIN_PRICE, minPrice)
                .putFloat(KEY_MAX_PRICE, maxPrice)
                .putInt(KEY_MIN_RATING, minRating)
                .putInt(KEY_MAX_RATING, maxRating)
                .putInt(KEY_SYNC_INTERVAL, intervalMinutes)
                .apply();
    }
}