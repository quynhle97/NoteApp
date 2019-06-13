package com.finalproject.group14.noteapp.reminder

import android.os.Bundle

import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.appDefault.AppDefaultActivity

class ReminderActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.reminder_layout
    }

    override fun createInitialFragment(): ReminderFragment {
        return ReminderFragment.newInstance()
    }


}
