package com.example.learnflow.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.view.View
import android.view.ViewGroup





object Utils {
    fun textSizeToSp(context: Context, textSize: Float): Float {
        val scale = context.resources.displayMetrics.density
        return textSize / scale
    }

    fun animateBackgroundColor(view: View, colorFrom: Int, colorTo: Int, updateListener: AnimatorUpdateListener ) {
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener(updateListener)
        colorAnimation.start()
    }

    fun getAllNestedChildren(v: View): List<View> {
        val visited: MutableList<View> = ArrayList()
        val unvisited: MutableList<View> = ArrayList()
        unvisited.add(v)
        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0)
            visited.add(child)
            if (child !is ViewGroup) continue
            val childCount = child.childCount
            for (i in 0 until childCount) unvisited.add(child.getChildAt(i))
        }
        return visited
    }
}