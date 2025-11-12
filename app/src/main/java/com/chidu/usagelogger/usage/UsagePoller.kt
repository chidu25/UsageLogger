package com.chidu.usagelogger.usage

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.chidu.usagelogger.data.AppDatabase
import com.chidu.usagelogger.data.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class UsagePoller(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val usm = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - 15 * 60_000
        val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)

        val dao = AppDatabase.get(applicationContext).events()
        stats?.forEach { s ->
            if (s.totalTimeInForeground > 0) {
                val details = """{"totalMs":${s.totalTimeInForeground},"lastTimeUsed":${s.lastTimeUsed}}"""
                dao.insert(
                    Event(
                        ts = end,
                        type = "APP_USAGE",
                        packageName = s.packageName,
                        title = null,
                        details = details
                    )
                )
            }
        }
        Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "usage_poller"

        fun schedule(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<UsagePoller>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(ctx)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, req)
        }
    }
}
