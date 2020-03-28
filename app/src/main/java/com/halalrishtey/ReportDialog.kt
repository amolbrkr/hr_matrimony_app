package com.halalrishtey

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_report_dialog.view.*

class ReportDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(getDialogView())
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null!")
    }

    private fun getDialogView(): View {
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.fragment_report_dialog, null)
        view.reportSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            arrayOf(
                "Fake Profile",
                "Abusing / Harassing",
                "Asking for Dowry",
                "Other"
            )
        )
        view.reportSpinner.setSelection(0)
        return view
    }
}
