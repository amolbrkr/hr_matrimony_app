package com.makeshaadi

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeshaadi.adapter.SpinnerAdapters
import com.makeshaadi.viewmodels.SearchParams
import com.makeshaadi.viewmodels.SearchViewModel
import io.apptik.widget.MultiSlider.OnThumbValueChangeListener
import kotlinx.android.synthetic.main.search_filter_sheet.view.*


class SearchSheetDialog : BottomSheetDialogFragment() {
    private val searchVM: SearchViewModel by activityViewModels()

    //Defaults
    private var minAge: Int = 18
    private var maxAge: Int = 45
    private var minHeight: Int = 122
    private var maxHeight: Int = 227
    private var location: String = ""
    private var eduSpinnerVal: String = "Qualification"

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.search_filter_sheet, null)

        //Set default values as range for sliders & labels
        view.ageSlider.min = minAge
        view.ageSlider.max = maxAge
        view.heightSlider.min = minHeight
        view.heightSlider.max = maxHeight
        view.ageText.text = "Age - $minAge to $maxAge"
        view.heightText.text =
            "Height - ${CustomUtils.convertCmToFtIn(minHeight)} to ${CustomUtils.convertCmToFtIn(
                maxHeight
            )}"

        val filters = searchVM.filters.value
        if (filters != null) {
            view.ageSlider.getThumb(0).value = filters.minAge
            view.ageSlider.getThumb(1).value = filters.maxAge
            view.heightSlider.getThumb(0).value = filters.minHeight
            view.heightSlider.getThumb(1).value = filters.maxHeight
            location = filters.location
            eduSpinnerVal = filters.highestEdu

            view.ageText.text = "Age - ${filters.minAge} to ${filters.maxAge}"
            view.heightText.text =
                "Height - ${CustomUtils.convertCmToFtIn(filters.minHeight)} to ${CustomUtils.convertCmToFtIn(
                    filters.maxHeight
                )}"

        } else {
            view.heightSlider.repositionThumbs()
        }

        searchVM.appliedFilters.observe(requireActivity(), Observer {
            view.appliedText.text = "Applied: " + it
        })

        view.ageSlider.setOnThumbValueChangeListener(OnThumbValueChangeListener { _, _, thumbIndex, value ->
            if (thumbIndex == 0) {
                minAge = value
            } else {
                maxAge = value
            }
            view.ageText.text = "Age - $minAge to $maxAge"
        })

        view.heightSlider.setOnThumbValueChangeListener(OnThumbValueChangeListener { _, _, thumbIndex, value ->
            if (thumbIndex == 0) minHeight = value
            else maxHeight = value
            view.heightText.text =
                "Height - ${CustomUtils.convertCmToFtIn(minHeight)} to ${CustomUtils.convertCmToFtIn(
                    maxHeight
                )}"
        })

        view.searchLocInp.editText?.doOnTextChanged { text, _, count, _ ->
            if (count > 2)
                location = text.toString()
        }

        view.searchEduSpinner.adapter = SpinnerAdapters(requireContext()).highestEducationAdapter
        view.searchEduSpinner.setSelection(
            SpinnerAdapters(requireContext()).highestEducationAdapter.getPosition(
                eduSpinnerVal
            )
        )

        view.searchLocInp.editText?.setText(location)

        view.applyFilterBtn.setOnClickListener {
            searchVM.filters.value = SearchParams(
                minAge = minAge,
                maxAge = maxAge,
                minHeight = minHeight,
                maxHeight = maxHeight,
                location = location,
                highestEdu = view.searchEduSpinner.selectedItem.toString()
            )

            Log.d("SearchSheet", searchVM.filters.value.toString())
            this.dismiss()
        }

        view.clearFilterBtn.setOnClickListener {
            //TODO: Add proper clearing logic
            minAge = 18
            maxAge = 45
            minHeight = 122
            maxHeight = 227
            searchVM.query.value = ""
            searchVM.filters.value = null
            view.ageSlider.repositionThumbs()
            view.heightSlider.repositionThumbs()
            view.searchLocInp.editText?.setText("")
            view.searchEduSpinner.setSelection(0)
            Log.d("SearchDialog", "After clearing: ${searchVM.filters.value}")
        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        return dialog
    }
}