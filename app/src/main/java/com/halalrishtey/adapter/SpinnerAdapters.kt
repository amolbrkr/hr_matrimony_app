package com.halalrishtey.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.halalrishtey.R

class SpinnerAdapters(context: Context) {

    val dargahAdapter = ArrayAdapter<String>(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Dargah/Fateha",
            "Yes",
            "No",
            "Neutral"
        )
    )

    val sectAdapter = ArrayAdapter<String>(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Maslak/Sect",
            "Hanafi",
            "Maliki",
            "Shafa'i",
            "Hanbali"
        )
    )

    val maritalStatusAdapter = ArrayAdapter<String>(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Marital Status",
            "Never Married",
            "Divorced",
            "Widowed",
            "Annulled"
        )
    )

    val highestEducationAdapter = ArrayAdapter<String>(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Qualification",
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

    val createdForAdapter = ArrayAdapter(
        context,
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

    val heightAdapter = ArrayAdapter<String>(
        context,
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

}