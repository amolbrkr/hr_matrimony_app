package com.halalrishtey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_image_item.view.*

class ProfileImagesAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ProfileImagesAdapter.PIVH>() {

    class PIVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PIVH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_image_item, parent, false)
        return PIVH(v)
    }

    override fun onBindViewHolder(holder: PIVH, position: Int) {
        Picasso.get().load(images[position]).resize(100, 100)
            .centerCrop()
            .into(holder.itemView.pImage)

        holder.itemView.deleteImageBtn.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Image #$position deleted!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getItemCount() = images.size
}