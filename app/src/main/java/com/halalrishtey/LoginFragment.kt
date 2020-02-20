package com.halalrishtey


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {

    private val userAuthVM: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        val loc = userAuthVM.locationUpdates.value
//        val addr = if (loc != null) {
//            CustomUtils.convertCoordsToAddr(requireContext(), loc.latitude, loc.longitude);
//        } else null
//
//        val infoToBeUpdated = hashMapOf(
//            "pincode" to addr?.postalCode,
//            "locationLat" to addr?.latitude,
//            "locationLong" to addr?.longitude,
//            "address" to addr?.getAddressLine(0),
//            "lastSignInAt" to System.currentTimeMillis()
//        )

        login_button.setOnClickListener {
            val email = email_editText.editText?.text
            val pw = password_editText.editText?.text

            val emailError = userAuthVM.validateEmail(email.toString())
            val pwError = userAuthVM.validatePassword(pw.toString())

            if (emailError != null) {
                email_editText.error = emailError
            } else {
                email_editText.error = null
            }

            if (pwError != null) {
                password_editText.error = pwError
            } else {
                password_editText.error = null
            }

            if (emailError == null && pwError == null) {
                userAuthVM.signIn(email.toString(), pw.toString(), null)
                    .observe(viewLifecycleOwner, Observer {
                        if (it?.errorMessage != null) {
                            Snackbar.make(view, "Error: ${it.errorMessage}", Snackbar.LENGTH_LONG)
                                .show()
                        }

                        if (it?.data != null && it.errorMessage == null) {
                            //Saving authentication data
                            val sharedPref =
                                context?.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
                                    ?.edit()

                            sharedPref?.putString("user_uid", it.data.uid)
                            sharedPref?.commit()

                            val i: Intent = Intent(
                                activity, MainActivity::class.java
                            )
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                            activity?.finish()
                        }
                    })
            }
        }


        goToResetPwButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }

        goToRegisterButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}
