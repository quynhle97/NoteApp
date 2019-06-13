package com.finalproject.group14.noteapp.main

import android.support.v7.widget.RecyclerView

abstract class CustomRecyclerScrollViewListener : RecyclerView.OnScrollListener() {
    internal var scrollDist = 0
    internal var isVisible = true

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (isVisible && scrollDist > MINIMUM) {
            hide()
            scrollDist = 0
            isVisible = false
        } else if (!isVisible && scrollDist < -MINIMUM) {
            show()
            scrollDist = 0
            isVisible = true
        }
        if (isVisible && dy > 0 || !isVisible && dy < 0) {
            scrollDist += dy
        }
    }

    abstract fun show()

    abstract fun hide()

    companion object {
        internal val MINIMUM = 20f
    }
}
