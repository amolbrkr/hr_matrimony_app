package com.halalrishtey


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.halalrishtey.services.StorageRepository
import com.halalrishtey.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_upload_image.*


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

                val ref = StorageRepository.imagesReference
                        .child("${System.currentTimeMillis()}.${getExt(imgUri!!)}")

                ref.putFile(imgUri!!)
                        .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                            ref.downloadUrl.addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val url: String = it.result.toString()
                                    val bundle = Bundle().apply { putString("uploadedImageUrl", url) }
                                    sharedVM.bundleFromUploadImageFragment.value = bundle
                                    requireActivity().onBackPressed()
                                } else {
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
            imgUri = data.data
            uploadImg_imageView.setImageURI(imgUri)
        }
    }

    private fun getExt(uri: Uri): String? {
        val contentResolver = activity?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }
}
