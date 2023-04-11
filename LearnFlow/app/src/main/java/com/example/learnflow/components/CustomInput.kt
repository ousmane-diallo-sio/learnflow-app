package com.example.learnflow.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.learnflow.R

class CustomInput(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), IComponent {

    val et: EditText
    private val iv: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_input, this)
        et = findViewById(R.id.etCustomInput)
        iv = findViewById(R.id.ivCustomInput)

        handleAttrs(attrs)
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomInput, 0, 0)
        try {
            iv.setImageResource(
                styledAttributes.getResourceId(
                    R.styleable.CustomInput_icon,
                    R.drawable.ic_launcher_background
                )
            )
            et.setHint(styledAttributes.getResourceId(R.styleable.CustomInput_hint, R.string.app_name))
            et.inputType = styledAttributes.getInt(R.styleable.CustomInput_inputType, InputType.TYPE_CLASS_TEXT)
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun setOnClickListeners() {

    }


}