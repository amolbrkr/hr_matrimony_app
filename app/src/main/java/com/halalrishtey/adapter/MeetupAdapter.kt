package com.halalrishtey.adapter

import android.content.Intent
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.CustomUtils
import com.halalrishtey.MeetupDetailActivity
import com.halalrishtey.R
import com.halalrishtey.models.MeetupItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_meetup_item.view.*

class MeetupAdapter(private val meetups: ArrayList<MeetupItem>) :
    RecyclerView.Adapter<MeetupAdapter.MeetupVH>() {

    class MeetupVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetupVH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_meetup_item, parent, false)

        return MeetupVH(v)
    }

    override fun onBindViewHolder(holder: MeetupVH, position: Int) {
        val v = holder.itemView
        v.setOnClickListener {
            val i = Intent(v.context, MeetupDetailActivity::class.java)
            i.putExtra("meetupData", meetups[position])
            v.context.startActivity(i)
        }
        if (meetups[position].isApproved) {
            v.apprBadge.visibility = View.VISIBLE
            v.mApprText.text = "Meetup Approved!"
        } else {
            v.apprBadge.visibility = View.GONE
        }

        if (meetups[position].sourcePhoto.length > 5) Picasso.get()
            .load(meetups[position].sourcePhoto).into(v.mUserImg1)

        if (meetups[position].targetPhoto.length > 5) Picasso.get()
            .load(meetups[position].targetPhoto).into(v.mUserImg2)

        val mDate = CustomUtils.genDateString(meetups[position].date)

        v.mLocText.text = meetups[position].address
        v.mTitleText.text = SpannableStringBuilder().append("Meetup with ")
            .bold { append(meetups[position].targetName) }.append(" on $mDate")
    }

    override fun getItemCount() = meetups.size
}