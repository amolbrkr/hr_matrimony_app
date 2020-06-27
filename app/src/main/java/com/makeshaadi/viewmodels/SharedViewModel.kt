package com.makeshaadi.viewmodels

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val bundleFromUploadImageFragment = MutableLiveData<Bundle>()
    val uploadImageRequester = MutableLiveData<String>()
}