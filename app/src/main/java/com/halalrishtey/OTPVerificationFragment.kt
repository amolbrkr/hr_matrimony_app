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
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_otpverification.*
import java.util.concurrent.TimeUnit

class OTPVerificationFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var forceResendingToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_otpverification, container, false)
    }

    override fun onStart() {
        super.onStart()

        //Assign the free plan to User
        userVM.getFreePlan().observe(viewLifecycleOwner, Observer { freePlan ->
            if (freePlan != null)
                userAuthVM.newUser.value?.currentPlan = freePlan
        })

        resendOtpBtn.isEnabled = false

        val phoneNum =
            userAuthVM.newUser.value?.countryCallingCode + userAuthVM.newUser.value?.phoneNumber

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                if (p0.smsCode != null) {
                    userAuthVM.newUser.value?.isOTPVerified = true
                    otp_view.setText(p0.smsCode)

                    timeRemainingText.text = "OTP Received"
                    Toast.makeText(
                        context,
                        "Horray! Thanks for verifying...",
                        Toast.LENGTH_SHORT
                    ).show()

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
                resendOtpBtn.isEnabled = true
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

                //Assign resending token
                forceResendingToken = p1
                startCounter()
            }
        }

        resendOtpBtn.setOnClickListener {
            this.startCounter()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNum,
                60,
                TimeUnit.SECONDS,
                requireActivity(),
                callbacks,
                forceResendingToken
            )
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNum,
            60,
            TimeUnit.SECONDS,
            requireActivity(),
            callbacks
        )
    }

    fun startCounter() {
        timeRemainingText.text = "Time Remaining: 60 Seconds"
        val h = Handler()
        var count = 60
        val r = object : Runnable {
            override fun run() {
                count--
                timeRemainingText?.text = "Time Remaining: $count Seconds"
                if (count > 0) h.postDelayed(this, 1000)
            }
        }
        h.postDelayed(r, 1000)
    }
}
