package fr.kameouss.instamemeeditor.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.learnflow.R
import com.example.learnflow.components.CustomValidator
import com.example.learnflow.components.IComponent
import com.example.learnflow.components.IValidator
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class ImagePickerFragment : Fragment(), IValidator, IComponent {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var ivPlaceholder: ImageView
    lateinit var ivImage: ImageView
    override lateinit var tvError: TextView
    override var customValidator: CustomValidator? = null
    override var isRequired: Boolean = false
    private var callback: ((uri: Uri) -> Unit?)? = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_picker, container, false)
        constraintLayout = view.findViewById(R.id.clImagePickerFragment)
        ivPlaceholder = view.findViewById(R.id.ivPlaceholderPickerFragment)
        ivImage = view.findViewById(R.id.ivImagePickerFragment)
        tvError = view.findViewById(R.id.tvErrorImagePickerFragment)
        setListeners()

        return view
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        handleAttrs(attrs)
    }

    override fun handleAttrs(attrs: AttributeSet?) {
        val styledAttributes = context?.obtainStyledAttributes(attrs, R.styleable.ImagePickerFragment, 0, 0)

        try {
            isRequired = styledAttributes?.getBoolean(R.styleable.ImagePickerFragment_isRequired, isRequired) ?: isRequired
        } catch (e: Exception) {
            Log.e("Components", e.toString())
        } finally {
            styledAttributes?.recycle()
        }
    }

    override fun setListeners() {
        constraintLayout.setOnClickListener {
            pickImage {
                ivImage.setImageURI(it)
                ivImage.setPadding(0)
                ivImage.imageTintList = null
                ivImage.layoutParams?.width = LinearLayout.LayoutParams.MATCH_PARENT
                ivImage.layoutParams?.height = LinearLayout.LayoutParams.MATCH_PARENT
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

        if (ivImage.drawable == null) {
            triggerError("Veuillez sélectionner une image")
            return false
        }

        hideError()
        return true
    }

    var mGetContent = registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            crop(uri)
        }
    }

    var UCropActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri: Uri? = result.data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                callback?.invoke(resultUri)
            }
        } else {
            Toast.makeText(
                context,
                "Une erreur est survenue\n Code d'erreur : ${result.resultCode}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun pickImage(callBack: (uri: Uri) -> Unit) {
        callback = callBack
        mGetContent.launch("image/*")
    }

    fun crop(sourceUri: Uri) {
        val destUriString = StringBuilder(UUID.randomUUID().toString()).append(".png").toString()
        val destUri = Uri.fromFile(File(requireActivity().cacheDir, destUriString))

        val uCropOptions: UCrop.Options = UCrop.Options()
        val mainColor = context?.getColor(R.color.salmon) ?: Color.BLUE
        uCropOptions.setCompressionFormat(Bitmap.CompressFormat.PNG)
        uCropOptions.setLogoColor(mainColor)
        uCropOptions.setStatusBarColor(Color.BLACK)
        uCropOptions.setToolbarColor(mainColor)
        uCropOptions.setToolbarWidgetColor(Color.WHITE)
        uCropOptions.setActiveControlsWidgetColor(mainColor)
        val intent: Intent = UCrop.of(sourceUri, destUri).getIntent(requireContext())
        intent.putExtras(uCropOptions.optionBundle)
        UCropActivityResultLauncher.launch(intent)
    }
}