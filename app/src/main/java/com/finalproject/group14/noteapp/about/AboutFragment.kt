package com.finalproject.group14.noteapp.about

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.finalproject.group14.noteapp.analytics.AnalyticsApplication
import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.appDefault.AppDefaultFragment

class AboutFragment : AppDefaultFragment() {

    private var mVersionTextView: TextView? = null
    private val appVersion = "0.1"
    private var toolbar: Toolbar? = null
    private var contactMe: TextView? = null
    private var app: AnalyticsApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app = activity.application as AnalyticsApplication
        app!!.send(this)
        mVersionTextView = view!!.findViewById(R.id.aboutVersionTextView) as TextView
        mVersionTextView!!.setText(java.lang.String.format(resources.getString(R.string.app_version), appVersion))
    }

    @LayoutRes
    override fun layoutRes(): Int {
        return R.layout.fragment_about
    }

    companion object {

        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
