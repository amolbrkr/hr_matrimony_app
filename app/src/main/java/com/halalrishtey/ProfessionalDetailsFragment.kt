package com.halalrishtey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.viewmodels.SharedViewModel
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_professional_details.*

class ProfessionalDetailsFragment : Fragment() {
    private val sharedVM: SharedViewModel by activityViewModels()
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private var savedViewInstance: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            savedViewInstance =
                inflater.inflate(R.layout.fragment_professional_details, container, false)
            savedViewInstance
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedVM.bundleFromUploadImageFragment.observe(viewLifecycleOwner, Observer { bundle ->
            val uploadedImgUrl = bundle.getString("uploadedImageUrl", "Not Uploaded")
            if (uploadedImgUrl != "Not Uploaded" && sharedVM.uploadImageRequester.value == "ProfessionalDetailsFragment") {
                userAuthVM.newUser.value?.idProofUrl = uploadedImgUrl
            }
        })

        maritalStatusSpinner.adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Marital Status",
                "Bachelor",
                "Divorced",
                "Widowed",
                "Annulled"
            )
        )
        maritalStatusSpinner.setSelection(0)

        sectSpinner.adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Maslak/Sect",
                "Hanafi",
                "Maliki",
                "Shafa'i",
                "Hanbali"
            )
        )
        sectSpinner.setSelection(0)

        dargahSpinner.adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Dargah/Fateha",
                "Yes",
                "No",
                "Neutral"
            )
        )
        dargahSpinner.setSelection(0)

        uploadAdhar_Button.setOnClickListener {
            sharedVM.uploadImageRequester.value = "ProfessionalDetailsFragment"
            findNavController().navigate(R.id.action_professionalDetails_to_uploadImageFragment)
        }

        pdRegisterButton.setOnClickListener {
            pdRegisterButton.isEnabled = false
            registrationProgressBar.visibility = View.VISIBLE

            if (validateProfessionalDetails() != null) {
                Snackbar.make(
                    view,
                    "Error: ${validateProfessionalDetails()}",
                    Snackbar.LENGTH_SHORT
                ).show()

                pdRegisterButton.isEnabled = true
                registrationProgressBar.visibility = View.GONE
            } else {

                userAuthVM.newUser.value?.occupation = workLocTextInp.editText?.text.toString()
                userAuthVM.newUser.value?.organizationName =
                    orgNameTextInp.editText?.text.toString()
                userAuthVM.newUser.value?.annualIncome =
                    Integer.valueOf(incomeTextInp.editText?.text.toString())
                userAuthVM.newUser.value?.maritalStatus =
                    maritalStatusSpinner.selectedItem.toString()
                userAuthVM.newUser.value?.sect = sectSpinner.selectedItem.toString()
                userAuthVM.newUser.value?.dargah = dargahSpinner.selectedItem.toString()

                val email = userAuthVM.newUser.value!!.email
                val pw = userAuthVM.pwd.value!!

                Log.d("Auth", "Trying to create user for: $email & $pw")

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
                        Snackbar.make(view, "Error: ${authData.errorMessage}", Snackbar.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }
    }

    private fun validateProfessionalDetails(): String? {

        return when {
            maritalStatusSpinner.selectedItemPosition == 0 -> {
                "Please select your marital status"
            }
            sectSpinner.selectedItemPosition == 0 -> {
                "Please select your Maslak/Sect"
            }
            dargahSpinner.selectedItemPosition == 0 -> {
                "Please select your dargah"
            }
            userAuthVM.newUser.value?.idProofUrl.isNullOrBlank() || userAuthVM.newUser.value?.idProofUrl == "Not Uploaded" -> {
                "Please upload your ID Proof correctly"
            }
            workLocTextInp.editText?.text.isNullOrBlank() -> {
                "Please tells where you work"
            }
            incomeTextInp.editText?.text.isNullOrBlank() -> {
                "Please enter your annual income"
            }
            orgNameTextInp.editText?.text.isNullOrBlank() -> {
                "Please enter name of organization where you work"
            }
            else -> null
        }
    }
}
