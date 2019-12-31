package com.halalrishtey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        register_button.setOnClickListener {
            val email = registerEmail_editText.editText?.text
            val pw = registerPassword_editText.editText?.text

            userAuthVM.signUp(email.toString(), pw.toString())
                .observe(viewLifecycleOwner, Observer { authData: AuthData ->

                    if (authData.errorMessage != null) {
                        Snackbar.make(
                            view,
                            "Error: ${authData.errorMessage}",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                    }

                    if (authData.data != null) {
                        Toast.makeText(context, "User: ${authData.data.email}", Toast.LENGTH_SHORT)
                            .show()

                    }
                })
        }
    }
}
