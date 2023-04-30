package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.learnflow.R

class Slider(context: Context?, attrs: AttributeSet) : LinearLayout(context, attrs), IComponent {

    private val llWrapper: LinearLayout
    private val rlItems: RelativeLayout
    private val btnPrev: CustomBtn
    private val btnNext: CustomBtn
    private var items: ArrayList<View> = ArrayList()
    private var currentIndex = 0
    private var animationDurationMS: Long = 300

    init {
        LayoutInflater.from(context).inflate(R.layout.slider, this)
        orientation = VERTICAL

        llWrapper = findViewById(R.id.llWrapperSlider)
        rlItems = findViewById(R.id.llItemsSlider)
        btnPrev = findViewById(R.id.btnPrevSlider)
        btnNext = findViewById(R.id.btnNextSlider)

        setListeners()
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        btnPrev.setOnClickListener { slideBackwards() }
        btnNext.setOnClickListener { slideForward() }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null) {
            items.add(child)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        items = items.filter { it.parent === this && it.id != llWrapper.id } as ArrayList<View>
        items.forEachIndexed { index, view ->
            removeView(view)
            rlItems.addView(view)
            animateItem(view, index,true)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        items.forEachIndexed { index, view ->
            animateItem(view, index,true)
        }
    }

    private fun slideForward() {
        if (items.size > 1) {
            val nextIndex = (currentIndex + 1) % items.size
            animateItem(items[currentIndex], currentIndex, false)
            animateItem(items[nextIndex], nextIndex, false)
            currentIndex = nextIndex
            Log.d("Components", "index : $currentIndex")

        }
    }

    private fun slideBackwards() {
        if (items.size > 1) {
            val prevIndex = if (currentIndex == 0) items.size - 1 else currentIndex - 1
            animateItem(items[currentIndex], currentIndex, true)
            animateItem(items[prevIndex], prevIndex, true)
            currentIndex = prevIndex
            Log.d("Components", "index : $currentIndex")
        }
    }

    private fun animateItem(v: View, index: Int, backwards: Boolean) {
        val translationX = if (index == currentIndex) 0f else if (backwards) width.toFloat() else -width.toFloat()
        v.animate().translationX(translationX).duration = animationDurationMS
    }
}