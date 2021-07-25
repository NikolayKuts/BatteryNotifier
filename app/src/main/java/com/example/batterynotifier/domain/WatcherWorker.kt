package com.example.batterynotifier.domain

import android.content.Context
import androidx.work.*
import com.example.batterynotifier.presintation.presenters.WatcherPresenter
import java.util.concurrent.TimeUnit

class WatcherWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result  {
        WatcherPresenter(applicationContext).showNotificationIfBatteryStateIsNotPermissible()
        scheduleWork()
        return Result.success()
    }

    private fun scheduleWork() {
        val workRequest = OneTimeWorkRequestBuilder<WatcherWorker>().addTag(UNIQUE_WORK_TAG)
            .setInitialDelay(INITIAL_DELAY_MINUTES, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest)
    }
}