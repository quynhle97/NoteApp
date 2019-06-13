package com.finalproject.group14.noteapp.utility

import android.content.res.Resources

import com.finalproject.group14.noteapp.R

class PreferenceKeys(resources: Resources) {
    val night_mode_pref_key: String

    init {
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key)
    }
}
