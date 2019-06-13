package com.finalproject.group14.noteapp.reminder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

import com.finalproject.group14.noteapp.analytics.AnalyticsApplication
import com.finalproject.group14.noteapp.appDefault.AppDefaultFragment
import com.finalproject.group14.noteapp.main.MainActivity
import com.finalproject.group14.noteapp.main.MainFragment
import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.utility.StoreRetrieveData
import com.finalproject.group14.noteapp.utility.ToDoItem
import com.finalproject.group14.noteapp.utility.TodoNotificationService

import org.json.JSONException

import java.io.IOException
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.UUID

import fr.ganfra.materialspinner.MaterialSpinner

import android.content.Context.MODE_PRIVATE

class ReminderFragment : AppDefaultFragment() {
    private var mtoDoTextTextView: TextView? = null
    private var mRemoveToDoButton: Button? = null
    private var mSnoozeSpinner: MaterialSpinner? = null
    private var snoozeOptionsArray: Array<String>? = null
    private var storeRetrieveData: StoreRetrieveData? = null
    private var mToDoItems: ArrayList<ToDoItem>? = null
    private var mItem: ToDoItem? = null
    private var mSnoozeTextView: TextView? = null
    internal var theme: String? = null
    internal lateinit var app: AnalyticsApplication

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity.application as AnalyticsApplication
        app.send(this)

        theme = activity.getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
            activity.setTheme(R.style.CustomStyle_LightTheme)
        } else {
            activity.setTheme(R.style.CustomStyle_DarkTheme)
        }
        storeRetrieveData = StoreRetrieveData(context, MainFragment.FILENAME)
        mToDoItems = MainFragment.getLocallyStoredData(storeRetrieveData!!)

        (activity as AppCompatActivity).setSupportActionBar(view!!.findViewById(R.id.toolbar) as Toolbar)


        val i = activity.intent
        val id = i.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID
        mItem = null
        for (toDoItem in mToDoItems!!) {
            if (toDoItem.identifier == id) {
                mItem = toDoItem
                break
            }
        }

        snoozeOptionsArray = resources.getStringArray(R.array.snooze_options)

        mRemoveToDoButton = view.findViewById(R.id.toDoReminderRemoveButton) as Button
        mtoDoTextTextView = view.findViewById(R.id.toDoReminderTextViewBody) as TextView
        mSnoozeTextView = view.findViewById(R.id.reminderViewSnoozeTextView) as TextView
        mSnoozeSpinner = view.findViewById(R.id.todoReminderSnoozeSpinner) as MaterialSpinner

        //        mtoDoTextTextView.setBackgroundColor(item.getTodoColor());
        mtoDoTextTextView!!.text = mItem!!.toDoText

        if (theme == MainFragment.LIGHTTHEME) {
            mSnoozeTextView!!.setTextColor(resources.getColor(R.color.secondary_text))
        } else {
            mSnoozeTextView!!.setTextColor(Color.WHITE)
            mSnoozeTextView!!.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_snooze_white_24dp, 0, 0, 0
            )
        }

        mRemoveToDoButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                app.send(this, "Action", "Todo Removed from Reminder Activity")
                mToDoItems!!.remove(mItem as Nothing)
                changeOccurred()
                saveData()
                closeApp()
                //                finish();
            }
        })

        val adapter = ArrayAdapter(context, R.layout.spinner_text_view, snoozeOptionsArray!!)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        mSnoozeSpinner!!.adapter = adapter
        //        mSnoozeSpinner.setSelection(0);
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_reminder
    }

    private fun closeApp() {
        val i = Intent(context, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val sharedPreferences = activity.getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(EXIT, true)
        editor.apply()
        startActivity(i)

    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.menu_reminder, menu)
        return true
    }

    private fun changeOccurred() {
        val sharedPreferences = activity.getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true)
        //        editor.commit();
        editor.apply()
    }

    private fun addTimeToDate(mins: Int): Date {
        app.send(this, "Action", "Snoozed", "For $mins minutes")
        val date = Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, mins)
        return calendar.time
    }

    private fun valueFromSpinner(): Int {
        when (mSnoozeSpinner!!.selectedItemPosition) {
            0 -> return 10
            1 -> return 30
            2 -> return 60
            else -> return 0
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.toDoReminderDoneMenuItem -> {
                val date = addTimeToDate(valueFromSpinner())
                mItem!!.toDoDate = date
                mItem!!.setHasReminder(true)
                changeOccurred()
                saveData()
                closeApp()
                //foo
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun saveData() {
        try {
            mToDoItems?.let { storeRetrieveData!!.saveToFile(it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {
        val EXIT = "com.finalproject.exit"


        fun newInstance(): ReminderFragment {
            return ReminderFragment()
        }
    }
}
