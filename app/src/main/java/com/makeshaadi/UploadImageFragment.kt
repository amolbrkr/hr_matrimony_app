package com.makeshaadi


import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.UploadTask
import com.makeshaadi.services.StorageService
import com.makeshaadi.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_upload_image.*
import java.io.ByteArrayOutputStream


class UploadImageFragment : Fragment() {
    private val sharedVM: SharedViewModel by activityViewModels()
    private var imgUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        chooseImg_button.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, 1)
        }

        uploadImg_button.setOnClickListener {
            if (imgUri != null) {
                Toast.makeText(context, "Upload in progress...", Toast.LENGTH_SHORT).show()
                uploadImg_button.isEnabled = false
                uploadProgress.visibility = View.VISIBLE

                val ref = StorageService.imagesReference
                    .child("${System.currentTimeMillis()}.${getExt(imgUri!!)}")

                ref.putFile(getImageUri(requireContext(), uploadImg_imageView.croppedImage)!!)
                    .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                        ref.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val url: String = it.result.toString()
                                val bundle = Bundle().apply { putString("uploadedImageUrl", url) }
                                sharedVM.bundleFromUploadImageFragment.value = bundle
                                requireActivity().onBackPressed()
                            } else {
                                uploadProgress.visibility = View.GONE
                                uploadImg_button.isEnabled = true
                            }
                        }
                    })
                    .addOnFailureListener(OnFailureListener {
                        uploadImg_button.isEnabled = true
                        Snackbar.make(view, "Error: ${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    })
            } else {
                Snackbar.make(view, "No Image selected!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            prvHelpText.visibility = View.GONE
            uploadImg_imageView.setImageUriAsync(data.data)
            imgUri = data.data
        }
    }

    private fun getExt(uri: Uri): String? {
        val contentResolver = activity?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }
}
