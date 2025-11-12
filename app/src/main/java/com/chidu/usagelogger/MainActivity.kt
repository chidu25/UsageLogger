package com.chidu.usagelogger

import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.chidu.usagelogger.data.AppDatabase
import com.chidu.usagelogger.export.Exporter
import com.chidu.usagelogger.permissions.PermissionHelper
import com.chidu.usagelogger.system.ScreenEventReceiver
import com.chidu.usagelogger.usage.UsagePoller
import kotlinx.coroutines.launch
import android.content.Intent

class MainActivity : ComponentActivity() {

    private val screenReceiver = ScreenEventReceiver()

    private val createCsv = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null) {
            lifecycleScope.launch {
                Exporter.exportTo(this@MainActivity, uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnUsageAccess).setOnClickListener {
            if (!PermissionHelper.hasUsageAccess(this)) {
                PermissionHelper.openUsageAccess(this)
            } else {
                UsagePoller.schedule(this)
            }
        }

        findViewById<Button>(R.id.btnNotifAccess).setOnClickListener {
            PermissionHelper.openNotifAccess(this)
        }

        findViewById<Button>(R.id.btnExport).setOnClickListener {
            createCsv.launch("usage_logger_${System.currentTimeMillis()}.csv")
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            lifecycleScope.launch {
                AppDatabase.get(this@MainActivity).events().clear()
            }
        }

        if (PermissionHelper.hasUsageAccess(this)) {
            UsagePoller.schedule(this)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun onStop() {
        unregisterReceiver(screenReceiver)
        super.onStop()
    }
}
