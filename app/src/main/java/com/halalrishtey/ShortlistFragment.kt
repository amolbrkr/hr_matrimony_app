package com.halalrishtey


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
        if (userVM.currentUserProfile.value?.interestedProfiles?.size == 0) {
            shortlistedProfilesRV.visibility = View.GONE
            shortlistBgImageView.visibility = View.VISIBLE
            shortlistHelperText.visibility = View.VISIBLE
        } else {
            shortlistedProfilesRV.visibility = View.VISIBLE
            shortlistBgImageView.visibility = View.GONE
            shortlistHelperText.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        userVM.currentUserProfile.observe(viewLifecycleOwner, Observer { currentUser ->
            userVM.getProfilesByIds(currentUser.interestedProfiles)
                .observe(viewLifecycleOwner, Observer {
                    it.forEach { u ->
                        val isShortlisted =
                            currentUser.interestedProfiles.contains(
                                u.uid
                            )

                        val interestBtnListener = View.OnClickListener { v ->
                            if (!isShortlisted) {
                                v.showInterestBtn.setIconResource(R.drawable.ic_favorite)

                                //Add interest target to correct arraylist
                                currentUser.interestedProfiles.add(u.uid!!)
                                userVM.currentUserProfile.value = currentUser

                                userVM.initInterest(
                                    userVM.currentUserId.value!!,
                                    u.uid!!
                                ).observe(viewLifecycleOwner, Observer { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                        .show()
                                })
                            } else {
                                v.showInterestBtn.setIconResource(R.drawable.ic_favorite_border)

                                //Remove interest target from correct arraylist
                                currentUser.interestedProfiles.remove(u.uid!!)
                                userVM.currentUserProfile.value = currentUser

                                userVM.removeInterest(
                                    userVM.currentUserId.value!!,
                                    u.uid!!
                                ).observe(viewLifecycleOwner, Observer { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                        .show()
                                })
                            }
                        }
                        val messageBtnListener = View.OnClickListener { v ->
                            Toast.makeText(
                                context,
                                "Message button clicked for ${u.displayName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        interestedProfiles.add(
                            ProfileCardData(
                                data = u,
                                showBtnInterestListener = interestBtnListener,
                                messageBtnListener = messageBtnListener,
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
