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
import com.halalrishtey.models.Sect
import com.halalrishtey.viewmodels.UserAuthViewModel
import kotlinx.android.synthetic.main.fragment_professional_details.*

class ProfessionalDetailsFragment : Fragment() {
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
        val sectValues = arrayOf("Sunnat", "Tableeghee", "Hadees", "Shia", "Others")
        val sectAdapter: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                sectValues
            )

        sect_dropDown?.setAdapter(sectAdapter)

        val adhaarUploadUrl = arguments?.get("aadharImgUrl") as String?
        uploadAdhar_Button.setOnClickListener {
            findNavController().navigate(R.id.action_professionalDetails_to_uploadImageFragment)
        }

        pdRegisterButton.setOnClickListener {
            userAuthVM.newUser.value?.aadharPhotoUrl = adhaarUploadUrl ?: "Not Uploaded"
            userAuthVM.newUser.value?.heightFeet = hFt_editText.editText?.text.toString()
            userAuthVM.newUser.value?.heightInch = hIn_editText.editText?.text.toString()
            userAuthVM.newUser.value?.age =
                Integer.parseInt(age_editText.editText?.text.toString().trim())
            userAuthVM.newUser.value?.education =
                highestEdu_editText.editText?.text.toString()
            userAuthVM.newUser.value?.workExperience =
                workExp_editText.editText?.text.toString()
            userAuthVM.newUser.value?.occupation =
                profession_editText.editText?.text.toString()
            userAuthVM.newUser.value?.sect = Sect.valueOf(sect_dropDown.text.toString())


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

                    val i: Intent = Intent(
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
