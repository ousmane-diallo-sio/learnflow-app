package com.example.learnflow.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.learnflow.R

class SelectorItem(context: Context, attrs: AttributeSet): LinearLayout(context, attrs), IComponent {

    private val cvItemWrapper: CardView
    private val tvItem: TextView
    private val defaultColors = HashMap<String, ColorStateList>()
    var isItemSelected = false
    set(value) {
        field = value
        val cvColorTo = context.getColor(if (value) R.color.aquamarine else R.color.bright_gray)
        val tvColorTo = context.getColor(if (value) R.color.bright_gray else R.color.aquamarine)
        Utils.animateBackgroundColor(
            cvItemWrapper,
            cvItemWrapper.cardBackgroundColor.defaultColor,
            cvColorTo
        ) { animator -> cvItemWrapper.setCardBackgroundColor(animator.animatedValue as Int) }
        Utils.animateBackgroundColor(
            tvItem,
            tvItem.currentTextColor,
            tvColorTo
        ) { animator -> tvItem.setTextColor(animator.animatedValue as Int) }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.selector_item, this)
        cvItemWrapper = findViewById(R.id.cvItemWrapperSelectorItem)
        tvItem = findViewById(R.id.tvItemSelectorItem)

        defaultColors["cvItemWrapper"] = cvItemWrapper.cardBackgroundColor
        defaultColors["tvItem"] = tvItem.textColors
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }
}