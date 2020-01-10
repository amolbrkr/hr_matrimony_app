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
import com.halalrishtey.models.CreatedFor
import com.halalrishtey.models.Gender
import com.halalrishtey.viewmodels.SharedViewModel
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_personal_details.*
import java.util.*


class PersonalDetailsFragment : Fragment() {
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private val sharedVM: SharedViewModel by activityViewModels()
    private var savedViewInstance: View? = null

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
        sharedVM.bundleFromUploadImageFragment.observe(viewLifecycleOwner, androidx.lifecycle.Observer { bundle ->
            userAuthVM.newUser.value?.photoUrl = bundle.getString("uploadedImageUrl", "Not Uploaded")
        })

        val createdForStrings =
                arrayOf("Myself", "Relative", "Son", "Daughter", "Brother", "Sister")

        val createdForAdapter: ArrayAdapter<String> =
                ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_menu_popup_item,
                        createdForStrings
                )

        createdFor_dropDown.setAdapter(createdForAdapter)
        val cal: Calendar = Calendar.getInstance()


        val datePickerDialog =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val date = "$dayOfMonth / ${monthOfYear + 1} / $year"
                    userAuthVM.newUser.value?.dateOfBirth =
                            GregorianCalendar(year, monthOfYear + 1, dayOfMonth).time
                    dob_button.text = date
                }

        dob_button.setOnClickListener {
            DatePickerDialog(
                    requireContext(), datePickerDialog,
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        profileImageView.setOnClickListener {
            findNavController().navigate(R.id.action_personalDetailsFragment_to_uploadImageFragment)
        }

        pdNextButton.setOnClickListener {
            userAuthVM.newUser.value?.displayName =
                    name_editText.editText?.text.toString()

            userAuthVM.newUser.value?.phoneNumber =
                    phone_editText.editText?.text.toString().toLong()

            userAuthVM.newUser.value?.createdFor =
                    CreatedFor.valueOf(createdFor_dropDown.text.toString())

            userAuthVM.newUser.value?.pinCode =
                    Integer.parseInt(pincode_editText.editText?.text.toString())

            userAuthVM.newUser.value?.gender = Gender.valueOf(
                    if (maleSelector.isChecked) {
                        "Male"
                    } else {
                        "Female"
                    }
            )
            findNavController().navigate(
                    R.id.action_personalDetailsFragment_to_professionalDetails
            )
        }
    }
}
