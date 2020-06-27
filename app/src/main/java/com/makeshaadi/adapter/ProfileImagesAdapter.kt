package com.makeshaadi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.makeshaadi.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.profile_image_item.view.*
import kotlin.reflect.KFunction1

class ProfileImagesAdapter(
    private val images: ArrayList<String>,
    private val deleteCallback: KFunction1<String, Unit>
) :
    RecyclerView.Adapter<ProfileImagesAdapter.pImageVH>() {

    class pImageVH(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): pImageVH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_image_item, parent, false)
        return pImageVH(v)
    }

    override fun onBindViewHolder(holder: pImageVH, position: Int) {
        if (images[position].length > 10) {
            Picasso.get().load(images[position]).resize(100, 100)
                .centerCrop()
                .into(holder.itemView.pImage)
        }

        holder.itemView.deleteImageBtn.setOnClickListener {
            deleteCallback(images[position])
            images.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = images.size
}