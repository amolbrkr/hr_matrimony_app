package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.halalrishtey.models.User
import kotlinx.android.synthetic.main.fragment_personal_details.*

class PersonalDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val userData = arguments?.get("userInfo") as User

        val CreatedForStrings =
            arrayOf("Myself", "Relative", "Son", "Daughter", "Brother", "Sister")


        val createdForAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(
                requireContext(),
                R.layout.dropdown_menu_popup_item,
                CreatedForStrings
            )

        createdFor_dropDown.setAdapter(createdForAdapter)
    }
}
