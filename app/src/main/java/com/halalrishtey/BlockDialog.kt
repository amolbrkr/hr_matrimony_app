package com.halalrishtey

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_block_dialog.view.*

class BlockDialog(private val currentId: String, private val targetId: String) : DialogFragment() {
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
            LayoutInflater.from(requireContext()).inflate(R.layout.fragment_block_dialog, null)

        view.blockBtn.setOnClickListener {
            ChatActivity().blockUser(requireContext(), currentId, targetId)
        }

        return view
    }

}
