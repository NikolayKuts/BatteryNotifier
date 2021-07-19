package com.example.batterynotifier.presintation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.work.*
import com.example.batterynotifier.databinding.FragmentTargetBinding
import com.example.batterynotifier.domain.TargetWorker
import com.example.batterynotifier.presintation.FIRST_APPLICATION_START_TAG
import com.example.batterynotifier.presintation.PREFERENCE_FILE_KEY
import com.example.batterynotifier.presintation.IS_TARGET_MODE_TURNED_ON
import com.example.batterynotifier.presintation.IS_WATCHER_MODE_TURNED_ON
import java.util.concurrent.TimeUnit

class TargetFragment : Fragment() {
    private var _binding: FragmentTargetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTargetBinding.inflate(inflater, container, false)
        return binding.root
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

            sharedPreferences.apply {
                val isTargetModeTurnedOn = getBoolean(IS_TARGET_MODE_TURNED_ON, false)
                if (!isTargetModeTurnedOn) {
                    edit().putBoolean(IS_TARGET_MODE_TURNED_ON, true).apply()
                    edit().putBoolean(IS_WATCHER_MODE_TURNED_ON, false).apply()

                    val workRequest = PeriodicWorkRequestBuilder<TargetWorker>(
                        PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS
                    )
                        .build()

                    WorkManager.getInstance(activity.applicationContext).enqueue(workRequest)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}