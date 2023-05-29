package com.example.learnflow.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import com.example.learnflow.R
import com.example.learnflow.utils.StringValidator

class CustomInput(context: Context, attrs: AttributeSet?): LinearLayout(context, attrs), IComponent, IValidator {

    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_NUMBER = 2
        const val TYPE_DATE = 16
        const val TYPE_EMAIL = 32
        const val TYPE_PASSWORD = 128
    }

    val et: EditText
    val tvError: TextView
    private val ivBefore: ImageView
    private val llAction: LinearLayout
    val ivAction: ImageView

    var textWatcher: TextWatcher? = object:TextWatcher{
        override fun afterTextChanged(s: Editable?) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onInputValidation(validate())
        }
    }
        set(value) {
            field = value
            et.removeTextChangedListener(field)
            et.addTextChangedListener(value)
        }

    var onInputValidation: ((isValid: Boolean) -> Unit) = {  }
    private var isRequired: Boolean = false

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_input, this)
        et = findViewById(R.id.etCustomInput)
        tvError = findViewById(R.id.tvErrorCustomInput)
        ivBefore = findViewById(R.id.ivCustomInput)
        llAction = findViewById(R.id.llActionCustomInput)
        ivAction = llAction.children.elementAt(0) as ImageView

        handleAttrs(attrs)
        et.addTextChangedListener(textWatcher)
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CustomInput, 0, 0)
        try {
            val resIconBefore = styledAttributes.getResourceId(
                R.styleable.CustomInput_icon,
                0
            )
            if (resIconBefore != 0) {
                ivBefore.setImageResource(resIconBefore)
            } else {
                ivBefore.visibility = GONE
            }
            et.setHint(styledAttributes.getResourceId(R.styleable.CustomInput_hint, R.string.app_name))
            et.inputType = styledAttributes.getInt(R.styleable.CustomInput_inputType, InputType.TYPE_CLASS_TEXT)
            isRequired = styledAttributes.getBoolean(R.styleable.CustomInput_isRequired, false)
            if (et.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                setTextTypePassword()
            }
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes.recycle()
        }
    }

    override fun setListeners() {}

    private fun setTextTypePassword() {
        val passwordTransformationMethod = PasswordTransformationMethod()
        val drawableHideText = R.drawable.baseline_lock_24
        val drawableShowText = R.drawable.baseline_remove_red_eye_24

        et.transformationMethod = passwordTransformationMethod
        val action = { _: View ->
            when(et.transformationMethod) {
                is PasswordTransformationMethod -> {
                    et.transformationMethod = null
                    setIvActionDrawable(context.getDrawable(drawableHideText)!!)
                }
                else -> {
                    et.transformationMethod = passwordTransformationMethod
                    setIvActionDrawable(context.getDrawable(drawableShowText)!!)
                }
            }
        }
        setAction(
            action,
            AppCompatResources.getDrawable(context, drawableShowText)!!
        )
    }

    fun setAction(onClick: OnClickListener, drawable: Drawable) {
        ivAction.setImageDrawable(drawable)
        llAction.setOnClickListener(onClick)
        llAction.visibility = VISIBLE
    }

    fun setAction(onClick: OnClickListener, uri: Uri) {
        ivAction.setImageURI(uri)
        ivAction.imageTintList = null
        llAction.setOnClickListener(onClick)
        llAction.visibility = VISIBLE
    }

    fun setIvActionTint(color: Int) {
        ivAction.setColorFilter(color)
    }

    fun setIvActionDrawable(drawable: Drawable) {
        ivAction.setImageDrawable(drawable)
    }

    override fun triggerError(error: String) {
        tvError.text = error
        tvError.visibility = VISIBLE
    }

    override fun hideError() {
        tvError.visibility = GONE
    }

    override fun validate(): Boolean {
        if (!isRequired) {
            hideError()
            return true
        }
        if (et.text.isBlank()) {
            triggerError(context.getString(R.string.required_field))
            return false
        }
        if (et.inputType == TYPE_EMAIL && !StringValidator.email(et.text.toString())) {
            triggerError(context.getString(R.string.invalid_email))
            return false
        }
        hideError()
        return true
    }

}