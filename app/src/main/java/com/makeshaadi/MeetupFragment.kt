package com.makeshaadi


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
import com.makeshaadi.adapter.MeetupAdapter
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.MeetupStatus
import com.makeshaadi.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.fragment_meetup.*
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
        adapter = MeetupAdapter(
            userVM.currentUid.value!!,
            meetups,
            { id -> cancelMeetup(id) },
            { current, target, id -> reschedMeetup(current, target, id) })
        v.meetupRV.layoutManager = LinearLayoutManager(context)
        v.meetupRV.adapter = adapter
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (userVM.currentUser.value?.meetupList?.size == 0) {
            mHelperText.visibility = View.VISIBLE
            mBgImgView.visibility = View.VISIBLE
            meetupRV.visibility = View.GONE
        } else {
            mHelperText.visibility = View.GONE
            mBgImgView.visibility = View.GONE
            meetupRV.visibility = View.VISIBLE
            meetupProgress.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        userVM.currentUser.observe(viewLifecycleOwner, Observer { u ->
            meetupProgress.visibility = View.GONE
            userVM.getMeetupsFromIds(u.meetupList).observe(viewLifecycleOwner, Observer {
                meetups.clear()
                val t = it.filter { meetup -> meetup.status == MeetupStatus.Scheduled }
                if (t.isEmpty()) {
                    mHelperText.visibility = View.VISIBLE
                    mBgImgView.visibility = View.VISIBLE
                } else {
                    meetups.addAll(t)
                    adapter.notifyDataSetChanged()
                    mHelperText.visibility = View.GONE
                    mBgImgView.visibility = View.GONE
                }
            })
        })
    }

    private fun cancelMeetup(id: String) {
        userVM.updateMeetupStatus(id, MeetupStatus.Cancelled)
            .observe(requireActivity(), Observer {
                if (!it.isNullOrBlank()) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            })

    }

    private fun reschedMeetup(current: String, target: String, id: String) {
        val i = Intent(context, ScheduleMeetupActivity::class.java)
        i.apply {
            putExtra("mode", "update")
            putExtra("currentId", current)
            putExtra("targetId", target)
            putExtra("meetupId", id)
        }
        startActivity(i)
    }
}
