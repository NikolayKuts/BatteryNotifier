package com.example.batterynotifier.presintation.presenters

import android.content.Context
import com.example.batterynotifier.domain.BatteryInfoContainer
import com.example.batterynotifier.domain.Notifier
import com.example.batterynotifier.presintation.COLLECTION_NAME
import com.example.batterynotifier.presintation.DOCUMENT_NAME
import com.example.batterynotifier.presintation.interfaces.WatcherView
import com.example.batterynotifier.presintation.fragments.WatcherFragment
import com.example.batterynotifier.presintation.screens.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class WatcherPresenter(private val context: Context, private val view: WatcherView? = null) {
    private val firestore = FirebaseFirestore.getInstance()

    fun downloadData() {
        firestore.collection(COLLECTION_NAME)
            .document(DOCUMENT_NAME)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val container = documentSnapshot.toObject<BatteryInfoContainer>()
                view?.showInfo(container)
            }
    }

    fun showNotificationIfBatteryStateIsNotPermissible() {
        onGetBatteryInfoContainer { container ->
            container?.let {
                if (
                    (it.batteryCondition ?: 0) < BatteryInfoContainer.MIN_PERMISSIBLE_BATTERY_LEVEL
                    && it.isCharging != true
                ) {
                    Notifier(context).apply {
                        setChannel()
                        setPendingIntent(
                            MainActivity::class.java,
                            WatcherFragment.WATCHER_FRAGMENT_TAG
                        )
                        pushNotification(Notifier.DEFAULT_WATCHER_NOTIFICATION_TEXT)
                    }
                }
            }
        }
    }

    private fun onGetBatteryInfoContainer(handleData: (BatteryInfoContainer?) -> Unit) {
        firestore.collection(COLLECTION_NAME)
            .document(DOCUMENT_NAME)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val container = documentSnapshot.toObject<BatteryInfoContainer>()
                handleData(container)
            }
    }
}