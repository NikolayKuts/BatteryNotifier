package com.example.batterynotifier.domain

data class BatteryInfoContainer(
    val batteryCondition: Int? = null,
    @field:JvmField
    val isCharging: Boolean? = null,
    val lastChargingTime: Long? = null
) {
    companion object {
        const val MIN_PERMISSIBLE_BATTERY_LEVEL = 50
    }
}