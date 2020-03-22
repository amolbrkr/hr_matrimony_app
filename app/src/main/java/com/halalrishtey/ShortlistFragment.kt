package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.CardDataRVAdapter
import com.halalrishtey.models.ProfileCardData
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_shortlist.*
import kotlinx.android.synthetic.main.fragment_shortlist.view.*


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
                    it.forEach { user ->
                        val isShortlisted =
                            currentUser.interestedProfiles.contains(
                                user.uid
                            )

                        interestedProfiles.add(
                            ProfileCardData(
                                data = user,
                                showBtnInterestListener = HomeFragment().genInterestBtnListener(
                                    isShortlisted,
                                    user.uid!!,
                                    currentUser,
                                    view!!
                                ),
                                messageBtnListener = HomeFragment().genMessageBtnListener(
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
