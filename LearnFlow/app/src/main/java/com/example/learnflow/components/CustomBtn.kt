package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.learnflow.R

class CustomBtn(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val ll: FrameLayout
    private val tv: TextView
    private val pb: ProgressBar
    private val llContent: LinearLayout

    private var isLoading: Boolean = false
        set(value) {
            field = value
            if (value) {
                pb.visibility = VISIBLE
                llContent.visibility = INVISIBLE
                return
            }
            pb.visibility = GONE
            llContent.visibility = VISIBLE
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_btn, this)
        ll = findViewById(R.id.flCustomBtn)
        tv = findViewById(R.id.tvCustomBtn)
        pb = findViewById(R.id.pbCustomBtn)
        llContent = findViewById(R.id.llContentCustomBtn)

        handleAttrs(attrs)
        setOnCLickListeners()
    }

    private fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomBtn, 0, 0)
        try {
            tv.text = styledAttributes.getString(R.styleable.CustomBtn_text)
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    private fun setOnCLickListeners() {
        ll.setOnClickListener { isLoading = !isLoading }
    }
}