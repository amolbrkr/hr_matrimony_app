package com.halalrishtey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.models.AuthData
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
                            val i: Intent = Intent(
                                activity, MainActivity::class.java
                            )
                            startActivity(i)
                            activity?.finish()
                        }
                    })
            }
        }
    }
}
