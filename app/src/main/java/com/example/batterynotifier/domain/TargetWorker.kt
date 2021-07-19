package com.example.batterynotifier.domain

import android.content.Context
import androidx.work.*
import com.example.batterynotifier.presintation.presenters.TargetPresenter

class TargetWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        TargetPresenter(applicationContext).apply {
            loadData()
            onCheckBatteryState()
        }
        return Result.success()
    }
}