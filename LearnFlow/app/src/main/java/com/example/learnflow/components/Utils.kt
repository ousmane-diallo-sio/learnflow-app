package com.example.learnflow.components

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.view.View


class Utils {
    companion object {

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

    }
}