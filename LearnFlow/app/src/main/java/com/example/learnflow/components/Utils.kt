package com.example.learnflow.components

import android.content.Context
import android.util.TypedValue

class Utils {
    companion object {

        fun textSizeToSp(context: Context, textSize: Float): Float {
            val scale = context.resources.displayMetrics.density
            return textSize / scale
        }

    }
}