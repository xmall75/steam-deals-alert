package com.xmall75.steamdealsalert.data;

import com.xmall75.steamdealsalert.data.remote.DealDto;

public final class DealFilter {

    private DealFilter() {}

    public static boolean isValidDeal(DealDto d, double maxPrice, int minRatingPercent) {
        if (d == null) return false;

        String appId = d.steamAppID;
        if (appId == null || appId.isEmpty() || "0".equals(appId)) return false;

        double salePrice = toDouble(d.salePrice);
        double savings = toDouble(d.savings);
        int rating = toInt(d.steamRatingPercent);

        if (savings <= 0 || salePrice > maxPrice) return false;

        if (minRatingPercent > 0 && rating > 0) {
            return rating >= minRatingPercent;
        }

        return true;
    }

    public static double toDouble(String s) {
        if (s == null) return 0;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int toInt(String s) {
        if (s == null) return 0;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}