package com.example.batterynotifier.presintation.interfaces

import com.example.batterynotifier.domain.BatteryInfoContainer

interface WatcherView {
    fun showInfo(container: BatteryInfoContainer?)
}