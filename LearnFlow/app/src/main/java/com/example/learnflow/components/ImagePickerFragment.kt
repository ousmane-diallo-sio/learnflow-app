package fr.kameouss.instamemeeditor.components

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.learnflow.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*
import java.util.concurrent.Callable

class ImagePickerFragment : Fragment() {
    private var callback: (uri: Uri) -> Unit? = {}

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
                callback(resultUri)
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