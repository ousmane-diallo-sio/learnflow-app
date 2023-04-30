package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.example.learnflow.R

class Slider(context: Context?, attrs: AttributeSet) : LinearLayout(context, attrs), IComponent {

    private val flItems: FrameLayout
    private val btnPrev: CustomBtn
    private val btnNext: CustomBtn
    private val items: ArrayList<View> = ArrayList()
    private var currentIndex = 0
    private var animationDurationMS: Long = 1000

    init {
        LayoutInflater.from(context).inflate(R.layout.slider, this)

        flItems = findViewById(R.id.flItemsSlider)
        btnPrev = findViewById(R.id.btnPrevSlider)
        btnNext = findViewById(R.id.btnNextSlider)

        items.filter { v -> v != items.first() }.forEach { v ->
            animateItem(v, true)
        }
        setListeners()
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        btnPrev.setOnClickListener {
            slideBackwards()
            btnPrev.isLoading = false
        }
        btnNext.setOnClickListener {
            slideForward()
            btnNext.isLoading = false
        }
    }

    override fun addView(child: View?) {
        Log.i("Components", String.format("test : %s", child.toString()))
        if (child != null) {
            flItems.addView(child)
            items.add(child)
        }
    }

    private fun slideForward() {
        if (items.size > 0) {
            val nextIndex = (currentIndex + 1) % items.size
            animateItem(items[currentIndex], false)
            animateItem(items[nextIndex], false)
            currentIndex = nextIndex
        }
    }

    private fun slideBackwards() {
        if (items.size > 0) {
            val prevIndex = (currentIndex - 1) % items.size
            animateItem(items[currentIndex], true)
            animateItem(items[prevIndex], true)
            currentIndex = prevIndex
        }
    }

    private fun animateItem(v: View, backwards: Boolean) {
        val translationX = if (backwards) -width.toFloat() else width.toFloat()
        v.animate().translationX(translationX).duration = animationDurationMS
    }
}