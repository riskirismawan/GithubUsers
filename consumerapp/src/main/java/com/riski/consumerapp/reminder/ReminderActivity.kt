package com.riski.consumerapp.reminder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.riski.consumerapp.R
import com.riski.consumerapp.databinding.ActivityReminderBinding

class ReminderActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityReminderBinding
    private lateinit var reminderReceiver: ReminderReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.reminder)

        reminderReceiver = ReminderReceiver()

        binding.btnCancelReminder.setOnClickListener(this)
        binding.btnSetReminder.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_cancel_reminder -> {
                reminderReceiver.cancelAlarm(this, ReminderReceiver.TYPE_REPEATING)
            }
            R.id.btn_set_reminder -> {
                val time = "09:00"
                val message = "Open Application"
                reminderReceiver.setRepeatingAlarm(this, ReminderReceiver.TYPE_REPEATING, time, message)
            }
        }
    }
}