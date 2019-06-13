package com.finalproject.group14.noteapp.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem

import com.finalproject.group14.noteapp.about.AboutActivity
import com.finalproject.group14.noteapp.appDefault.AppDefaultActivity
import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.settings.SettingsActivity

class MainActivity : AppDefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById(R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun createInitialFragment(): Fragment {
        return MainFragment.newInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(this, AboutActivity::class.java)
                startActivity(i)
                return true
            }
            R.id.preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}


