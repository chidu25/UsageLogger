package com.chidu.usagelogger.permissions

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

object PermissionHelper {
    fun hasUsageAccess(ctx: Context): Boolean {
        val usm = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - 60_000
        val list = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
        return list != null && list.isNotEmpty()
    }

    fun openUsageAccess(ctx: Context) {
        Toast.makeText(ctx, "Grant Usage Access to log app time", Toast.LENGTH_LONG).show()
        ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openNotifAccess(ctx: Context) {
        Toast.makeText(ctx, "Enable Notification Access for logging notifications", Toast.LENGTH_LONG).show()
        ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
