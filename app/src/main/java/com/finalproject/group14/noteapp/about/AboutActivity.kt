package com.finalproject.group14.noteapp.about

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.TextView

import com.finalproject.group14.noteapp.analytics.AnalyticsApplication
import com.finalproject.group14.noteapp.appDefault.AppDefaultActivity
import com.finalproject.group14.noteapp.main.MainFragment
import com.finalproject.group14.noteapp.R

class AboutActivity : AppDefaultActivity() {

    private val mVersionTextView: TextView? = null
    private var appVersion = "0.1"
    private var toolbar: Toolbar? = null
    internal var theme: String? = null
    //    private UUID mId;
    private val app: AnalyticsApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, Context.MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.DARKTHEME) {
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            setTheme(R.style.CustomStyle_LightTheme)
        }

        super.onCreate(savedInstanceState)
        //        mId = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);

        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            appVersion = info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.about_layout
    }

    override fun createInitialFragment(): Fragment {
        return AboutFragment.newInstance()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
