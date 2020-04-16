package com.halalrishtey

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.profile_options_sheet.view.*

class ProfileOptionsSheet(val user: User) : BottomSheetDialogFragment() {
    private val userVM: UserViewModel by activityViewModels()

    private fun genInterestBtnListener(
        isShortlisted: Boolean,
        targetUserId: String,
        v: View
    ): View.OnClickListener {
        return View.OnClickListener {
            if (!isShortlisted) {
                v.showIntIcon.setImageResource(R.drawable.ic_favorite)
                userVM.initInterest(
                    userVM.currentUid.value!!,
                    userVM.currentUser.value?.displayName!!,
                    targetUserId
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                v.showIntIcon.setImageResource(R.drawable.ic_favorite_border)
                userVM.removeInterest(
                    userVM.currentUid.value!!,
                    targetUserId
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
    }

    private fun genMessageBtnListener(
        currentUser: User,
        targetUser: User
    ): View.OnClickListener {
        return View.OnClickListener {
            userVM.initConversation(currentUser, targetUser)
                .observe(viewLifecycleOwner, Observer {
                    if (it.length > 1) {
                        val i = Intent(context, ChatActivity::class.java)
                        i.putExtra("conversationId", it)
                        i.putExtra("currentId", currentUser.uid)
                        i.putExtra("targetId", targetUser.uid)
                        i.putExtra("targetPhotoUrl", targetUser.photoUrl)
                        i.putExtra("targetName", targetUser.displayName)
                        startActivity(i)
                    }
                })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.profile_options_sheet, null)

        v.shareProfileOpt.setOnClickListener {

            val i = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Name: ${user.displayName}\n" +
                            "Age: ${user.age}\n" +
                            "Height: ${user.height}\n" +
                            "Income: ${user.annualIncome} PA\n" +
                            "Check out ${user.displayName}'s profile at Halal Rishtey app! \n\n Get the app now from #"
                )
                type = "text/plain"
            }
            startActivity(i)
        }

        v.dirContactOpt.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${user.phoneNumber}")
            startActivity(intent)
        }

        val isShortlisted =
            userVM.currentUser.value?.interestedProfiles?.contains(
                user.uid
            ) ?: false

        v.showInterestOpt.setOnClickListener(genInterestBtnListener(isShortlisted, user.uid!!, v))

        v.messageOpt.setOnClickListener(genMessageBtnListener(userVM.currentUser.value!!, user))

        val d = BottomSheetDialog(requireContext())
        d.setContentView(v)
        return d
    }
}
