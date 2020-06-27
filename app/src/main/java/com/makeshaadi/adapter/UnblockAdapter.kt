package com.makeshaadi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeshaadi.R
import com.makeshaadi.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.blocked_profile.view.*

class UnblockAdapter(
    private val blockedUsers: ArrayList<User>,
    private val ubUserBtnCb: (target: String) -> Unit
) :
    RecyclerView.Adapter<UnblockAdapter.UnblockVH>() {

    class UnblockVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnblockVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.blocked_profile, parent, false)

        return UnblockVH(view)
    }

    override fun onBindViewHolder(holder: UnblockVH, position: Int) {
        holder.itemView.apply {
            if (blockedUsers[position].photoUrl.length > 10) {
                Picasso.get().load(blockedUsers[position].photoUrl).into(bpThumb)
            }

            bpThumb.contentDescription = "${blockedUsers[position].displayName}'s Profile Image"

            bpNameText.text = blockedUsers[position].displayName
            bpUnblockBtn.setOnClickListener {
                ubUserBtnCb(blockedUsers[position].uid!!)
                blockedUsers.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun getItemCount() = blockedUsers.size;
}