package com.example.learnflow.components

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.learnflow.R

class Slider(context: Context?, attrs: AttributeSet) : LinearLayout(context, attrs), IComponent {

    private val llWrapper: LinearLayout
    private val rlItems: RelativeLayout
    val btnPrev: CustomBtn
    val btnNext: CustomBtn
    var items: ArrayList<SliderItem> = ArrayList()
    private set
    private var currentIndex = 0
    private var animationDurationMS: Long = 200

    init {
        LayoutInflater.from(context).inflate(R.layout.slider, this)
        orientation = VERTICAL

        llWrapper = findViewById(R.id.llWrapperSlider)
        rlItems = findViewById(R.id.llItemsSlider)
        btnPrev = findViewById(R.id.btnPrevSlider)
        btnNext = findViewById(R.id.btnNextSlider)

        handleAttrs(attrs)
        setListeners()

        llWrapper.viewTreeObserver.addOnGlobalLayoutListener {
            // prevents Slider items from disappearing when keyboard opens/closes
            items[currentIndex].animate().translationX(0f).duration = 0
        }
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.Slider, 0, 0)

        try {
            btnPrev.disabled = styledAttributes.getBoolean(R.styleable.Slider_disableBtnPrev, false)
            btnNext.disabled = styledAttributes.getBoolean(R.styleable.Slider_disableBtnNext, false)
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun setListeners() {
        btnPrev.setOnClickListener { slideBackwards() }
        btnNext.setOnClickListener { slideForward() }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null && child is SliderItem) {
            items.add(child)
        }
    }

    fun addItems(vararg _items: SliderItem) {
        for (item in _items) {
            if (items.contains(item)) continue
            if (item.parent != null) {
                (item.parent as ViewGroup).removeView(item)
            }
            items.add(item)
            setupItem(item)
        }
    }

    fun removeItems(vararg _items: SliderItem) {
        for (item in _items) {
            if (!items.contains(item)) continue
            items.remove(item)
            rlItems.removeView(item)
        }
    }

    private fun setupItem(item: SliderItem) {
        removeView(item)
        rlItems.addView(item)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        items.forEachIndexed { index, view ->
            setupItem(view)
            if (index != 0) {
                view.animate().translationX(width.toFloat()).duration = 0
            }
        }
        items[0].bringToFront()
        if (!isMovableLeft()) btnPrev.disabled = true
        if (!isMovableRight()) btnNext.disabled = true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        items.forEachIndexed { index, view ->
            if (index != 0) {
                view.translationX = width.toFloat()
            }
        }
    }

    private fun slideForward() {
        if (items.size > 1) {
            animateItem(false)
            btnPrev.disabled = !isMovableLeft()
            btnNext.disabled = !isMovableRight()
        }
    }

    private fun slideBackwards() {
        if (items.size > 1) {
            animateItem( true)
            btnPrev.disabled = !isMovableLeft()
            btnNext.disabled = !isMovableRight()
        }
    }

    private fun isMovableLeft(): Boolean {
        return currentIndex > 0
    }

    private fun isMovableRight(): Boolean {
        return currentIndex < items.size -1
    }

    private fun animateItem(backwards: Boolean) {
        if (backwards && !isMovableLeft()) return
        if (!backwards && !isMovableRight()) return

        val v = items[currentIndex]
        currentIndex = if (backwards) currentIndex - 1 else currentIndex + 1
        val newItem = items[currentIndex]
        val translationX = if (backwards) width.toFloat() else -width.toFloat()

        v.animate().translationX(translationX).duration = animationDurationMS
        newItem.bringToFront()
        newItem.animate().translationX(translationX * -1).duration = animationDurationMS
        newItem.animate().translationX(0f).duration = animationDurationMS
    }
}