package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.learnflow.R
import com.google.android.flexbox.FlexboxLayout

class ItemsSelector(context: Context, attrs: AttributeSet): LinearLayout(context, attrs), IComponent {

    private val fblItemsContainer: FlexboxLayout
    private var items = ArrayList<SelectorItem>()

    init {
        LayoutInflater.from(context).inflate(R.layout.items_selector, this)

        fblItemsContainer = findViewById(R.id.fblItemsContainerItemSelector)
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        items.forEach{ item ->
            item.setOnClickListener {
                item.isSelected = !item.isSelected
            }
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null && child is SelectorItem) {
            items.add(child)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        items.forEachIndexed { index, view ->
            removeView(view)
            fblItemsContainer.addView(view)
        }
    }

}