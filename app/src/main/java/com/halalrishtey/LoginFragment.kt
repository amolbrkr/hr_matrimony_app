package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        login_button.setOnClickListener {
            val email = email_editText.editText?.text
            val pw = password_editText.editText?.text

            userAuthVM.signIn(email.toString(), pw.toString())
                .observe(viewLifecycleOwner, Observer {
                    if (it?.errorMessage != null) {
                        Snackbar.make(view, "Error: ${it.errorMessage}", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    if (it?.data != null) {
                        Toast.makeText(context, "Logged in: ${it.data.email}", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        }

        goToRegisterButton.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}
