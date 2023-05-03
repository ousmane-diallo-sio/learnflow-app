package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
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

    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null && child is SelectorItem) {
            items.add(child)
            Log.d("Components", "item size : ${items.size}")
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        items.forEachIndexed { _, item ->
            removeView(item)
            fblItemsContainer.addView(item)
            item.setOnClickListener {
                item.isItemSelected = !item.isItemSelected
            }
        }
    }

}