package com.xmall75.steamdealsalert.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DealDao {

    @Query("SELECT * FROM deals WHERE active = 1 ORDER BY firstSeenAt DESC")
    LiveData<List<DealEntity>> observeActive();

    @Query("SELECT * FROM deals WHERE steamAppId = :id LIMIT 1")
    DealEntity getById(String id);

    @Query("SELECT * FROM deals WHERE active = 1 AND notified = 0")
    List<DealEntity> getPendingNotify();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(DealEntity entity);

    @Query("UPDATE deals SET active = 0")
    void deactivateAll();

    @Query("UPDATE deals SET active = 0 WHERE steamAppId NOT IN (:activeIds)")
    void deactivateNotIn(List<String> activeIds);

    @Query("UPDATE deals SET notified = 1 WHERE steamAppId IN (:ids)")
    void markNotified(List<String> ids);
}
