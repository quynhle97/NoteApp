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

import android.test.ActivityUnitTestCase

import com.finalproject.group14.noteapp.main.MainActivity
import com.finalproject.group14.noteapp.utility.StoreRetrieveData
import com.finalproject.group14.noteapp.utility.ToDoItem

import java.util.ArrayList
import java.util.Date

/**
 * Test cases for StoreRetrieveData class
 */
class TestStoreRetrieveData : ActivityUnitTestCase<MainActivity>(MainActivity::class.java) {

    private var mMainActivity: MainActivity? = null
    private var mOriginalData: ArrayList<ToDoItem>? = null
    internal var mTestData: ArrayList<ToDoItem>

    private val dataStorage: StoreRetrieveData
        get() {
            val context = instrumentation.targetContext
            return StoreRetrieveData(context, MainActivity.FILENAME)
        }

    init {

        // Create some test data
        mTestData = ArrayList()
        for (i in 1..10) {
            mTestData.add(ToDoItem(
                    "item$i",
                    false,
                    Date()))
        }
    }

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        mMainActivity = activity
        mOriginalData = ArrayList()

        // Save the original data and wipe out the storage
        val dataStorage = dataStorage
        try {
            val items = dataStorage.loadFromFile()

            if (items.size > 0) {
                mOriginalData!!.clear()
                mOriginalData!!.addAll(items)

                items.clear()
                dataStorage.saveToFile(items)
            }

        } catch (e: Exception) {
            Assert.fail("Couldn't store data: " + e.message)
        }

    }

    @Throws(Exception::class)
    override fun tearDown() {
        super.tearDown()

        // Let's restore the data we might have wiped out during setUp()...
        val dataStorage = dataStorage
        dataStorage.saveToFile(mOriginalData)
    }

    /**
     * We should have an empty data storage at hand for the starters
     */
    fun testPreconditions() {
        val dataStorage = dataStorage

        var items: ArrayList<ToDoItem>? = null
        try {
            items = dataStorage.loadFromFile()
        } catch (e: Exception) {
            Assert.fail("Couldn't read from data storage: " + e.message)
        }

        Assert.assertEquals(0, items!!.size)
    }

    /**
     * Write items to data storage and ensure those same items can be retrieved from the storage.
     */
    fun testWritingToAndReadingFromTheDataStorage() {
        val dataStorage = dataStorage
        var retrievedItems = ArrayList<ToDoItem>()

        // Persist the test data
        try {
            dataStorage.saveToFile(mTestData)
        } catch (e: Exception) {
            Assert.fail("Couldn't store data: " + e.message)
        }

        // Read from storage
        try {
            retrievedItems = dataStorage.loadFromFile()
        } catch (e: Exception) {
            Assert.fail("Couldn't read from data storage: " + e.message)
        }

        // We should have equal amount of items than what we just stored
        Assert.assertEquals(mTestData.size, retrievedItems.size)

        // The content should be same as well...
        for (retrievedItem in retrievedItems) {
            // We want to be sure every single item in data storage can also be found from
            // our test data collection
            var found = false
            for (testItem in mTestData) {

                // Check the items are same
                if (retrievedItem.identifier == testItem.identifier &&
                        retrievedItem.toDoText == testItem.toDoText &&
                        retrievedItem.hasReminder() == testItem.hasReminder() &&
                        retrievedItem.toDoDate == testItem.toDoDate) {

                    found = true
                    break
                }
            }

            if (!found) {
                Assert.fail("Content mis-match between test data and data retrieved from the storage!")
            }
        }
    }

    /**
     * Ensure JSONArray conversion works as intended
     */
    fun testArrayListToJsonArrayConversion() {
        try {
            val array = StoreRetrieveData.toJSONArray(mTestData)
            Assert.assertEquals(mTestData.size, array.length())
        } catch (e: Exception) {
            Assert.fail("Exception thrown when converting to JSONArray: " + e.message)
        }

    }
}
