package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.learnflow.R
import com.example.learnflow.utils.Utils

class CustomBtn(context: Context, private val attrs: AttributeSet?) : LinearLayout(context, attrs), IComponent {

    private val cv: CardView
    private val ll: FrameLayout
    private val iconBefore: ImageView
    val tv: TextView
    private val iconAfter: ImageView
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
                animate().alpha(0f).duration = 200
                return
            }
            animate().alpha(1f).duration = 200
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_btn, this)
        cv = findViewById(R.id.cvCustomBtn)
        ll = findViewById(R.id.flCustomBtn)
        iconBefore = findViewById(R.id.iconBeforeCustomBtn)
        tv = findViewById(R.id.tvCustomBtn)
        iconAfter = findViewById(R.id.iconAfterCustomBtn)
        pb = findViewById(R.id.pbCustomBtn)
        llContent = findViewById(R.id.llContentCustomBtn)

        handleAttrs()
        setListeners()
    }

    override fun handleAttrs() {
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
            iconBefore.setImageResource(styledAttributes.getResourceId(R.styleable.CustomBtn_iconBefore, 0))
            iconAfter.setImageResource(styledAttributes.getResourceId(R.styleable.CustomBtn_iconAfter, 0))
            val iconsSize = styledAttributes.getDimension(R.styleable.CustomBtn_iconsSize, 0f)
            if (iconsSize > 0) {
                if (iconBefore.drawable != null) {
                    iconBefore.layoutParams.width = iconsSize.toInt()
                    iconBefore.layoutParams.height = iconsSize.toInt()
                    iconBefore.requestLayout()
                }
                if (iconAfter.drawable != null) {
                    iconAfter.layoutParams.width = iconsSize.toInt()
                    iconAfter.layoutParams.height = iconsSize.toInt()
                    iconAfter.requestLayout()
                }
            }
            iconBefore.setColorFilter(tv.currentTextColor)
            iconAfter.setColorFilter(tv.currentTextColor)

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
                l?.onClick(view)
            }
        }
        super.setOnClickListener(onClickWrapper)
    }
}