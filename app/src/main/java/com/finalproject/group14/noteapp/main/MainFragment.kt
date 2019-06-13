package com.finalproject.group14.noteapp.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.finalproject.group14.noteapp.about.AboutActivity
import com.finalproject.group14.noteapp.addtodo.AddToDoActivity
import com.finalproject.group14.noteapp.addtodo.AddToDoFragment
import com.finalproject.group14.noteapp.analytics.AnalyticsApplication
import com.finalproject.group14.noteapp.appDefault.AppDefaultFragment
import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.reminder.ReminderFragment
import com.finalproject.group14.noteapp.settings.SettingsActivity
import com.finalproject.group14.noteapp.utility.ItemTouchHelperClass
import com.finalproject.group14.noteapp.utility.RecyclerViewEmptySupport
import com.finalproject.group14.noteapp.utility.StoreRetrieveData
import com.finalproject.group14.noteapp.utility.ToDoItem
import com.finalproject.group14.noteapp.utility.TodoNotificationService

import org.json.JSONException

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Date

import android.app.Activity.RESULT_CANCELED
import android.content.Context.ALARM_SERVICE
import android.content.Context.MODE_PRIVATE

class MainFragment : AppDefaultFragment() {
    private var mRecyclerView: RecyclerViewEmptySupport? = null
    private var mAddToDoItemFAB: FloatingActionButton? = null
    private var mToDoItemsArrayList: ArrayList<ToDoItem>? = null
    private var mCoordLayout: CoordinatorLayout? = null
    private var adapter: BasicListAdapter? = null
    private var mJustDeletedToDoItem: ToDoItem? = null
    private var mIndexOfDeletedToDoItem: Int = 0
    private var storeRetrieveData: StoreRetrieveData? = null
    lateinit var itemTouchHelper: ItemTouchHelper
    private var customRecyclerScrollViewListener: CustomRecyclerScrollViewListener? = null
    private var mTheme = -1
    private var theme: String? = "name_of_the_theme"
    private var app: AnalyticsApplication? = null
    private val testStrings = arrayOf("Clean my room", "Water the plants", "Get car washed", "Get my dry cleaning")

    private val alarmManager: AlarmManager
        get() = activity.getSystemService(ALARM_SERVICE) as AlarmManager


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity.application as AnalyticsApplication

