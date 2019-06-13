package com.finalproject.group14.noteapp.utility

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.Date
import java.util.UUID

class ToDoItem : Serializable {
    var toDoText: String? = null
    private var mHasReminder: Boolean = false
    private var mToDoDescription: String? = null
    var todoColor: Int = 0
    var toDoDate: Date? = null
    var identifier: UUID? = null
        private set


    @JvmOverloads
    constructor(todoBody: String = "Clean my room", tododescription: String = "Sweep and Mop my Room", hasReminder: Boolean = true, toDoDate: Date? = Date()) {
        toDoText = todoBody
        mHasReminder = hasReminder
        this.toDoDate = toDoDate
        mToDoDescription = tododescription
        todoColor = 1677725
        identifier = UUID.randomUUID()
    }

    @Throws(JSONException::class)
    constructor(jsonObject: JSONObject) {
        toDoText = jsonObject.getString(TODOTEXT)
        mToDoDescription = jsonObject.getString(TODODESCRIPTION)
        mHasReminder = jsonObject.getBoolean(TODOREMINDER)
        todoColor = jsonObject.getInt(TODOCOLOR)

        identifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER))

        if (jsonObject.has(TODODATE)) {
            toDoDate = Date(jsonObject.getLong(TODODATE))
        }
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(TODOTEXT, toDoText)
        jsonObject.put(TODOREMINDER, mHasReminder)
        jsonObject.put(TODODESCRIPTION, mToDoDescription)
        if (toDoDate != null) {
            jsonObject.put(TODODATE, toDoDate!!.time)
        }
        jsonObject.put(TODOCOLOR, todoColor)
        jsonObject.put(TODOIDENTIFIER, identifier!!.toString())

        return jsonObject
    }

    fun getmToDoDescription(): String? {
        return mToDoDescription
    }

    fun setmToDoDescription(mToDoDescription: String) {
        this.mToDoDescription = mToDoDescription
    }

    fun hasReminder(): Boolean {
        return mHasReminder
    }

    fun setHasReminder(mHasReminder: Boolean) {
        this.mHasReminder = mHasReminder
    }

    companion object {
        //add description
        private val TODODESCRIPTION = "tododescription"
        private val TODOTEXT = "todotext"
        private val TODOREMINDER = "todoreminder"
        private val TODOCOLOR = "todocolor"
        private val TODODATE = "tododate"
        private val TODOIDENTIFIER = "todoidentifier"
    }
}

