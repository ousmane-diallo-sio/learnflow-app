package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout

// The purpose of this class is only the wrap SliderItems in order to recognize them
// in the Slider
class SliderItem(context: Context, private val attrs: AttributeSet): LinearLayout(context, attrs), IComponent {

    override fun handleAttrs() {
        // Not needed
    }

    override fun setListeners() {
        // Not needed
    }
}