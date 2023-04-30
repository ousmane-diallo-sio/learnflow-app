package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.learnflow.R

class CustomBtn(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), IComponent {

    private val cv: CardView
    private val ll: FrameLayout
    private val tv: TextView
    private val pb: ProgressBar
    private val llContent: LinearLayout

    var isLoading: Boolean = false
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

    var disabled: Boolean = false
        set(value) {
            field = value
            if (value) {
                alpha = 0.5f
                return
            }
            alpha = 1f
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_btn, this)
        cv = findViewById(R.id.cvCustomBtn)
        ll = findViewById(R.id.flCustomBtn)
        tv = findViewById(R.id.tvCustomBtn)
        pb = findViewById(R.id.pbCustomBtn)
        llContent = findViewById(R.id.llContentCustomBtn)

        handleAttrs(attrs)
        setListeners()
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomBtn, 0, 0)
        try {
            tv.text = styledAttributes.getString(R.styleable.CustomBtn_text)
            tv.textSize = Utils.textSizeToSp(context, styledAttributes.getDimension(R.styleable.CustomBtn_textSize, tv.textSize))
            ll.setBackgroundColor(context.getColor(styledAttributes.getResourceId(R.styleable.CustomBtn_btnBackgroundColor, R.color.white)))
            tv.setTextColor(context.getColor(styledAttributes.getResourceId(R.styleable.CustomBtn_textColor, R.color.salmon)))
            val customHeight = styledAttributes.getDimension(R.styleable.CustomBtn_customHeight, 0f)
            if (customHeight.toInt() != 0) {
                cv.layoutParams.height = customHeight.toInt()
            }
            val elevation = styledAttributes.getDimension(R.styleable.CustomBtn_elevation, -1f)
            if (elevation.toInt() == 0) {
                cv.useCompatPadding = false
            } else if (elevation.toInt() > 0) {
                cv.elevation = elevation
            }
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun setListeners() {

    }

    override fun setOnClickListener(l: OnClickListener?) {
        val onClickWrapper: (View) -> Unit = { view ->
            if (!disabled) {
                isLoading = true
                l?.onClick(view)
            }
        }
        super.setOnClickListener(onClickWrapper)
    }
}