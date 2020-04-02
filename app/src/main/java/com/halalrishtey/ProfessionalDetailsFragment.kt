package com.halalrishtey

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
                "Never Married",
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

            if (validateProfessionalDetails() != null) {
                Snackbar.make(
                    view,
                    "Error: ${validateProfessionalDetails()}",
                    Snackbar.LENGTH_SHORT
                ).show()
                pdRegisterButton.isEnabled = true
            } else {
                userAuthVM.newUser.value?.apply {
                    workLocation =
                        workLocTextInp.editText?.text.toString()
                    organizationName =
                        orgNameTextInp.editText?.text.toString()
                    annualIncome =
                        Integer.valueOf(incomeTextInp.editText?.text.toString())
                    maritalStatus =
                        maritalStatusSpinner.selectedItem.toString()
                    sect = sectSpinner.selectedItem.toString()
                    dargah = dargahSpinner.selectedItem.toString()

                    val email = email
                    val pw = userAuthVM.pwd.value!!
                    Log.d("ProfessionalDetails", "Trying to create user for: $email & $pw")
                }
                findNavController().navigate(R.id.action_professionalDetails_to_OTPVerificationFragment)
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
