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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_upload_image.*


class UploadImageFragment : Fragment() {

    private lateinit var storageReference: StorageReference
    private var imgUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        storageReference = FirebaseStorage.getInstance().getReference("Images")
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

            Toast.makeText(context, "Upload in progress...", Toast.LENGTH_SHORT).show()
            uploadImg_button.isClickable = false

            val ref = storageReference
                .child("${System.currentTimeMillis()}.${getExt(imgUri!!)}")

            ref.putFile(imgUri!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                    ref.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val url: String = it.result.toString()
                            val bundle = bundleOf("aadharImgUrl" to url)
                            findNavController().navigate(
                                R.id.action_uploadImageFragment_to_professionalDetails,
                                bundle
                            )
                        } else {
                            uploadImg_button.isClickable = true
                        }
                    }
                })
                .addOnFailureListener(OnFailureListener {
                    uploadImg_button.isClickable = true
                    Snackbar.make(view, "Image upload failed, try again!", Snackbar.LENGTH_LONG)
                        .show()
                })
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
