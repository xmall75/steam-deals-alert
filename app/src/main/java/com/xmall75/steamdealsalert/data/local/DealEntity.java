package com.xmall75.steamdealsalert.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deals")
public class DealEntity {

    @PrimaryKey
    @NonNull
    public String steamAppId = "";

    public String title;
    public double normalPrice;
    public int ratingPercent;
    public String ratingText;
    public String thumb;

    public long firstSeenAt;

    public boolean notified;

    public boolean active;
}
