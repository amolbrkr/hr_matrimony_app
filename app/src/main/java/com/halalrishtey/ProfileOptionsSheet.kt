package com.halalrishtey

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.profile_options_sheet.view.*

class ProfileOptionsSheet(val user: User) : BottomSheetDialogFragment() {
    private val userVM: UserViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = layoutInflater.inflate(R.layout.profile_options_sheet, null)

        v.shareProfileOpt.setOnClickListener {

            val i = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out ${user.displayName}'s profile on Halal Rishtey App!\n\n Get the app now from #"
                )
                type = "text/plain"
            }
            startActivity(i)
        }

        val d = BottomSheetDialog(requireContext())
        d.setContentView(v)
        return d
    }
}
