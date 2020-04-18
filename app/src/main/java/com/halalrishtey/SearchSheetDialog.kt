package com.halalrishtey

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halalrishtey.adapter.SpinnerAdapters
import com.halalrishtey.viewmodels.SearchParams
import com.halalrishtey.viewmodels.SearchViewModel
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar
import kotlinx.android.synthetic.main.search_filter_sheet.view.*
import java.text.DecimalFormat

class SearchSheetDialog : BottomSheetDialogFragment() {
    private val searchVM: SearchViewModel by activityViewModels()

    private var minAge: Int = 0
    private var maxAge: Int = 27
    private var minHeight: Int = 0
    private var maxHeight: Int = 107
    private var location: String = ""
    private var eduSpinnerVal: String = "Qualification"

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.search_filter_sheet, null)

        searchVM.filters.value?.let {
            minAge = it.minAge
            maxAge = it.maxAge
            minHeight = it.minHeight
            maxHeight = it.maxHeight
            location = it.location
            eduSpinnerVal = it.highestEdu
        }

        view.ageText.text = "Age - ${minAge + 18} to ${maxAge + 18}"
        val df = DecimalFormat("0.00")

        view.heightText.text =
            "Height - ${df.format(minHeight / 30.48)} feet to ${df.format(maxHeight / 30.48)} feet"

        view.ageRangeSeeker.setProgress(minAge, maxAge)
        view.ageRangeSeeker.setOnRangeSeekBarChangeListener(object :
            RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(p0: RangeSeekBar?, p1: Int, p2: Int, p3: Boolean) {
                minAge = p1 + 18
                maxAge = p2 + 18

                view.ageText.text = "Age - $minAge to $maxAge"
            }

            override fun onStartTrackingTouch(p0: RangeSeekBar?) {}
            override fun onStopTrackingTouch(p0: RangeSeekBar?) {}
        })

        view.heightRangeSeeker.setProgress(minHeight, maxHeight)
        view.heightRangeSeeker.setOnRangeSeekBarChangeListener(object :
            RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(p0: RangeSeekBar?, p1: Int, p2: Int, p3: Boolean) {
                minHeight = 137 + p1
                maxHeight = 137 + p2

                view.heightText.text =
                    "Height - ${Math.round(minHeight / 30.48 * 100) / 100} feet to ${Math.round(
                        maxHeight / 30.48 * 100
                    ) / 100} feet"
            }

            override fun onStartTrackingTouch(p0: RangeSeekBar?) {}
            override fun onStopTrackingTouch(p0: RangeSeekBar?) {}
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
            view.searchLocInp.editText?.setText("")
            view.ageRangeSeeker.setProgress(0, 27)
            view.heightRangeSeeker.setProgress(0, 107)
            view.searchEduSpinner.setSelection(0)
        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        return dialog
    }
}