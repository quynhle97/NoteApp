package com.finalproject.group14.noteapp.analytics

import android.app.Application
import android.content.pm.PackageManager

import com.finalproject.group14.noteapp.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

class AnalyticsApplication : Application() {

    private var mTracker: Tracker? = null

    private val defaultTracker: Tracker
        @Synchronized get() {
            if (mTracker == null) {
                val analytics = GoogleAnalytics.getInstance(this)
                mTracker = analytics.newTracker(R.xml.global_tracker)
                mTracker!!.setAppName("Minimal")
                mTracker!!.enableExceptionReporting(true)
                try {
                    mTracker!!.setAppId(packageManager.getPackageInfo(packageName, 0).versionName)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            }
            return mTracker as Tracker
        }

    fun send(screenName: Any) {
        send(screenName, HitBuilders.ScreenViewBuilder().build())
    }

    private fun send(screenName: Any, params: Map<String, String>) {
        if (IS_ENABLED) {
            val tracker = defaultTracker
            tracker.setScreenName(getClassName(screenName))
            tracker.send(params)
        }
    }

    private fun getClassName(o: Any): String {
        var c: Class<*> = o.javaClass
        while (c.isAnonymousClass) {
            c = c.enclosingClass
        }
        return c.simpleName

    }

    fun send(screenName: Any, category: String, action: String) {
        send(screenName, HitBuilders.EventBuilder().setCategory(category).setAction(action).build())
    }

    fun send(screenName: Any, category: String, action: String, label: String) {
        send(screenName, HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build())
    }

    companion object {
        private val IS_ENABLED = true
    }
}
