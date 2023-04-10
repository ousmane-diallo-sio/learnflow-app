package com.example.learnflow.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.learnflow.R

class CustomBtn(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private val mLl: FrameLayout
    private val tv: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_btn, this)
        mLl = findViewById(R.id.flCustomBtn)
        tv = findViewById(R.id.tvCustomBtn)

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
        mLl.setOnClickListener {
            Toast.makeText(context, "test", Toast.LENGTH_LONG).show()
        }

    }
}