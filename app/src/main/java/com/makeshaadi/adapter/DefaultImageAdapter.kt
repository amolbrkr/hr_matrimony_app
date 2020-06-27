package com.makeshaadi.adapter

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexvasilkov.gestures.Settings
import com.makeshaadi.R
import com.makeshaadi.UserImagesActivity
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
        holder.itemView.defImg.controller.settings
            .setMaxZoom(2f)
            .setDoubleTapZoom(-1f) // Falls back to max zoom level
            .setPanEnabled(true)
            .setZoomEnabled(true)
            .setDoubleTapEnabled(true)
            .setRotationEnabled(false)
            .setRestrictRotation(false)
            .setOverscrollDistance(0f, 0f)
            .setOverzoomFactor(2f)
            .setFillViewport(true) //Changed
            .setFitMethod(Settings.Fit.INSIDE).gravity = Gravity.CENTER


        if (images[position].length > 10) {
            Picasso.get().load(images[position])
                .into(holder.itemView.defImg)
        }

        holder.itemView.defImg.setOnClickListener {
            val i = Intent(it.context, UserImagesActivity::class.java)
            i.apply {
                putExtra("images", images)
                putExtra("pos", position)
            }
            it.context.startActivity(i)
        }
    }

    override fun getItemCount() = images.size
}