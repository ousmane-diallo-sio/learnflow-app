package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

// The purpose of this class is only the wrap SliderItems in order to recognize them
// in the Slider
class SliderItem(context: Context, private val attrs: AttributeSet): LinearLayout(context, attrs), IComponent {

    var slideValidator: () -> Boolean = { true }

    override fun handleAttrs() {
        // Not needed
    }

    override fun setListeners() {
        // Not needed
    }

    fun validate(): Boolean {
        return slideValidator.invoke()
    }

}