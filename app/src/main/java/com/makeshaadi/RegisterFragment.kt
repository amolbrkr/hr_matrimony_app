package com.makeshaadi

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.makeshaadi.models.User
import com.makeshaadi.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*


class RegisterFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private var userLoc: Address? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userAuthVM.locationUpdates.observe(viewLifecycleOwner, Observer {
            val geocoder = Geocoder(context, Locale.getDefault())

            try {
                userLoc = if (it != null) {
                    geocoder.getFromLocation(it.latitude, it.longitude, 1)[0]
                } else null
            } catch (e: Exception) {
                Log.d("Register", "error: ${e.message}")
                Snackbar.make(view, "Error: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        })

        goToLoginBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        registerBackBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        registerBtn.setOnClickListener {
            val email = registerEmailTextInp.editText?.text
            val pw = registerPwTextInp.editText?.text
            val phone = phoneTextInp.editText?.text
            val confirmPw = confirmPWTextInp.editText?.text

            val emailError = userAuthVM.validateEmail(email.toString())
            val pwError = userAuthVM.validatePassword(pw.toString())
            val phoneError = userAuthVM.validatePhone(phone.toString())

            val pwMatch = if (pw.toString() != confirmPw.toString()) {
                confirmPWTextInp.error = "Passwords do not match!"
                false
            } else {
                true
            }

            if (emailError != null) {
                registerEmailTextInp.error = emailError
            } else {
                registerEmailTextInp.error = null
            }

            if (pwError != null) {
                registerPwTextInp.error = pwError
            } else {
                registerPwTextInp.error = null
            }

            if (phoneError != null) {
                phoneTextInp.error = phoneError
            } else {
                phoneTextInp.error = null
            }

            if (emailError == null && pwError == null && phoneError == null && pwMatch) {
                val newUser = if (userLoc != null) {
                    User(
                        email.toString(),
                        phoneNumber = phone.toString().toLong(),
                        address = userLoc!!.getAddressLine(0),
                        locationLat = userLoc!!.latitude,
                        locationLong = userLoc!!.longitude,
                        countryCode = userLoc!!.countryCode,
                        countryCallingCode = countryCodeTextInp.editText?.text.toString(),
                        pincode = userLoc!!.postalCode
                    )
                } else {
                    Snackbar.make(
                        view,
                        "We can't access your location which is required for proper functioning of the app. Please enable your location",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    User(
                        email.toString(),
                        phoneNumber = phone.toString().toLong(),
                        countryCallingCode = countryCodeTextInp.editText?.text.toString()
                    )
                }
                userAuthVM.newUser.value = newUser
                userAuthVM.pwd.value = pw.toString()

                //Navigate to personal details
                findNavController().navigate(
                    R.id.action_registerFragment_to_personalDetailsFragment
                )
            }
        }
    }
}

