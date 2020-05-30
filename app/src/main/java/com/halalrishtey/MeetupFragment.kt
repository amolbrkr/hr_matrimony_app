package com.halalrishtey


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.MeetupAdapter
import com.halalrishtey.models.MeetupItem
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_meetup.view.*

class MeetupFragment : Fragment() {
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var adapter: MeetupAdapter
    private lateinit var meetups: ArrayList<MeetupItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_meetup, container, false)
        meetups = ArrayList()
        adapter = MeetupAdapter(meetups)
        v.meetupRV.layoutManager = LinearLayoutManager(context)
        v.meetupRV.adapter = adapter
        return v
    }

    override fun onStart() {
        super.onStart()

        userVM.currentUser.observe(viewLifecycleOwner, Observer { u ->
            userVM.getMeetupsFromIds(u.meetupList).observe(viewLifecycleOwner, Observer {
                meetups.clear()
                meetups.addAll(it)
                adapter.notifyDataSetChanged()
            })
        })
    }
}
