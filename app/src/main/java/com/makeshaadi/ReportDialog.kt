package com.makeshaadi

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.makeshaadi.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_report_dialog.view.*

class ReportDialog(private val currentId: String, private val targetId: String) : DialogFragment() {
    private val userVM: UserViewModel by activityViewModels()

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

        view.reportOnlyBtn.setOnClickListener {
            userVM.reportUser(currentId, targetId, view.reportSpinner.selectedItem.toString())
            activity?.onBackPressed()
        }

        view.reportAndBlockBtn.setOnClickListener {
            userVM.reportUser(currentId, targetId, view.reportSpinner.selectedItem.toString())
            userVM.blockUser(currentId, targetId).observe(this, Observer {
                if (!it.contains("Successfully"))
                    Toast.makeText(view.context, "Error: $it", Toast.LENGTH_SHORT).show()
                this.activity?.finish()
            })
        }

        return view
    }
}
