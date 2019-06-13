package com.finalproject.group14.noteapp.addtodo

import android.os.Bundle
import android.support.v4.app.Fragment

import com.finalproject.group14.noteapp.R
import com.finalproject.group14.noteapp.appDefault.AppDefaultActivity

class AddToDoActivity : AppDefaultActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.activity_add_to_do
    }

    override fun createInitialFragment(): Fragment {
        return AddToDoFragment.newInstance()
    }

    override fun onResume() {
        super.onResume()
    }

}

