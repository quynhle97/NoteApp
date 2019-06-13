package com.finalproject.group14.noteapp.addtodo

import android.animation.Animator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.finalproject.group14.noteapp.analytics.AnalyticsApplication
import com.finalproject.group14.noteapp.main.MainFragment
import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.utility.ToDoItem
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import com.finalproject.group14.noteapp.appDefault.AppDefaultFragment

class AddToDoFragment : AppDefaultFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val mLastEdited: Date? = null

    private var mToDoTextBodyEditText: EditText? = null
    private var mToDoTextBodyDescription: EditText? = null

    private var mToDoDateSwitch: SwitchCompat? = null
    //    private TextView mLastSeenTextView;
    private var mUserDateSpinnerContainingLinearLayout: LinearLayout? = null
    private var mReminderTextView: TextView? = null

    private var CombinationText: String? = null

    private var mDateEditText: EditText? = null
    private var mTimeEditText: EditText? = null
    private val mDefaultTimeOptions12H: Array<String>? = null
    private val mDefaultTimeOptions24H: Array<String>? = null

    private val mChooseDateButton: Button? = null
    private val mChooseTimeButton: Button? = null
    private var mCopyClipboard: Button? = null

    private var mUserToDoItem: ToDoItem? = null
    private var mToDoSendFloatingActionButton: FloatingActionButton? = null

    private var mUserEnteredText: String? = null
    private var mUserEnteredDescription: String? = null
    private var mUserHasReminder: Boolean = false
    private var mToolbar: Toolbar? = null
    private var mUserReminderDate: Date? = null
    private var mUserColor: Int = 0
    private val setDateButtonClickedOnce = false
    private val setTimeButtonClickedOnce = false
    private var mContainerLayout: LinearLayout? = null
    private var theme: String? = null
    internal lateinit var app: AnalyticsApplication

    private val themeSet: String
        get() = activity.getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity.application as AnalyticsApplication

        val reminderIconImageButton: ImageButton
        val reminderRemindMeTextView: TextView


        theme = activity.getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
            activity.setTheme(R.style.CustomStyle_LightTheme)
            Log.d("OskarSchindler", "Light Theme")
        } else {
            activity.setTheme(R.style.CustomStyle_DarkTheme)
        }


        val cross = resources.getDrawable(R.drawable.ic_clear_white_24dp)
        cross?.setColorFilter(resources.getColor(R.color.icons), PorterDuff.Mode.SRC_ATOP)

        mToolbar = view!!.findViewById(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(mToolbar)

        if ((activity as AppCompatActivity).supportActionBar != null) {
            (activity as AppCompatActivity).supportActionBar!!.elevation = 0f
            (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            (activity as AppCompatActivity).supportActionBar!!.setHomeAsUpIndicator(cross)
        }

        mUserToDoItem = activity.intent.getSerializableExtra(MainFragment.TODOITEM) as ToDoItem

        mUserEnteredText = mUserToDoItem!!.toDoText
        mUserEnteredDescription = mUserToDoItem!!.getmToDoDescription()
        mUserHasReminder = mUserToDoItem!!.hasReminder()
        mUserReminderDate = mUserToDoItem!!.toDoDate
        mUserColor = mUserToDoItem!!.todoColor

        reminderIconImageButton = view.findViewById(R.id.userToDoReminderIconImageButton) as ImageButton
        reminderRemindMeTextView = view.findViewById(R.id.userToDoRemindMeTextView) as TextView
        if (theme == MainFragment.DARKTHEME) {
            reminderIconImageButton.setImageDrawable(resources.getDrawable(R.drawable.ic_alarm_add_white_24dp))
            reminderRemindMeTextView.setTextColor(Color.WHITE)
        }

        //Button for Copy to Clipboard
        mCopyClipboard = view.findViewById(R.id.copyclipboard) as Button

        mContainerLayout = view.findViewById(R.id.todoReminderAndDateContainerLayout) as LinearLayout
        mUserDateSpinnerContainingLinearLayout = view.findViewById(R.id.toDoEnterDateLinearLayout) as LinearLayout
        mToDoTextBodyEditText = view.findViewById(R.id.userToDoEditText) as EditText
        mToDoTextBodyDescription = view.findViewById(R.id.userToDoDescription) as EditText
        mToDoDateSwitch = view.findViewById(R.id.toDoHasDateSwitchCompat) as SwitchCompat
        //        mLastSeenTextView = (TextView)findViewById(R.id.toDoLastEditedTextView);
        mToDoSendFloatingActionButton = view.findViewById(R.id.makeToDoFloatingActionButton) as FloatingActionButton
        mReminderTextView = view.findViewById(R.id.newToDoDateTimeReminderTextView) as TextView


        //OnClickListener for CopyClipboard Button
        mCopyClipboard!!.setOnClickListener {
            val toDoTextContainer = mToDoTextBodyEditText!!.text.toString()
            val toDoTextBodyDescriptionContainer = mToDoTextBodyDescription!!.text.toString()
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            CombinationText = "Title : $toDoTextContainer\nDescription : $toDoTextBodyDescriptionContainer\n -Copied From MinimalToDo"
            val clip = ClipData.newPlainText("text", CombinationText)
            clipboard.primaryClip = clip
            Toast.makeText(context, "Copied To Clipboard!", Toast.LENGTH_SHORT).show()
        }

        mContainerLayout!!.setOnClickListener {
            hideKeyboard(mToDoTextBodyEditText)
            hideKeyboard(mToDoTextBodyDescription)
        }


        if (mUserHasReminder && mUserReminderDate != null) {
            //            mUserDateSpinnerContainingLinearLayout.setVisibility(View.VISIBLE);
            setReminderTextView()
            setEnterDateLayoutVisibleWithAnimations(true)
        }
        if (mUserReminderDate == null) {
            mToDoDateSwitch!!.isChecked = false
            mReminderTextView!!.visibility = View.INVISIBLE
        }

        mToDoTextBodyEditText!!.requestFocus()
        mToDoTextBodyEditText!!.setText(mUserEnteredText)
        mToDoTextBodyDescription!!.setText(mUserEnteredDescription)
        val imm = this.activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        mToDoTextBodyEditText!!.setSelection(mToDoTextBodyEditText!!.length())


        mToDoTextBodyEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mUserEnteredText = s.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mToDoTextBodyDescription!!.setText(mUserEnteredDescription)
        mToDoTextBodyDescription!!.setSelection(mToDoTextBodyDescription!!.length())
        mToDoTextBodyDescription!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mUserEnteredDescription = s.toString()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        setEnterDateLayoutVisible(mToDoDateSwitch!!.isChecked)

        mToDoDateSwitch!!.isChecked = mUserHasReminder && mUserReminderDate != null
        mToDoDateSwitch!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    app.send(this, "Action", "Reminder Set")
                } else {
                    app.send(this, "Action", "Reminder Removed")

                }

                if (!isChecked) {
                    mUserReminderDate = null
                }
                mUserHasReminder = isChecked
                setDateAndTimeEditText()
                setEnterDateLayoutVisibleWithAnimations(isChecked)
                hideKeyboard(mToDoTextBodyEditText)
                hideKeyboard(mToDoTextBodyDescription)
            }
        })


        mToDoSendFloatingActionButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (mToDoTextBodyEditText!!.length() <= 0) {
                    mToDoTextBodyEditText!!.error = getString(R.string.todo_error)
                } else if (mUserReminderDate != null && mUserReminderDate!!.before(Date())) {
                    app.send(this, "Action", "Date in the Past")
                    makeResult(RESULT_CANCELED)
                } else {
                    app.send(this, "Action", "Make Todo")
                    makeResult(RESULT_OK)
                    activity.finish()
                }
                hideKeyboard(mToDoTextBodyEditText)
                hideKeyboard(mToDoTextBodyDescription)
            }
        })


        mDateEditText = view.findViewById(R.id.newTodoDateEditText) as EditText
        mTimeEditText = view.findViewById(R.id.newTodoTimeEditText) as EditText

        mDateEditText!!.setOnClickListener {
            val date: Date?
            hideKeyboard(mToDoTextBodyEditText)
            if (mUserToDoItem!!.toDoDate != null) {
                //                    date = mUserToDoItem.getToDoDate();
                date = mUserReminderDate
            } else {
                date = Date()
            }
            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog.newInstance(this@AddToDoFragment, year, month, day)
            if (theme == MainFragment.DARKTHEME) {
                datePickerDialog.isThemeDark = true
            }
            datePickerDialog.show(activity.fragmentManager, "DateFragment")
        }


        mTimeEditText!!.setOnClickListener {
            val date: Date?
            hideKeyboard(mToDoTextBodyEditText)
            if (mUserToDoItem!!.toDoDate != null) {
                //                    date = mUserToDoItem.getToDoDate();
                date = mUserReminderDate
            } else {
                date = Date()
            }
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog.newInstance(this@AddToDoFragment, hour, minute, DateFormat.is24HourFormat(context))
            if (theme == MainFragment.DARKTHEME) {
                timePickerDialog.isThemeDark = true
            }
            timePickerDialog.show(activity.fragmentManager, "TimeFragment")
        }

        setDateAndTimeEditText()

    }

    private fun setDateAndTimeEditText() {

        if (mUserToDoItem!!.hasReminder() && mUserReminderDate != null) {
            val userDate = formatDate("d MMM, yyyy", mUserReminderDate)
            val formatToUse: String
            if (DateFormat.is24HourFormat(context)) {
                formatToUse = "k:mm"
            } else {
                formatToUse = "h:mm a"

            }
            val userTime = formatDate(formatToUse, mUserReminderDate)
            mTimeEditText!!.setText(userTime)
            mDateEditText!!.setText(userDate)

        } else {
            mDateEditText!!.setText(getString(R.string.date_reminder_default))
            //            mUserReminderDate = new Date();
            val time24 = DateFormat.is24HourFormat(context)
            val cal = Calendar.getInstance()
            if (time24) {
                cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1)
            } else {
                cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1)
            }
            cal.set(Calendar.MINUTE, 0)
            mUserReminderDate = cal.time
            Log.d("OskarSchindler", "Imagined Date: " + mUserReminderDate!!)
            val timeString: String
            if (time24) {
                timeString = formatDate("k:mm", mUserReminderDate)
            } else {
                timeString = formatDate("h:mm a", mUserReminderDate)
            }
            mTimeEditText!!.setText(timeString)
        }
    }

    fun hideKeyboard(et: EditText?) {

        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et!!.windowToken, 0)
    }


    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val hour: Int
        val minute: Int

        val reminderCalendar = Calendar.getInstance()
        reminderCalendar.set(year, month, day)

        if (reminderCalendar.before(calendar)) {
            return
        }

        if (mUserReminderDate != null) {
            calendar.time = mUserReminderDate
        }

        if (DateFormat.is24HourFormat(context)) {
            hour = calendar.get(Calendar.HOUR_OF_DAY)
        } else {

            hour = calendar.get(Calendar.HOUR)
        }
        minute = calendar.get(Calendar.MINUTE)

        calendar.set(year, month, day, hour, minute)
        mUserReminderDate = calendar.time
        setReminderTextView()
        setDateEditText()
    }

    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        if (mUserReminderDate != null) {
            calendar.time = mUserReminderDate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("OskarSchindler", "Time set: $hour")
        calendar.set(year, month, day, hour, minute, 0)
        mUserReminderDate = calendar.time

        setReminderTextView()
        setTimeEditText()
    }

    fun setDateEditText() {
        val dateFormat = "d MMM, yyyy"
        mDateEditText!!.setText(formatDate(dateFormat, mUserReminderDate))
    }

    fun setTimeEditText() {
        val dateFormat: String
        if (DateFormat.is24HourFormat(context)) {
            dateFormat = "k:mm"
        } else {
            dateFormat = "h:mm a"

        }
        mTimeEditText!!.setText(formatDate(dateFormat, mUserReminderDate))
    }

    fun setReminderTextView() {
        if (mUserReminderDate != null) {
            mReminderTextView!!.visibility = View.VISIBLE
            if (mUserReminderDate!!.before(Date())) {
                Log.d("OskarSchindler", "DATE is " + mUserReminderDate!!)
                mReminderTextView!!.text = getString(R.string.date_error_check_again)
                mReminderTextView!!.setTextColor(Color.RED)
                return
            }
            val date = mUserReminderDate
            val dateString = formatDate("d MMM, yyyy", date)
            val timeString: String
            var amPmString = ""

            if (DateFormat.is24HourFormat(context)) {
                timeString = formatDate("k:mm", date)
            } else {
                timeString = formatDate("h:mm", date)
                amPmString = formatDate("a", date)
            }
            val finalString = String.format(resources.getString(R.string.remind_date_and_time), dateString, timeString, amPmString)
            mReminderTextView!!.setTextColor(resources.getColor(R.color.secondary_text))
            mReminderTextView!!.setText(finalString)
        } else {
            mReminderTextView!!.visibility = View.INVISIBLE

        }
    }

    fun makeResult(result: Int) {
        Log.d(TAG, "makeResult - ok : in")
        val i = Intent()
        if (mUserEnteredText!!.length > 0) {

            val capitalizedString = Character.toUpperCase(mUserEnteredText!![0]) + mUserEnteredText!!.substring(1)
            mUserToDoItem!!.toDoText = capitalizedString
            Log.d(TAG, "Description: " + mUserEnteredDescription!!)
            mUserToDoItem!!.setmToDoDescription(mUserEnteredDescription!!)
        } else {
            mUserToDoItem!!.toDoText = mUserEnteredText
            Log.d(TAG, "Description: " + mUserEnteredDescription!!)
            mUserToDoItem!!.setmToDoDescription(mUserEnteredDescription!!)
        }
        //        mUserToDoItem.setLastEdited(mLastEdited);
        if (mUserReminderDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = mUserReminderDate
            calendar.set(Calendar.SECOND, 0)
            mUserReminderDate = calendar.time
        }
        mUserToDoItem!!.setHasReminder(mUserHasReminder)
        mUserToDoItem!!.toDoDate = mUserReminderDate
        mUserToDoItem!!.todoColor = mUserColor
        i.putExtra(MainFragment.TODOITEM, mUserToDoItem)
        activity.setResult(result, i)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(activity) != null) {
                    app.send(this, "Action", "Discard Todo")
                    makeResult(RESULT_CANCELED)
                    NavUtils.navigateUpFromSameTask(activity)
                }
                hideKeyboard(mToDoTextBodyEditText)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onTimeSet(radialPickerLayout: RadialPickerLayout, hour: Int, minute: Int) {
        setTime(hour, minute)
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog, year: Int, month: Int, day: Int) {
        setDate(year, month, day)
    }

    fun setEnterDateLayoutVisible(checked: Boolean) {
        if (checked) {
            mUserDateSpinnerContainingLinearLayout!!.visibility = View.VISIBLE
        } else {
            mUserDateSpinnerContainingLinearLayout!!.visibility = View.INVISIBLE
        }
    }

    fun setEnterDateLayoutVisibleWithAnimations(checked: Boolean) {
        if (checked) {
            setReminderTextView()
            mUserDateSpinnerContainingLinearLayout!!.animate().alpha(1.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            mUserDateSpinnerContainingLinearLayout!!.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator) {}

                        override fun onAnimationCancel(animation: Animator) {}

                        override fun onAnimationRepeat(animation: Animator) {}
                    }
            )
        } else {
            mUserDateSpinnerContainingLinearLayout!!.animate().alpha(0.0f).setDuration(500).setListener(
                    object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {

                        }

                        override fun onAnimationEnd(animation: Animator) {
                            mUserDateSpinnerContainingLinearLayout!!.visibility = View.INVISIBLE
                        }

                        override fun onAnimationCancel(animation: Animator) {

                        }

                        override fun onAnimationRepeat(animation: Animator) {

                        }
                    }
            )
        }

    }


    override fun layoutRes(): Int {
        return R.layout.fragment_add_to_do
    }

    companion object {
        private val TAG = "AddToDoFragment"
        val DATE_FORMAT = "MMM d, yyyy"
        val DATE_FORMAT_MONTH_DAY = "MMM d"
        val DATE_FORMAT_TIME = "H:m"

        fun formatDate(formatString: String, dateToFormat: Date?): String {
            val simpleDateFormat = SimpleDateFormat(formatString)
            return simpleDateFormat.format(dateToFormat)
        }

        fun newInstance(): AddToDoFragment {
            return AddToDoFragment()
        }
    }
}
