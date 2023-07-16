package com.example.learnflow.components

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.example.learnflow.R
import fr.kameouss.instamemeeditor.components.ImagePickerFragment

class ImagePicker(context: Context, private val attrs: AttributeSet) : LinearLayout(context, attrs),
    IValidator, IComponent {

    override lateinit var tvError: TextView
    override var customValidator: CustomValidator? = null
    override var isRequired: Boolean = false

    private var imgPickerFragment: ImagePickerFragment? = null
    private val constraintLayout: ConstraintLayout
    private val ivPlaceholder: ImageView
    val ivImage: ImageView


    init {
        LayoutInflater.from(context).inflate(R.layout.image_picker, this)

        constraintLayout = findViewById(R.id.clImagePickerFragment)
        ivPlaceholder = findViewById(R.id.ivPlaceholderPickerFragment)
        ivImage = findViewById(R.id.ivImagePickerFragment)
        tvError = findViewById(R.id.tvErrorImagePickerFragment)

        handleAttrs()
    }

    override fun handleAttrs() {
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.ImagePicker, 0, 0)

        isRequired = styledAttributes.getBoolean(R.styleable.ImagePicker_isRequired, isRequired)
        styledAttributes.recycle()
    }

    override fun setListeners() {
        constraintLayout.setOnClickListener {
            imgPickerFragment?.pickImage {
                ivImage.setImageURI(it)
                ivImage.setPadding(0)
                ivImage.imageTintList = null
                ivImage.layoutParams?.width = LayoutParams.MATCH_PARENT
                ivImage.layoutParams?.height = LayoutParams.MATCH_PARENT
                ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    override fun triggerError(error: String) {
        tvError.text = error
        tvError.visibility = View.VISIBLE
    }

    override fun hideError() {
        tvError.visibility = View.GONE
    }

    override fun validate(): Boolean {
        if (!isRequired) return true

        Log.d("ImagePicker", "validating image picker")
        Log.d("ImagePicker", "ivImage.drawable: ${ivImage.drawable}")
        if (ivImage.drawable == null) {
            triggerError("Veuillez sélectionner une image")
            return false
        }

        hideError()
        return true
    }

}