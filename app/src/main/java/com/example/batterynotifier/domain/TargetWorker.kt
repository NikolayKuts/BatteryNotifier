package com.example.batterynotifier.domain

import android.content.Context
import androidx.work.*
import com.example.batterynotifier.presintation.presenters.TargetPresenter
import java.util.concurrent.TimeUnit

class TargetWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        TargetPresenter(applicationContext).apply {
            loadData()
            onCheckBatteryState()
        }
        scheduleWork()
        return Result.success()
    }

    private fun scheduleWork() {
        val workRequest = OneTimeWorkRequestBuilder<TargetWorker>().addTag(UNIQUE_WORK_TAG)
            .setInitialDelay(INITIAL_DELAY_MINUTES, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_TAG,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}