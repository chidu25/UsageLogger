package com.chidu.usagelogger.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.chidu.usagelogger.data.AppDatabase
import com.chidu.usagelogger.data.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotifListener : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName
        val extras = sbn.notification.extras
        val title = extras.getCharSequence("android.title")?.toString()
        val text = extras.getCharSequence("android.text")?.toString()

        scope.launch {
            AppDatabase.get(this@NotifListener).events().insert(
                Event(
                    ts = System.currentTimeMillis(),
                    type = "NOTIF",
                    packageName = pkg,
                    title = title,
                    details = text?.take(500)
                )
            )
        }
    }
}
