package com.halalrishtey


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_otpverification.*
import java.util.concurrent.TimeUnit

class OTPVerificationFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_otpverification, container, false)
    }

    override fun onStart() {
        super.onStart()

        val phoneNum =
            userAuthVM.newUser.value?.countryCallingCode + userAuthVM.newUser.value?.phoneNumber

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                if (p0.smsCode != null) {
                    Log.d("OTP", p0.smsCode)

                    userAuthVM.newUser.value?.isOTPVerified = true
                    otpTextInp.editText?.setText(p0.smsCode)

                    val email = userAuthVM.newUser.value!!.email
                    val pw = userAuthVM.pwd.value!!

                    userAuthVM.signUp(
                        email, pw, userAuthVM.newUser.value!!
                    ).observe(viewLifecycleOwner, Observer { authData ->
                        if (authData.data != null) {

                            val sharedPref =
                                context?.getSharedPreferences("halalrishtey", Context.MODE_PRIVATE)
                                    ?.edit()

                            sharedPref?.putString("user_uid", authData.data.uid)
                            sharedPref?.commit()

                            Toast.makeText(
                                context,
                                "Successfully Created: ${authData.data.uid}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val i = Intent(
                                activity, MainActivity::class.java
                            )
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                            activity?.finish()
                        } else {
                            Snackbar.make(
                                requireView(),
                                "Error: ${authData.errorMessage}",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    })
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("OTP", p0.message)
                val s = Snackbar.make(
                    requireView(),
                    "Error: ${p0.message}",
                    Snackbar.LENGTH_INDEFINITE
                )

                s.setAction("OK") {
                    s.dismiss()
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                Log.d("OTP", "p0: $p0")
                timeRemainingText.text = "Time Remaining: 60"
                val h = Handler()
                var count = 60
                val r = object : Runnable {
                    override fun run() {
                        count--
                        timeRemainingText.text = "Time Remaining: $count"
                        if (count > 0) h.postDelayed(this, 1000)
                    }
                }
                h.postDelayed(r, 1000)
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNum,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            callbacks
        )
    }
}
