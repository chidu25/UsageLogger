package com.chidu.usagelogger.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Insert
    suspend fun insert(e: Event)

    @Query("SELECT * FROM events ORDER BY ts DESC LIMIT :limit OFFSET :offset")
    suspend fun list(limit: Int, offset: Int): List<Event>

    @Query("DELETE FROM events")
    suspend fun clear()
}
