package com.halalrishtey

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.profile_options_sheet.view.*

class ProfileOptionsSheet(val user: User) : BottomSheetDialogFragment() {
    private val userVM: UserViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.profile_options_sheet, null)

        v.meetupOpt.setOnClickListener {
            val current = userVM.currentUser.value?.uid
            val target = user.uid
            if (current != null && target != null) {
                val i = Intent(context, ScheduleMeetupActivity::class.java)
                i.apply {
                    putExtra("currentId", current)
                    putExtra("targetId", target)
                }
                startActivity(i)
            } else Snackbar.make(requireView(), "Something went wrong!", Snackbar.LENGTH_SHORT)
                .show()
        }

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

        Log.d("ProfileOptions", "Current user: ${userVM.currentUser.value}")
        val isShortlisted =
            userVM.currentUser.value?.interestedProfiles?.contains(
                user.uid
            ) ?: false

        v.showInterestOpt.setOnClickListener {
            if (!isShortlisted) {
                v.showIntIcon.setImageResource(R.drawable.ic_favorite)
                userVM.initInterest(
                    userVM.currentUid.value!!,
                    userVM.currentUser.value?.displayName!!,
                    user.uid!!
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                v.showIntIcon.setImageResource(R.drawable.ic_favorite_border)
                userVM.removeInterest(
                    userVM.currentUid.value!!,
                    user.uid!!
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
        v.messageOpt.setOnClickListener {
            val currentUser = userVM.currentUser.value!!
            userVM.initConversation(userVM.currentUser.value!!, user)
                .observe(viewLifecycleOwner, Observer {
                    if (it.length > 1) {
                        val i = Intent(context, ChatActivity::class.java)
                        i.putExtra("conversationId", it)
                        i.putExtra("currentId", currentUser.uid)
                        i.putExtra("targetId", user.uid)
                        i.putExtra("targetPhotoUrl", user.photoUrl)
                        i.putExtra("targetName", user.displayName)
                        startActivity(i)
                    }
                })
        }
        val d = BottomSheetDialog(requireContext())
        d.setContentView(v)
        return d
    }
}
