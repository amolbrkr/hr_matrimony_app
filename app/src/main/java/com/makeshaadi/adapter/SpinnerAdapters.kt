package com.makeshaadi.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.makeshaadi.R

class SpinnerAdapters(context: Context) {

    val numBroAdapter = ArrayAdapter(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Number of Brothers", "0", "1", "2", "3", "4", "5"
        )
    )

    val numSisAdapter = ArrayAdapter(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Number of Sisters", "0", "1", "2", "3", "4", "5"
        )
    )

    val incomeAdapter = ArrayAdapter(
        context,
        R.layout.dropdown_menu_popup_item,
        arrayOf(
            "Annual Income",
            "1 - 2 Lakh",
            "2 - 5 Lakh",
            "5 - 10 Lakh",
            "10 - 15 Lakh",
            "15 - 20 Lakh",
            "20 - 35 Lakh",
            "35 - 50 Lakh",
            "50 Lakh & Above"
        )
    )

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
            "Maslak / Sect",
            "Ahle Sunnatul Jama’at (Sunni)",
            "Tabligh Jama’at",
            "Ahle Hadees Jama’at",
            "Shia",
            "Others"
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
            "Pre School / SSC",
            "Under Graduate",
            "Graduate",
            "Post Graduate",
            "Professional Graduate",
            "Medical Professional Graduate",
            "Islamic Degree"
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