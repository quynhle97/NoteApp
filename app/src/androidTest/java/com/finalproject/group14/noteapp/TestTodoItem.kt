/**
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Miikka Andersson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.finalproject.group14.noteapp

import com.finalproject.group14.noteapp.utility.ToDoItem

import junit.framework.TestCase

import org.json.JSONException

import java.util.Date

/**
 * JUnit tests to verify functionality of ToDoItem class.
 */
class TestTodoItem : TestCase() {
    private val CURRENT_DATE = Date()
    private val TEXT_BODY = "This is some text"
    private val REMINDER_OFF = false
    private val REMINDER_ON = true

    /**
     * Check we can construct a ToDoItem object using the three parameter constructor
     */
    fun testThreeParameterConstructor() {
        val toDoItem = getToDoItem(REMINDER_OFF)
        Assert.assertEquals(TEXT_BODY, toDoItem.toDoText)
        Assert.assertEquals(REMINDER_OFF, toDoItem.hasReminder())
        Assert.assertEquals(CURRENT_DATE, toDoItem.toDoDate)
    }

    /**
     * Ensure we can marshall ToDoItem objects to Json
     */
    fun testObjectMarshallingToJson() {
        val toDoItem = getToDoItem(REMINDER_ON)

        try {
            val json = toDoItem.toJSON()
            Assert.assertEquals(TEXT_BODY, json.getString("todotext"))
            Assert.assertEquals(REMINDER_ON, json.getBoolean("todoreminder"))
            Assert.assertEquals(CURRENT_DATE.time.toString(), json.getString("tododate"))
        } catch (e: JSONException) {
            Assert.fail("Exception thrown during test execution: " + e.message)
        }

    }

    /**
     * Ensure we can create ToDoItem objects from Json data by using the json constructor
     */
    fun testObjectUnmarshallingFromJson() {
        val originalItem = getToDoItem(REMINDER_OFF)

        try {
            val json = originalItem.toJSON()
            val itemFromJson = ToDoItem(json)

            Assert.assertEquals(originalItem.toDoText, itemFromJson.toDoText)
            Assert.assertEquals(originalItem.toDoDate, itemFromJson.toDoDate)
            Assert.assertEquals(originalItem.hasReminder(), itemFromJson.hasReminder())
            Assert.assertEquals(originalItem.identifier, itemFromJson.identifier)

        } catch (e: JSONException) {
            Assert.fail("Exception thrown during test execution: " + e.message)
        }

    }

    private fun getToDoItem(hasReminder: Boolean): ToDoItem {
        return ToDoItem(TEXT_BODY, hasReminder, CURRENT_DATE)
    }
}
