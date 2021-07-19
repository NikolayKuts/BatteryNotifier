package com.example.batterynotifier.domain

import android.content.Context
import androidx.work.*
import com.example.batterynotifier.presintation.presenters.WatcherPresenter


class WatcherWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result  {
        WatcherPresenter(applicationContext).showNotificationIfBatteryStateIsNotPermissible()
        return Result.success()
    }
}