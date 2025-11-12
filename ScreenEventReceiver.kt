package com.chidu.usagelogger.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chidu.usagelogger.data.AppDatabase
import com.chidu.usagelogger.data.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenEventReceiver : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val type = when (intent.action) {
            Intent.ACTION_SCREEN_ON -> "SCREEN_ON"
            Intent.ACTION_SCREEN_OFF -> "SCREEN_OFF"
            Intent.ACTION_USER_PRESENT -> "UNLOCK"
            else -> "UNKNOWN"
        }
        scope.launch {
            AppDatabase.get(context).events().insert(
                Event(
                    ts = System.currentTimeMillis(),
                    type = type,
                    packageName = null,
                    title = null,
                    details = null
                )
            )
        }
    }
}
