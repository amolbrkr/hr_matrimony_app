package com.makeshaadi.services

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage

data class UploadResult(
    var fileUrl: String?,
    var errorMsg: String?
)

object StorageService {
    private val storageReference by lazy {
        FirebaseStorage.getInstance()
    }

    val imagesReference = storageReference.getReference("Images")

    fun uploadImgToStorage(imageUri: Uri): MutableLiveData<UploadResult> {
        val fileLink = MutableLiveData<UploadResult>()

        val ref = imagesReference.child("${System.currentTimeMillis()}")

        ref.putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener {
                        fileLink.value = UploadResult(it.toString(), null)
                    }
                } else {
                    fileLink.value = UploadResult(null, "File upload failed!")
                }
            }
        return fileLink
    }

    fun getFileExt(filePath: String): String? {
        Log.d("StorageRepository", filePath)
        return filePath.substring(filePath.lastIndexOf("."))
    }
}