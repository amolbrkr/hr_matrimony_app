package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.CardDataRVAdapter
import com.halalrishtey.models.ProfileCardData
import com.halalrishtey.services.UserRepository
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
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
//        UserRepository.getAllUserProfiles().value?.forEach {
//            usersToShow.add(ProfileCardData(it.displayName, it.age.toString(), it.idProofUrl))
//        }

        adapter = CardDataRVAdapter(usersToShow)
        linearLayoutManager = LinearLayoutManager(context)

        view.profileRV.layoutManager = linearLayoutManager
        view.profileRV.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        if (usersToShow.size == 0) {
            UserRepository.getAllUserProfiles().observe(viewLifecycleOwner, Observer {
                it.forEach { user ->
                    //TODO: Pass proper values here.
                    usersToShow.clear()
                    usersToShow.add(
                        ProfileCardData(
                            user.displayName,
                            user.age.toString(),
                            user.idProofUrl
                        )
                    )
                }
            })
        }
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
