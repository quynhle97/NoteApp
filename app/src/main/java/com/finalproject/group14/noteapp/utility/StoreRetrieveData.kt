package com.finalproject.group14.noteapp.utility

import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONTokener

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.ArrayList

class StoreRetrieveData(private val mContext: Context, private val mFileName: String) {

    @Throws(JSONException::class, IOException::class)
    fun saveToFile(items: ArrayList<ToDoItem>) {
        val fileOutputStream: FileOutputStream
        val outputStreamWriter: OutputStreamWriter
        fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE)
        outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.write(toJSONArray(items).toString())
        outputStreamWriter.close()
        fileOutputStream.close()
    }

    @Throws(IOException::class, JSONException::class)
    fun loadFromFile(): ArrayList<ToDoItem> {
        val items = ArrayList<ToDoItem>()
        var bufferedReader: BufferedReader? = null
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = mContext.openFileInput(mFileName)
            val builder = StringBuilder()
            var line: String? = null
            bufferedReader = BufferedReader(InputStreamReader(fileInputStream!!))

            while (bufferedReader.readLine().also { line = it } != null) {
                builder.append(line)
            }

            val jsonArray = JSONTokener(builder.toString()).nextValue() as JSONArray
            for (i in 0 until jsonArray.length()) {
                val item = ToDoItem(jsonArray.getJSONObject(i))
                items.add(item)
            }


        } catch (fnfe: FileNotFoundException) {
            //do nothing about it
            //file won't exist first time app is run
        } finally {
            bufferedReader?.close()
            fileInputStream?.close()

        }
        return items
    }

    companion object {

        @Throws(JSONException::class)
        fun toJSONArray(items: ArrayList<ToDoItem>): JSONArray {
            val jsonArray = JSONArray()
            for (item in items) {
                val jsonObject = item.toJSON()
                jsonArray.put(jsonObject)
            }
            return jsonArray
        }
    }

}
