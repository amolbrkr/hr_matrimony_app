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
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.adapter.CardDataRVAdapter
import com.halalrishtey.models.ProfileCardData
import com.halalrishtey.models.User
import com.halalrishtey.services.UserRepository
import com.halalrishtey.viewmodels.SearchViewModel
import com.halalrishtey.viewmodels.UserAuthViewModel
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.profile_card.view.*


class HomeFragment : Fragment() {
    private val userVM: UserViewModel by activityViewModels()
    private val userAuthVM: UserAuthViewModel by activityViewModels()
    private val searchVM: SearchViewModel by activityViewModels()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: CardDataRVAdapter
    private lateinit var usersToShow: ArrayList<ProfileCardData>
    private var lastScrollPos: Int = 0

    private fun genMessageBtnListener(
        currentUser: User,
        targetUser: User,
        v: View
    ): View.OnClickListener {
        return View.OnClickListener {
            val currentPlan = userVM.currentUser.value!!.currentPlan!!

            if (currentPlan.chatCount > 0) {
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
            } else {
                Snackbar.make(
                    v,
                    "You have used all your Messages from your current Plan.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

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
            userVM.currentUser.observe(viewLifecycleOwner, Observer { currentUser ->
                if (currentUser != null) {
                    UserRepository.getAllUserProfiles().observe(viewLifecycleOwner, Observer {
                        homeProgress.visibility = View.GONE
                        usersToShow.clear()
                        CustomUtils.filterValidProfiles(currentUser, it).map { user ->
                            val isShortlisted =
                                userVM.currentUser.value?.interestedProfiles?.contains(
                                    user.uid
                                ) ?: false
                            usersToShow.add(
                                ProfileCardData(
                                    data = user,
                                    showBtnInterestListener = genInterestBtnListener(
                                        isShortlisted,
                                        user.uid!!,
                                        requireView()
                                    ),
                                    messageBtnListener = genMessageBtnListener(
                                        currentUser,
                                        user,
                                        requireView()
                                    ),
                                    isUserShortlisted = isShortlisted
                                )
                            )
                        }
                        adapter.notifyDataSetChanged()
                    })
                }
            })

            searchVM.query.observe(viewLifecycleOwner, Observer {
                adapter.updateDataSet(searchVM.applySearchFilters(usersToShow))
            })

            searchVM.filters.observe(viewLifecycleOwner, Observer {
                adapter.updateDataSet(searchVM.applySearchFilters(usersToShow))
            })
        }

        userAuthVM.locationUpdates.observe(viewLifecycleOwner, Observer { loc ->
            val addr = if (loc != null) {
                CustomUtils.convertCoordsToAddr(requireContext(), loc.latitude, loc.longitude)
            } else null

            var addrLines = ""
            for (i in 0 until addr?.maxAddressLineIndex!!) {
                addrLines += addr.getAddressLine(i) + " "
            }

            val infoToBeUpdated: HashMap<String, Any?> = hashMapOf(
                "pincode" to addr.postalCode,
                "locationLat" to addr.latitude,
                "locationLong" to addr.longitude,
                "address" to addrLines,
                "lastSignInAt" to System.currentTimeMillis()
            )

            userAuthVM.updateUserData(userVM.currentUid.value!!, infoToBeUpdated)
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
