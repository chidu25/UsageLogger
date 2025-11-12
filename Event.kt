package com.chidu.usagelogger.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ts: Long,
    val type: String,          // e.g. APP_USAGE, NOTIF, SCREEN_ON
    val packageName: String?,
    val title: String?,
    val details: String?
)
