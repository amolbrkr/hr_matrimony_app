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
    private lateinit var shortlistedProfiles: ArrayList<ProfileCardData>
    private var lastScrollPos: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_shortlist, container, false)

        shortlistedProfiles = ArrayList()
        adapter = CardDataRVAdapter(shortlistedProfiles)
        linearLayoutManager = LinearLayoutManager(context)

        v.shortlistedProfilesRV.layoutManager = linearLayoutManager
        v.shortlistedProfilesRV.adapter = adapter

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (userVM.currentUserProfile.value?.shortlistedProfiles?.size == 0) {
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
        val t = userVM.currentUserProfile.value!!.shortlistedProfiles

        if (t.size > 0) {
            userVM.getProfilesByIds(t).observe(viewLifecycleOwner, Observer {
                it.forEach { u ->
                    shortlistedProfiles.add(
                        ProfileCardData(
                            title = u.displayName,
                            subTitle = "${u.age.toString()}, ${u.height}",
                            imageUrl = u.photoUrl,
                            userId = u.uid!!,
                            currentUserId = userVM.currentUserId.value!!
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            })
        }
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
