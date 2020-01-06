package com.halalrishtey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
                //user takes a id and an email
                val newUser = User(email.toString())
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

