package com.halalrishtey


import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.halalrishtey.services.UserRepository
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.profile_card.view.*


class HomeFragment : Fragment() {
    private val userVM: UserViewModel by activityViewModels()
    private val userAuthVM: UserAuthViewModel by activityViewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CardDataRVAdapter
    private lateinit var usersToShow: ArrayList<ProfileCardData>
    private var lastScrollPos: Int = 0

    fun genMessageBtnListener(
        currentUser: User,
        targetUser: User
    ): View.OnClickListener {
        return View.OnClickListener {
            //TODO: Properly implement this function
//            userVM.initConversation(currentUser, targetUser)
//                .observe(viewLifecycleOwner, Observer {
//                    Toast.makeText(context, "Result: $it", Toast.LENGTH_SHORT).show()
//                })
            val i = Intent(context, ChatActivity::class.java)
            startActivity(i)
        }
    }

    fun genInterestBtnListener(
        isShortlisted: Boolean,
        targetUserId: String,
        currentUser: User,
        v: View
    ): View.OnClickListener {
        return View.OnClickListener {
            if (!isShortlisted) {
                v.showInterestBtn.setIconResource(R.drawable.ic_favorite)
                userVM.initInterest(
                    userVM.currentUserId.value!!,
                    userVM.currentUserProfile.value?.displayName!!,
                    targetUserId
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                v.showInterestBtn.setIconResource(R.drawable.ic_favorite_border)
                userVM.removeInterest(
                    userVM.currentUserId.value!!,
                    targetUserId
                ).observe(viewLifecycleOwner, Observer { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                        .show()
                })
            }
        }
    }

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
                                    val isShortlisted =
                                        userVM.currentUserProfile.value?.interestedProfiles?.contains(
                                            user.uid
                                        ) ?: false
                                    Log.d(
                                        "HomeFragment",
                                        "User ${user.uid} is shortlisted by current user: $isShortlisted"
                                    )

                                    usersToShow.add(
                                        ProfileCardData(
                                            data = user,
                                            showBtnInterestListener = genInterestBtnListener(
                                                isShortlisted,
                                                user.uid!!,
                                                currentUser,
                                                requireView()
                                            ),
                                            messageBtnListener = genMessageBtnListener(
                                                currentUser,
                                                user
                                            ),
                                            isUserShortlisted = isShortlisted
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
                CustomUtils.convertCoordsToAddr(requireContext(), loc.latitude, loc.longitude)
            } else null

            val infoToBeUpdated: HashMap<String, Any?> = hashMapOf(
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
