package com.finalproject.group14.noteapp.utility

import android.app.IntentService
import android.content.Context
import android.content.Intent

import com.finalproject.group14.noteapp.main.MainFragment

import java.util.ArrayList
import java.util.UUID

class DeleteNotificationService : IntentService("DeleteNotificationService") {

    private var storeRetrieveData: StoreRetrieveData? = null
    private var mToDoItems: ArrayList<ToDoItem>? = null
    private var mItem: ToDoItem? = null

    override fun onHandleIntent(intent: Intent?) {
        storeRetrieveData = StoreRetrieveData(this, MainFragment.FILENAME)
        val todoID = intent!!.getSerializableExtra(TodoNotificationService.TODOUUID) as UUID

        mToDoItems = loadData()
        if (mToDoItems != null) {
            for (item in mToDoItems!!) {
                if (item.identifier == todoID) {
                    mItem = item
                    break
                }
            }

            if (mItem != null) {
                mToDoItems!!.remove(mItem!!)
                dataChanged()
                saveData()
            }

        }

    }

    private fun dataChanged() {
        val sharedPreferences = getSharedPreferences(MainFragment.SHARED_PREF_DATA_SET_CHANGED, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(MainFragment.CHANGE_OCCURED, true)
        editor.apply()
    }

    private fun saveData() {
        try {
            mToDoItems?.let { storeRetrieveData!!.saveToFile(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        saveData()
    }

    private fun loadData(): ArrayList<ToDoItem>? {
        try {
            return storeRetrieveData!!.loadFromFile()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null

    }
}
