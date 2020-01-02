package com.halalrishtey

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.models.AuthData
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        register_button.setOnClickListener {
            val email = registerEmail_editText.editText?.text
            val pw = registerPassword_editText.editText?.text

            val emailError = userAuthVM.validateEmail(email.toString())
            val pwError = userAuthVM.validatePassword(pw.toString())

            if (emailError != null) {
                registerEmail_editText.error = emailError
            } else {
                registerEmail_editText.error = null
            }

            if (pwError != null) {
                registerPassword_editText.error = pwError
            } else {
                registerPassword_editText.error = null
            }

            if (emailError == null && pwError == null) {
                userAuthVM.signUp(email.toString(), pw.toString())
                    .observe(viewLifecycleOwner, Observer { authData: AuthData ->

                        if (authData.errorMessage != null) {
                            Snackbar.make(
                                view,
                                "Error: ${authData.errorMessage}",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }

                        if (authData.data != null && authData.errorMessage == null) {
                            val sharedPref =
                                context?.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
                                    ?.edit()

                            sharedPref?.putString("user_uid", authData.data.uid)
                            sharedPref?.commit()

                            //Create a new user
                            val newUser = User(authData.data.uid, authData.data.email!!)
                            val bundle = bundleOf("userInfo" to newUser)

                            //Navigate to personal details
                            findNavController().navigate(
                                R.id.action_registerFragment_to_personalDetailsFragment,
                                bundle
                            )

                            /*val i: Intent = Intent(
                                activity, MainActivity::class.java
                            )
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                            activity?.finish()*/
                        }
                    })
            }
        }
    }
}
