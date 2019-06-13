package com.finalproject.group14.noteapp.appDefault

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class AppDefaultFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(layoutRes(), container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @LayoutRes
    protected abstract fun layoutRes(): Int
}
