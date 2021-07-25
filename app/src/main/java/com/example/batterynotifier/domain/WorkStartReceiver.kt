package com.example.batterynotifier.domain

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.*
import com.example.batterynotifier.presintation.IS_TARGET_MODE_TURNED_ON
import com.example.batterynotifier.presintation.IS_WATCHER_MODE_TURNED_ON
import com.example.batterynotifier.presintation.PREFERENCE_FILE_KEY
import java.util.concurrent.TimeUnit

class WorkStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Toast.makeText(context, "broadcast_receiver", Toast.LENGTH_SHORT).show()

            val sharedPreferences =
                context?.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
            val isTargetModeTurnedOn =
                sharedPreferences?.getBoolean(IS_TARGET_MODE_TURNED_ON, false) ?: false
            val isWatcherModeTurnedOn =
                sharedPreferences?.getBoolean(IS_WATCHER_MODE_TURNED_ON, false) ?: false

            val workRequest = when {
                isTargetModeTurnedOn -> {
                    OneTimeWorkRequestBuilder<TargetWorker>().addTag(UNIQUE_WORK_TAG)
                        .build()
                }
                isWatcherModeTurnedOn -> {
                    OneTimeWorkRequestBuilder<WatcherWorker>().addTag(UNIQUE_WORK_TAG)
                        .build()
                }
                else -> null
            }

            if (context != null && workRequest != null) {
                WorkManager.getInstance(context).enqueueUniqueWork(
                    UNIQUE_WORK_TAG,
                    ExistingWorkPolicy.REPLACE,
                    workRequest)
            }
        }
    }
}