package com.halalrishtey


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.CardDataRVAdapter
import com.halalrishtey.models.ProfileCardData
import com.halalrishtey.models.User
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_shortlist.*
import kotlinx.android.synthetic.main.fragment_shortlist.view.*
import kotlinx.android.synthetic.main.profile_card.view.*


class ShortlistFragment : Fragment() {
    private val userVM: UserViewModel by activityViewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CardDataRVAdapter
    private lateinit var interestedProfiles: ArrayList<ProfileCardData>
    private var lastScrollPos: Int = 0

    private fun genInterestBtnListener(
        isShortlisted: Boolean,
        targetUserId: String,
        v: View
    ): View.OnClickListener {
        return View.OnClickListener {
            if (!isShortlisted) {
                v.showInterestBtn.setIconResource(R.drawable.ic_favorite)
                userVM.initInterest(
                    userVM.currentUid.value!!,
                    userVM.currentUser.value?.displayName!!,
                    targetUserId
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                v.showInterestBtn.setIconResource(R.drawable.ic_favorite_border)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_shortlist, container, false)

        interestedProfiles = ArrayList()
        adapter = CardDataRVAdapter(interestedProfiles)
        linearLayoutManager = LinearLayoutManager(context)

        v.shortlistedProfilesRV.layoutManager = linearLayoutManager
        v.shortlistedProfilesRV.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (userVM.currentUser.value?.interestedProfiles?.size == 0) {
            shortlistedProfilesRV.visibility = View.GONE
            shortlistBgImageView.visibility = View.VISIBLE
            shortlistHelperText.visibility = View.VISIBLE
        } else {
            shortlistedProfilesRV.visibility = View.VISIBLE
            shortlistBgImageView.visibility = View.GONE
            shortlistHelperText.visibility = View.GONE
            interestProgress.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        userVM.currentUser.observe(viewLifecycleOwner, Observer { currentUser ->
            userVM.getProfilesByIds(currentUser.interestedProfiles)
                .observe(viewLifecycleOwner, Observer {
                    interestProgress.visibility = View.GONE
                    it.forEach { user ->
                        val isShortlisted =
                            currentUser.interestedProfiles.contains(
                                user.uid
                            )

                        interestedProfiles.add(
                            ProfileCardData(
                                data = user,
                                showBtnInterestListener = genInterestBtnListener(
                                    isShortlisted,
                                    user.uid!!,
                                    view!!
                                ),
                                messageBtnListener = genMessageBtnListener(
                                    currentUser,
                                    user
                                ),
                                isUserShortlisted = isShortlisted
                            )
                        )
                        adapter.notifyDataSetChanged()
                    }
                })
        })
    }

    override fun onPause() {
        super.onPause()

        lastScrollPos = linearLayoutManager.findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()

        shortlistedProfilesRV.layoutManager?.scrollToPosition(lastScrollPos)
    }
}