        theme = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)

        if (theme == LIGHTTHEME) {
            mTheme = R.style.CustomStyle_LightTheme
        } else {
            mTheme = R.style.CustomStyle_DarkTheme
        }
        this.activity.setTheme(mTheme)

        super.onCreate(savedInstanceState)


        val sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(CHANGE_OCCURED, false)
        editor.apply()

        storeRetrieveData = StoreRetrieveData(context, FILENAME)
        mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData!!)
        adapter = BasicListAdapter(mToDoItemsArrayList!!)
        setAlarms()

        mCoordLayout = view!!.findViewById(R.id.myCoordinatorLayout) as CoordinatorLayout
        mAddToDoItemFAB = view.findViewById(R.id.addToDoItemFAB) as FloatingActionButton

        mAddToDoItemFAB!!.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                app!!.send(this, "Action", "FAB pressed")
                val newTodo = Intent(context, AddToDoActivity::class.java)
                val item = ToDoItem("", "", false, null)
                val color = ColorGenerator.MATERIAL.randomColor
                item.todoColor = color

                newTodo.putExtra(TODOITEM, item)
                startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM)
            }
        })

        mRecyclerView = view.findViewById(R.id.toDoRecyclerView) as RecyclerViewEmptySupport
        if (theme == LIGHTTHEME) {
            mRecyclerView!!.setBackgroundColor(resources.getColor(R.color.primary_lightest))
        }
        mRecyclerView!!.setEmptyView(view.findViewById(R.id.toDoEmptyView))
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.layoutManager = LinearLayoutManager(context)


        customRecyclerScrollViewListener = object : CustomRecyclerScrollViewListener() {
            override fun show() {

                mAddToDoItemFAB!!.animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f)).start()
            }

            override fun hide() {

                val lp = mAddToDoItemFAB!!.layoutParams as CoordinatorLayout.LayoutParams
                val fabMargin = lp.bottomMargin
                mAddToDoItemFAB!!.animate().translationY((mAddToDoItemFAB!!.height + fabMargin).toFloat()).setInterpolator(AccelerateInterpolator(2.0f)).start()
            }
        }
        mRecyclerView!!.addOnScrollListener(customRecyclerScrollViewListener)


        val callback = ItemTouchHelperClass(adapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(mRecyclerView)

        mRecyclerView!!.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        app!!.send(this)

        val sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(ReminderFragment.EXIT, false)) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(ReminderFragment.EXIT, false)
            editor.apply()
            activity.finish()
        }

        if (activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)) {
            val editor = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit()
            editor.putBoolean(RECREATE_ACTIVITY, false)
            editor.apply()
            activity.recreate()
        }


    }

    override fun onStart() {
        app = activity.application as AnalyticsApplication
        super.onStart()
        val sharedPreferences = activity.getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(CHANGE_OCCURED, false)) {

            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData!!)
            adapter = BasicListAdapter(mToDoItemsArrayList!!)
            mRecyclerView!!.adapter = adapter
            setAlarms()

            val editor = sharedPreferences.edit()
            editor.putBoolean(CHANGE_OCCURED, false)
            //            editor.commit();
            editor.apply()


        }
    }

    private fun setAlarms() {
        if (mToDoItemsArrayList != null) {
            for (item in mToDoItemsArrayList!!) {
                if (item.hasReminder() && item.toDoDate != null) {
                    if (item.toDoDate!!.before(Date())) {
                        item.toDoDate = null
                        continue
                    }
                    val i = Intent(context, TodoNotificationService::class.java)
                    i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                    i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                    createAlarm(i, item.identifier!!.hashCode(), item.toDoDate!!.time)
                }
            }
        }
    }

    fun addThemeToSharedPreferences(theme: String) {
        val sharedPreferences = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(THEME_SAVED, theme)
        editor.apply()
    }


    fun onCreateOptionsMenu(menu: Menu): Boolean {
        activity.menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(context, AboutActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.preferences -> {
                val intent = Intent(context, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM) {
            val item = data!!.getSerializableExtra(TODOITEM) as ToDoItem
            if (item.toDoText!!.length <= 0) {
                return
            }
            var existed = false

            if (item.hasReminder() && item.toDoDate != null) {
                val i = Intent(context, TodoNotificationService::class.java)
                i.putExtra(TodoNotificationService.TODOTEXT, item.toDoText)
                i.putExtra(TodoNotificationService.TODOUUID, item.identifier)
                createAlarm(i, item.identifier!!.hashCode(), item.toDoDate!!.time)
            }

            for (i in mToDoItemsArrayList!!.indices) {
                if (item.identifier == mToDoItemsArrayList!![i].identifier) {
                    mToDoItemsArrayList!![i] = item
                    existed = true
                    adapter!!.notifyDataSetChanged()
                    break
                }
            }
            if (!existed) {
                addToDataStore(item)
            }


        }
    }

    private fun doesPendingIntentExist(i: Intent, requestCode: Int): Boolean {
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
        return pi != null
    }

    private fun createAlarm(i: Intent, requestCode: Int, timeInMillis: Long) {
        val am = alarmManager
        val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT)
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
        //        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }

    private fun deleteAlarm(i: Intent, requestCode: Int) {
        if (doesPendingIntentExist(i, requestCode)) {
            val pi = PendingIntent.getService(context, requestCode, i, PendingIntent.FLAG_NO_CREATE)
            pi.cancel()
            alarmManager.cancel(pi)
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode))
        }
    }

    private fun addToDataStore(item: ToDoItem) {
        mToDoItemsArrayList!!.add(item)
        adapter!!.notifyItemInserted(mToDoItemsArrayList!!.size - 1)

    }


    fun makeUpItems(items: ArrayList<ToDoItem>, len: Int) {
        for (testString in testStrings) {
            val item = ToDoItem(testString, testString, false, Date())
            items.add(item)
        }

    }

    inner class BasicListAdapter internal constructor(private val items: ArrayList<ToDoItem>) : RecyclerView.Adapter<BasicListAdapter.ViewHolder>(), ItemTouchHelperClass.ItemTouchHelperAdapter {

        override fun onItemMoved(fromPosition: Int, toPosition: Int) {
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(items, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(items, i, i - 1)
                }
            }
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onItemRemoved(position: Int) {
            app!!.send(this, "Action", "Swiped Todo Away")

            mJustDeletedToDoItem = items.removeAt(position)
            mIndexOfDeletedToDoItem = position
            val i = Intent(context, TodoNotificationService::class.java)
            deleteAlarm(i, mJustDeletedToDoItem!!.identifier!!.hashCode())
            notifyItemRemoved(position)

            //            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            val toShow = "Todo"
            Snackbar.make(mCoordLayout!!, "Deleted $toShow", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", object : View.OnClickListener {
                        override fun onClick(v: View) {

                            //Comment the line below if not using Google Analytics
                            app!!.send(this, "Action", "UNDO Pressed")
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem!!)
                            if (mJustDeletedToDoItem!!.toDoDate != null && mJustDeletedToDoItem!!.hasReminder()) {
                                val i = Intent(context, TodoNotificationService::class.java)
                                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem!!.toDoText)
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem!!.identifier)
                                createAlarm(i, mJustDeletedToDoItem!!.identifier!!.hashCode(), mJustDeletedToDoItem!!.toDoDate!!.time)
                            }
                            notifyItemInserted(mIndexOfDeletedToDoItem)
                        }
                    }).show()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicListAdapter.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_circle_try, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: BasicListAdapter.ViewHolder, position: Int) {
            val item = items[position]
            val sharedPreferences = activity.getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)

            val bgColor: Int

            val todoTextColor: Int
            if (sharedPreferences.getString(THEME_SAVED, LIGHTTHEME) == LIGHTTHEME) {
                bgColor = Color.WHITE
                todoTextColor = resources.getColor(R.color.secondary_text)
            } else {
                bgColor = Color.DKGRAY
                todoTextColor = Color.WHITE
            }
            holder.linearLayout.setBackgroundColor(bgColor)

            if (item.hasReminder() && item.toDoDate != null) {
                holder.mToDoTextview.maxLines = 1
                holder.mTimeTextView.visibility = View.VISIBLE
                //                holder.mToDoTextview.setVisibility(View.GONE);
            } else {
                holder.mTimeTextView.visibility = View.GONE
                holder.mToDoTextview.maxLines = 2
            }
            holder.mToDoTextview.text = item.toDoText
            holder.mToDoTextview.setTextColor(todoTextColor)
            val myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.toDoText!!.substring(0, 1), item.todoColor)

            holder.mColorImageView.setImageDrawable(myDrawable)
            if (item.toDoDate != null) {
                val timeToShow: String
                if (android.text.format.DateFormat.is24HourFormat(context)) {
                    timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_24_HOUR, item.toDoDate)
                } else {
                    timeToShow = AddToDoFragment.formatDate(MainFragment.DATE_TIME_FORMAT_12_HOUR, item.toDoDate)
                }
                holder.mTimeTextView.text = timeToShow
            }


        }

        override fun getItemCount(): Int {
            return items.size
        }


        inner class ViewHolder
        (internal var mView: View) : RecyclerView.ViewHolder(mView) {
            internal var linearLayout: LinearLayout
            internal var mToDoTextview: TextView
            //            TextView mColorTextView;
            internal var mColorImageView: ImageView
            internal var mTimeTextView: TextView

            init {
                mView.setOnClickListener {
                    val item = items[this@ViewHolder.adapterPosition]
                    val i = Intent(context, AddToDoActivity::class.java)
                    i.putExtra(TODOITEM, item)
                    startActivityForResult(i, REQUEST_ID_TODO_ITEM)
                }
                mToDoTextview = mView.findViewById(R.id.toDoListItemTextview) as TextView
                mTimeTextView = mView.findViewById(R.id.todoListItemTimeTextView) as TextView
                //                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView = mView.findViewById(R.id.toDoListItemColorImageView) as ImageView
                linearLayout = mView.findViewById(R.id.listItemLinearLayout) as LinearLayout
            }
        }
    }

    private fun saveDate() {
        try {
            mToDoItemsArrayList?.let { storeRetrieveData!!.saveToFile(it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            mToDoItemsArrayList?.let { storeRetrieveData!!.saveToFile(it) }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


    override fun onDestroy() {

        super.onDestroy()
        mRecyclerView!!.removeOnScrollListener(customRecyclerScrollViewListener)
    }

    override fun layoutRes(): Int {
        return R.layout.fragment_main
    }

    companion object {
        val TODOITEM = "com.final.group14.MainActivity"
        private val REQUEST_ID_TODO_ITEM = 100
        val DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a"
        val DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm"
        val FILENAME = "todoitems.json"
        val SHARED_PREF_DATA_SET_CHANGED = "com.finalproject.datasetchanged"
        val CHANGE_OCCURED = "com.finalproject.changeoccured"
        val THEME_PREFERENCES = "com.finalproject.themepref"
        val RECREATE_ACTIVITY = "com.finalproject.recreateactivity"
        val THEME_SAVED = "com.finalproject.savedtheme"
        val DARKTHEME = "com.finalproject.darktheme"
        val LIGHTTHEME = "com.finalproject.lighttheme"

        fun getLocallyStoredData(storeRetrieveData: StoreRetrieveData): ArrayList<ToDoItem> {
            var items: ArrayList<ToDoItem>? = null

            try {
                items = storeRetrieveData.loadFromFile()

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            if (items == null) {
                items = ArrayList()
            }
            return items

        }

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
