package com.makeshaadi.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.makeshaadi.CustomUtils
import com.makeshaadi.MeetupDetailActivity
import com.makeshaadi.R
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.MeetupStatus
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_meetup_item.view.*

class MeetupAdapter(
    private val currentId: String,
    private val meetups: ArrayList<MeetupItem>,
    private val cancelCb: (meetupId: String) -> Unit,
    private val reschedCb: (currentId: String, targetId: String, meetupId: String) -> Unit
) : RecyclerView.Adapter<MeetupAdapter.MeetupVH>() {

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

        val mTarget = if (meetups[position].sourceId == currentId)
            meetups[position].targetName
        else meetups[position].sourceName

        v.mTitleText.text = SpannableStringBuilder().append("Meetup with ")
            .bold { append(mTarget) }.append(" on $mDate")

        v.cancelMeetupBtn.setOnClickListener {
            val d = AlertDialog.Builder(v.context)
            d.setTitle("Confirm cancellation")
                .setMessage("Are you sure you want to cancel this meetup?")
                .setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->
                    cancelCb(meetups[position].meetupId)
                    meetups[position].status = MeetupStatus.Cancelled
                    meetups.removeAt(position)
                    notifyDataSetChanged()
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            d.create().show()
        }

        v.reschedBtn.setOnClickListener {
            reschedCb(
                meetups[position].sourceId,
                meetups[position].targetId,
                meetups[position].meetupId
            )
        }
    }

    override fun getItemCount() = meetups.size
}