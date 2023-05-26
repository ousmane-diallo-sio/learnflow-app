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
    private var multiSelection = false
    private var onElementSelected: ((SelectorItem) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.items_selector, this)
        fblItemsContainer = findViewById(R.id.fblItemsContainerItemSelector)
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ItemsSelector, 0, 0)

        try {
            multiSelection = styledAttributes.getBoolean(R.styleable.ItemsSelector_multiSelection, multiSelection)
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun setListeners() {}

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child != null && child is SelectorItem) {
            items.add(child)
        }
    }

    fun addItems(vararg _items: SelectorItem) {
        for (item in _items) {
            if (items.contains(item)) continue
            if (item.parent != null) {
                (item.parent as ViewGroup).removeView(item)
            }
            items.add(item)
            setupItem(item)
        }
    }

    fun removeItems(vararg _items: SelectorItem) {
        for (item in _items) {
            if (!items.contains(item)) continue
            items.remove(item)
            fblItemsContainer.removeView(item)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        items.forEach { setupItem(it) }
    }

    fun unselectAll() {
        items.forEach { it.isItemSelected = false }
    }

    fun setOnElementSelected(eventListener: (SelectorItem) -> Unit) {
        onElementSelected = eventListener
    }

    private fun setupItem(item: SelectorItem) {
        removeView(item)
        fblItemsContainer.addView(item)
        item.setOnClickListener {
            if (!multiSelection) unselectAll()
            item.isItemSelected = !item.isItemSelected
            onElementSelected?.invoke(item)
        }
    }

}