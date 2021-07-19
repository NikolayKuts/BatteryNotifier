package com.example.batterynotifier.presintation.presenters

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.Toast
import com.example.batterynotifier.domain.BatteryInfoContainer
import com.example.batterynotifier.domain.Notifier
import com.example.batterynotifier.presintation.COLLECTION_NAME
import com.example.batterynotifier.presintation.DOCUMENT_NAME
import com.google.firebase.firestore.FirebaseFirestore

class TargetPresenter(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    fun loadData() {
        addData(getBatteryInfoContainer())
    }

    fun onCheckBatteryState() {
        val batteryLevel = getBatteryLevel()
        val isCharging = getChargingState()
        if (batteryLevel < 50 && !isCharging) {
            showNotification()
        }
    }

    private fun addData(container: BatteryInfoContainer) {
        firestore.collection(COLLECTION_NAME)
            .document(DOCUMENT_NAME)
            .set(container)
            .addOnFailureListener { error ->
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getBatteryInfoContainer(): BatteryInfoContainer {
        val batteryLevel = getBatteryLevel()
        val chargingState = getChargingState()
        val currentTime = System.currentTimeMillis()
        return BatteryInfoContainer(batteryLevel, chargingState, currentTime)
    }

    private fun showNotification() {
        val notifier = Notifier(context)
        notifier.setChannel()
        notifier.pushNotification(Notifier.DEFAULT_TARGET_NOTIFICATION_TEXT)
    }

    private fun getBatteryLevel(): Int {
        var batteryLevel = -1
        val batteryStatus: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        batteryStatus?.let { intent ->
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        }
        return batteryLevel
    }

    private fun getChargingState(): Boolean {
        var chargingState = false
        val batteryStatus: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        batteryStatus?.let { intent ->
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            chargingState = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL
        }
        return chargingState
    }
}