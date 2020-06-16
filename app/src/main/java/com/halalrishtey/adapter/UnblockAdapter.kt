package com.halalrishtey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.halalrishtey.models.User
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
            bpNameText.text = blockedUsers[position].displayName
            bpUnblockBtn.setOnClickListener {
                ubUserBtnCb(blockedUsers[position].uid!!)
            }
        }
    }

    override fun getItemCount() = blockedUsers.size;
}