package com.chidu.usagelogger.export

import android.content.Context
import android.net.Uri
import com.chidu.usagelogger.data.AppDatabase
import org.apache.commons.csv.CSVFormat
import java.io.OutputStreamWriter

object Exporter {
    suspend fun exportTo(ctx: Context, uri: Uri) {
        val dao = AppDatabase.get(ctx).events()
        val rows = dao.list(limit = Int.MAX_VALUE, offset = 0)

        ctx.contentResolver.openOutputStream(uri)?.use { os ->
            OutputStreamWriter(os).use { w ->
                val printer = CSVFormat.DEFAULT
                    .withHeader("ts","type","package","title","details")
                    .print(w)
                for (e in rows) {
                    printer.printRecord(e.ts, e.type, e.packageName ?: "", e.title ?: "", e.details ?: "")
                }
                printer.flush()
            }
        }
    }
}
