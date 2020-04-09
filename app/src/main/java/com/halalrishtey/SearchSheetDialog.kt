package com.halalrishtey

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar
import kotlinx.android.synthetic.main.search_filter_sheet.view.*

class SearchSheetDialog : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.search_filter_sheet, null)

        view.ageRangeSeeker.setOnRangeSeekBarChangeListener(object :
            RangeSeekBar.OnRangeSeekBarChangeListener {
            override fun onProgressChanged(p0: RangeSeekBar?, p1: Int, p2: Int, p3: Boolean) {
                view.ageText.text = "Age    ${18 + p1} - ${18 + p2}"
            }

            override fun onStartTrackingTouch(p0: RangeSeekBar?) {
            }

            override fun onStopTrackingTouch(p0: RangeSeekBar?) {
            }

        })

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(view)
        return dialog
    }
}