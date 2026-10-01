package com.xmall75.steamdealsalert.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class Prefs {

    private static final String FILE = "settings";
    private static final String KEY_MIN_RATING = "min_rating";
    private static final String KEY_INTERVAL_MIN = "interval_minutes";
    private static final String KEY_MAX_PRICE = "max_price";

    public static final int DEFAULT_MIN_RATING = 0;
    public static final int DEFAULT_INTERVAL_MINUTES = 1;
    public static final double DEFAULT_MAX_PRICE = 0.5;

    private Prefs() {}

    private static SharedPreferences sp(Context c) {
        return c.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public static int getMinRating(Context c) {
        return sp(c).getInt(KEY_MIN_RATING, DEFAULT_MIN_RATING);
    }

    public static void setMinRating(Context c, int value) {
        sp(c).edit().putInt(KEY_MIN_RATING, value).apply();
    }

    public static int getIntervalMinutes(Context c) {
        return sp(c).getInt(KEY_INTERVAL_MIN, DEFAULT_INTERVAL_MINUTES);
    }

    public static void setIntervalMinutes(Context c, int value) {
        sp(c).edit().putInt(KEY_INTERVAL_MIN, value).apply();
    }

    public static double getMaxPrice(Context c) {
        return (double) sp(c).getFloat(KEY_MAX_PRICE, (float) DEFAULT_MAX_PRICE);
    }

    public static void setMaxPrice(Context c, double value) {
        sp(c).edit().putFloat(KEY_MAX_PRICE, (float) value).apply();
    }
}