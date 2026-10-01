package com.xmall75.steamdealsalert.data;

import com.xmall75.steamdealsalert.data.remote.DealDto;

public final class DealFilter {

    private static final double MIN_SAVINGS = 99.99;

    private DealFilter() {}

    /**
     * @param minRatingPercent rating Steam minimum (0 = terima semua, termasuk yang belum punya rating)
     */
    public static boolean isFreePromo(DealDto d, int minRatingPercent) {
        if (d == null) return false;

        String appId = d.steamAppID;
        if (appId == null || appId.isEmpty() || "0".equals(appId)) return false;

        if (toDouble(d.savings) < MIN_SAVINGS) return false;
        if (toDouble(d.normalPrice) <= 0) return false;
        if (toDouble(d.salePrice) > 0) return false;

        return toInt(d.steamRatingPercent) >= minRatingPercent;
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
