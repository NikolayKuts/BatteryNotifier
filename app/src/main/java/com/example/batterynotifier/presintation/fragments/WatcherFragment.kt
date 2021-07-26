package com.example.batterynotifier.presintation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.batterynotifier.R
import com.example.batterynotifier.databinding.FragmentWatcherBinding
import com.example.batterynotifier.domain.BatteryInfoContainer
import com.example.batterynotifier.domain.WatcherWorker
import com.example.batterynotifier.presintation.*
import com.example.batterynotifier.presintation.interfaces.WatcherView
import com.example.batterynotifier.presintation.presenters.WatcherPresenter
import java.text.DateFormat
import java.util.*
import com.example.batterynotifier.domain.UNIQUE_WORK_TAG

class WatcherFragment : Fragment(), WatcherView {
    private var _binding: FragmentWatcherBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val WATCHER_FRAGMENT_TAG = "watcher_fragment_tag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWatcherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.let { activity ->
            val presenter = WatcherPresenter(activity.applicationContext, this)
            presenter.downloadData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { activity ->
            val sharedPreferences =
                activity.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

            val isFirstStart = sharedPreferences.getBoolean(FIRST_APPLICATION_START_TAG, true)
            if (isFirstStart) {
                sharedPreferences.edit().putBoolean(FIRST_APPLICATION_START_TAG, false).apply()
            }

            val isWatcherModeTurnedOn =
                sharedPreferences.getBoolean(IS_WATCHER_MODE_TURNED_ON, false)
            if (!isWatcherModeTurnedOn) {
                scheduleWork()
                sharedPreferences.edit().putBoolean(IS_WATCHER_MODE_TURNED_ON, true).apply()
                sharedPreferences.edit().putBoolean(IS_TARGET_MODE_TURNED_ON, false).apply()
            }

            binding.buttonCancelWatching.setOnClickListener {
                activity.let { activity ->
                    WorkManager.getInstance(activity.applicationContext).cancelAllWork()
                    sharedPreferences.edit().putBoolean(IS_WATCHER_MODE_TURNED_ON, true).apply()
                    sharedPreferences.edit().putBoolean(FIRST_APPLICATION_START_TAG, true).apply()
                    cancelAllWorks()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showInfo(container: BatteryInfoContainer?) {
        binding.apply {
            container?.let {
                textViewBatteryLevelContent.text = "${container.batteryCondition}"
                textViewChargingState.text = getChargingState(container.isCharging)
                textViewTimeContent.text = getTime(container.lastChargingTime)
            }
        }
    }

    private fun cancelAllWorks() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainerView, ChoiceFragment())
            ?.addToBackStack(null)
            ?.commit()
    }

    private fun getTime(lastChargingTime: Long?): String {
        val date = Date(lastChargingTime ?: 0)
        val formattedTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(date)
        val formattedDate = DateFormat.getDateInstance().format(date)
        return "$formattedTime  $formattedDate"
    }

    private fun getChargingState(isCharging: Boolean?): String {
        return if (isCharging != null && isCharging) {
            getString(R.string.charging_state_charging)
        } else {
            getString(R.string.charging_state_not_charging)
        }
    }

    private fun scheduleWork() {
        activity?.let { activity ->
            val workRequest = OneTimeWorkRequestBuilder<WatcherWorker>().addTag(UNIQUE_WORK_TAG)
                .build()

            WorkManager.getInstance(activity.applicationContext).enqueueUniqueWork(
                UNIQUE_WORK_TAG,
                ExistingWorkPolicy.REPLACE,
                workRequest)
        }
    }
}