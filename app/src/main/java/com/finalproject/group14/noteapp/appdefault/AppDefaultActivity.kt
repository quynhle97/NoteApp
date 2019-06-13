package com.finalproject.group14.noteapp.appDefault

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.finalproject.group14.noteapp.R

abstract class AppDefaultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayoutRes())
        setUpInitialFragment(savedInstanceState)

    }

    private fun setUpInitialFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, createInitialFragment())
                    .commit()
        }
    }

    @LayoutRes
    protected abstract fun contentViewLayoutRes(): Int

    protected abstract fun createInitialFragment(): Fragment
}
