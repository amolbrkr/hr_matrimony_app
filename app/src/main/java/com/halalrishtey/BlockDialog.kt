package com.halalrishtey

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class BlockDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.fragment_block_dialog)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null!")
    }

}
