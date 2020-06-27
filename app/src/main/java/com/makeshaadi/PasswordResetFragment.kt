package com.makeshaadi


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.makeshaadi.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_password_reset.*

/**
 * A simple [Fragment] subclass.
 */
class PasswordResetFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resetPw_button.setOnClickListener {
            val email = resetPwEmail_editText.editText?.text.toString()

            prBackBtn.setOnClickListener {
                activity?.onBackPressed()
            }

            resetPwEmail_editText.error = if (userAuthVM.validateEmail(email) != null) {
                userAuthVM.validateEmail(email)
            } else {
                null
            }

            if (userAuthVM.validateEmail(email) == null) {
                userAuthVM.sendResetPasswordEmail(email).observe(viewLifecycleOwner, Observer {
                    Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                })
            }
        }
    }
}
