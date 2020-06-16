package com.halalrishtey

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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
        val currentPlan = userVM.currentUser.value!!.currentPlan!!

        v.meetupOpt.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(activity)
            dialogBuilder.setIcon(R.drawable.heart)
            dialogBuilder.setMessage(R.string.meetup_tnc_content)

            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                this.dismiss()
            })

            dialogBuilder.setPositiveButton(
                "Agree"
            ) { dialogInterface: DialogInterface, i: Int ->
                val current = userVM.currentUser.value?.uid
                val target = user.uid

                if (currentPlan != null && currentPlan.meetupCount > 0) {
                    if (current != null && target != null) {
                        userVM.decMeetupCount(current)
                        val i = Intent(context, ScheduleMeetupActivity::class.java)
                        i.apply {
                            putExtra("currentId", current)
                            putExtra("targetId", target)
                        }
                        startActivity(i)
                    } else Snackbar.make(
                        requireView(),
                        "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "You have used all your Meetups from your current Plan.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
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

            if (currentPlan.dcCount > 0) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${user.phoneNumber}")
                startActivity(intent)
            } else {
                Snackbar.make(
                    v,
                    "You have used all your Direct Contacts from your current Plan.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
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
                ).observe(requireActivity(), Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                v.showIntIcon.setImageResource(R.drawable.ic_favorite_border)
                userVM.removeInterest(
                    userVM.currentUid.value!!,
                    user.uid!!
                ).observe(requireActivity(), Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
        v.messageOpt.setOnClickListener {
            val currentUser = userVM.currentUser.value!!

            if (currentPlan.chatCount > 0) {
                userVM.initConversation(userVM.currentUser.value!!, user)
                    .observe(requireActivity(), Observer {
                        //Reduce message count by 1
                        userVM.decMessageCount(currentUser.uid!!)
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
            } else {
                Snackbar.make(
                    v,
                    "You have used all your Messages from your current Plan.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        val d = BottomSheetDialog(requireContext())
        d.setContentView(v)
        return d
    }
}
