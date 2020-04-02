package com.halalrishtey

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.models.Gender
import com.halalrishtey.viewmodels.SharedViewModel
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_personal_details.*
import java.util.*


class PersonalDetailsFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private val sharedVM: SharedViewModel by activityViewModels()
    private var savedViewInstance: View? = null
    private var userAge: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            savedViewInstance =
                inflater.inflate(R.layout.fragment_personal_details, container, false)
            savedViewInstance
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedVM.bundleFromUploadImageFragment.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { bundle ->
                val photoUrl = bundle.getString("uploadedImageUrl", "Not Uploaded")

                if (photoUrl != "Not Uploaded" && sharedVM.uploadImageRequester.value == "PersonalDetailsFragment") {
                    Picasso.get().load(photoUrl).into(profileImageView)
                    userAuthVM.newUser.value?.photoUrl = photoUrl
                }
            })

        eduSpinner.adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Highest Education",
                "Doctor",
                "Engineer",
                "Professional Degree",
                "Islamic Degree",
                "Post Graduate",
                "Graduate",
                "Under Graduate",
                "Intermediate",
                "Pre School/SSC"
            )
        )
        eduSpinner.setSelection(0)

        createdForSpinner.adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                arrayOf(
                    "Creating account for",
                    "Myself",
                    "Relative",
                    "Son",
                    "Daughter",
                    "Brother",
                    "Sister"
                )
            )
        createdForSpinner.setSelection(0)

        heightSpinner.adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Height",
                "7 ft 4 in",
                "7 ft 3 in",
                "7 ft 2 in",
                "7 ft 1 in",
                "7 ft 0 in",
                "6 ft 11 in",
                "6 ft 10 in",
                "6 ft 9 in",
                "6 ft 8 in",
                "6 ft 7 in",
                "6 ft 6 in",
                "6 ft 5 in",
                "6 ft 4 in",
                "6 ft 3 in",
                "6 ft 2 in",
                "6 ft 1 in",
                "6 ft 0 in",
                "5 ft 11 in",
                "5 ft 10 in",
                "5 ft 9 in",
                "5 ft 8 in",
                "5 ft 7 in",
                "5 ft 6 in",
                "5 ft 5 in",
                "5 ft 4 in",
                "5 ft 3 in",
                "5 ft 2 in",
                "5 ft 1 in",
                "5 ft 0 in",
                "4 ft 11 in",
                "4 ft 10 in",
                "4 ft 9 in",
                "4 ft 8 in",
                "4 ft 7 in",
                "4 ft 6 in",
                "4 ft 5 in",
                "4 ft 4 in",
                "4 ft 3 in",
                "4 ft 2 in",
                "4 ft 1 in",
                "4 ft 0 in"
            )
        )
        heightSpinner.setSelection(0)

        val cal: Calendar = Calendar.getInstance()
        val onDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date = "$dayOfMonth / ${monthOfYear + 1} / $year"
                userAuthVM.newUser.value?.dateOfBirth =
                    GregorianCalendar(year, monthOfYear + 1, dayOfMonth).time

                //Calculate and set age of user
                userAge = 2020 - year
                if (userAge < 18) {
                    Snackbar.make(view, "User must be 18 years old", Snackbar.LENGTH_SHORT).show()
                } else {
                    userAuthVM.newUser.value?.age = userAge
                }
                setDobBtn.text = date
            }

        setDobBtn.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                onDateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        profileImageView.setOnClickListener {
            sharedVM.uploadImageRequester.value = "PersonalDetailsFragment"
            findNavController().navigate(R.id.action_personalDetailsFragment_to_uploadImageFragment)
        }

        pdNextButton.setOnClickListener {
            if (validatePersonalDetails() == null) {

                userAuthVM.newUser.value?.displayName =
                    nameTextInp.editText?.text.toString()

                userAuthVM.newUser.value?.createdFor = createdForSpinner.selectedItem.toString()
                userAuthVM.newUser.value?.height = heightSpinner.selectedItem.toString()

                userAuthVM.newUser.value?.gender = Gender.valueOf(
                    if (maleSelector.isChecked) {
                        "Male"
                    } else {
                        "Female"
                    }
                )

                userAuthVM.newUser.value?.education = eduSpinner.selectedItem.toString()

                findNavController().navigate(
                    R.id.action_personalDetailsFragment_to_professionalDetails
                )
            } else {
                Snackbar.make(view, "Error: ${validatePersonalDetails()}", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun validatePersonalDetails(): String? {
        return when {
            createdForSpinner.selectedItemPosition == 0 -> {
                "Please choose the person you're creating the account for"
            }
            heightSpinner.selectedItemPosition == 0 -> {
                "Please select your height"
            }
            eduSpinner.selectedItemPosition == 0 -> {
                "Please select your highest education"
            }
            nameTextInp.editText?.text.toString().isBlank() -> {
                "Please enter your name properly"
            }
            nameTextInp.editText?.text.toString().length < 5 -> {
                "Name seems too short, please enter your full name"
            }
            userAuthVM.newUser.value?.age == null -> {
                "Please enter your date of birth"
            }
            userAge < 18 -> {
                "User must be atleast 18 years old"
            }
            else -> null
        }
    }
}
