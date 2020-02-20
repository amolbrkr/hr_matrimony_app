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
import com.halalrishtey.services.UserRepository
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
    private val userVM: UserViewModel by activityViewModels()
    private val userAuthVM: UserAuthViewModel by activityViewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CardDataRVAdapter
    private lateinit var usersToShow: ArrayList<ProfileCardData>
    private var lastScrollPos: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        usersToShow = ArrayList()
        adapter = CardDataRVAdapter(usersToShow)
        linearLayoutManager = LinearLayoutManager(context)

        view.profileRV.layoutManager = linearLayoutManager
        view.profileRV.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()

        if (usersToShow.size == 0) {
            userVM.getUser(userVM.currentUserId.value!!)
                .observe(viewLifecycleOwner, Observer { currentUser ->
                    if (currentUser != null) {
                        UserRepository.getAllUserProfiles().observe(viewLifecycleOwner, Observer {
                            usersToShow.clear()
                            it.forEach { user ->
                                if (user.uid != currentUser.uid && user.gender != currentUser.gender) {

                                    usersToShow.add(
                                        ProfileCardData(
                                            user.displayName,
                                            "${user.age.toString()}, ${user.height}",
                                            user.photoUrl
                                        )
                                    )
                                }
                            }
                            adapter.notifyDataSetChanged()
                        })
                    }
                })
        }

        userAuthVM.locationUpdates.observe(viewLifecycleOwner, Observer { loc ->
            val addr = if (loc != null) {
                CustomUtils.convertCoordsToAddr(requireContext(), loc.latitude, loc.longitude);
            } else null

            val infoToBeUpdated = hashMapOf(
                "pincode" to addr?.postalCode,
                "locationLat" to addr?.latitude,
                "locationLong" to addr?.longitude,
                "address" to addr?.getAddressLine(0),
                "lastSignInAt" to System.currentTimeMillis()
            )

            userAuthVM.updateUserData(userVM.currentUserId.value!!, infoToBeUpdated)
            userAuthVM.locationUpdates.removeObservers(viewLifecycleOwner)
        })

    }

    override fun onPause() {
        super.onPause()

        lastScrollPos = linearLayoutManager.findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()

        profileRV.layoutManager?.scrollToPosition(lastScrollPos)
    }
}
