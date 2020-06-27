package com.makeshaadi

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.makeshaadi.viewmodels.UserViewModel
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
            userVM.blockUser(currentId, targetId).observe(this, Observer {
                if (!it.contains("Successfully"))
                    Toast.makeText(view.context, "Error: $it", Toast.LENGTH_SHORT).show()
                else
                    Snackbar.make(view, "You have blocked a user!", Snackbar.LENGTH_SHORT).show()
                this.activity?.finish()
            })
        }

        return view
    }

}
