package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import java.util.Locale
import java.util.concurrent.TimeUnit

class CountDownActivity : AppCompatActivity() {

    private lateinit var notificationWorkRequest: OneTimeWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)
        supportActionBar?.title = "Count Down"

        val habit = intent.getParcelableExtra<Habit>(HABIT) as Habit

        findViewById<TextView>(R.id.tv_count_down_title).text = habit.title

        val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

        // Set initial time and observe current time. Update button state when countdown is finished
        val initialTime = habit.minutesFocus * 60000L
        viewModel.setInitialTime(initialTime)
        viewModel.currentTimeString.observe(this) {
            findViewById<TextView>(R.id.tv_count_down).text = it
        }
        viewModel.eventCountDownFinish.observe(this) { updateButtonState(it) }

        // Start and cancel One Time Request WorkManager to notify when time is up.
        val workManager = WorkManager.getInstance(applicationContext)
        val data = workDataOf(
            HABIT_ID to habit.id,
            HABIT_TITLE to habit.title
        )

        notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(initialTime, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            workManager.enqueue(notificationWorkRequest)
            viewModel.startTimer()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            workManager.cancelWorkById(notificationWorkRequest.id)
            viewModel.resetTimer()
//            viewModel.setInitialTime(initialTime)
        }
    }


    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}