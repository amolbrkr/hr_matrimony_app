package com.halalrishtey.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.halalrishtey.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_layout.view.*

class DefaultImageAdapter(private val images: ArrayList<String>) :
    RecyclerView.Adapter<DefaultImageAdapter.ImageVH>() {

    class ImageVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.image_layout, parent, false)

        return ImageVH(v)
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        Picasso.get().load(images[position])
            .into(holder.itemView.defImg)
    }

    override fun getItemCount() = images.size
}