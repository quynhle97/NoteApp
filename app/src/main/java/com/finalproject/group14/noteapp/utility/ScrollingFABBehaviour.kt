package com.finalproject.group14.noteapp.utility

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View

class ScrollingFABBehaviour(context: Context, attributeSet: AttributeSet) : CoordinatorLayout.Behavior<FloatingActionButton>(context, attributeSet) {
    private val toolbarHeight: Int

    init {
        this.toolbarHeight = Utils.getToolbarHeight(context)
    }


    override fun layoutDependsOn(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        return dependency is Snackbar.SnackbarLayout || dependency is Toolbar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout?, child: FloatingActionButton?, dependency: View?): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            val finalVal = parent!!.height.toFloat() - dependency.y
            child!!.translationY = -finalVal
        }
        if (dependency is Toolbar) {
            val lp = child!!.layoutParams as CoordinatorLayout.LayoutParams
            val fabBottomMargin = lp.bottomMargin
            val distanceToScroll = child.height + fabBottomMargin
            val finalVal = dependency.y / toolbarHeight.toFloat()
            val distFinal = -distanceToScroll * finalVal
            child.translationY = distFinal
        }


        return true
    }

    companion object {
        private val scrolledUp = false
        private val scrolledDown = false
    }

}
