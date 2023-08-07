package com.example.learnflow.components

import android.content.Context
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.learnflow.R

class CheckboxCounterItem(val viewContext: Context) :
    LinearLayout(viewContext, null), IComponent {

    val cvRoot: CardView
    val cb: CheckBox
    val tv: TextView
    val ci: CustomInput

    init {
        LayoutInflater.from(viewContext).inflate(R.layout.checkbox_counter_item, this)

        cvRoot = findViewById(R.id.cvRootCheckboxCounterItem)
        cb = findViewById(R.id.cbCheckboxCounterItem)
        tv = findViewById(R.id.tvCheckboxCounterItem)
        ci = findViewById(R.id.ciCheckboxCounterItem)

        handleAttrs()
        setListeners()
    }

    override fun handleAttrs() {}

    override fun setListeners() {
        cvRoot.setOnClickListener {
            cb.isChecked = !cb.isChecked
            if (cb.isChecked) {
                ci.visibility = VISIBLE
                cvRoot.backgroundTintList = viewContext.getColorStateList(R.color.cultured)
                return@setOnClickListener
            }
            ci.visibility = GONE
            cvRoot.backgroundTintList = viewContext.getColorStateList(R.color.white)
        }
    }

}