package com.example.batterynotifier.presintation.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.batterynotifier.R
import com.example.batterynotifier.domain.Notifier
import com.example.batterynotifier.presintation.FIRST_APPLICATION_START_TAG
import com.example.batterynotifier.presintation.PREFERENCE_FILE_KEY
import com.example.batterynotifier.presintation.IS_TARGET_MODE_TURNED_ON
import com.example.batterynotifier.presintation.IS_WATCHER_MODE_TURNED_ON
import com.example.batterynotifier.presintation.fragments.ChoiceFragment
import com.example.batterynotifier.presintation.fragments.TargetFragment
import com.example.batterynotifier.presintation.fragments.WatcherFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreference =
            applicationContext.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
        val isFirstStart = sharedPreference.getBoolean(FIRST_APPLICATION_START_TAG, true)
        val isTargetModeTurnedOn = sharedPreference.getBoolean(IS_TARGET_MODE_TURNED_ON, false)
        val isWatcherModeTurnedOn = sharedPreference.getBoolean(IS_WATCHER_MODE_TURNED_ON, false)

        val fragmentTag = intent.getStringExtra(Notifier.FRAGMENT_TAG)

        val transaction = supportFragmentManager.beginTransaction()

        if (isFirstStart) {
            transaction.replace(R.id.fragmentContainerView, ChoiceFragment()).commit()
        } else if (fragmentTag == WatcherFragment.WATCHER_FRAGMENT_TAG || isWatcherModeTurnedOn) {
            transaction.replace(R.id.fragmentContainerView, WatcherFragment()).commit()
        } else if (isTargetModeTurnedOn) {
            transaction.replace(R.id.fragmentContainerView, TargetFragment()).commit()
        }
    }
}