package com.example.learnflow.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.io.ByteArrayOutputStream


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

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("Utils", "base64ToBitmap: ", e)
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun convertDpToPx(px: Int): Float {
        return (px * Resources.getSystem().displayMetrics.density)
    }
}