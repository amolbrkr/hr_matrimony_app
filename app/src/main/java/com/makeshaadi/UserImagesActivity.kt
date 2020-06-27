package com.makeshaadi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.makeshaadi.adapter.DefaultImageAdapter
import kotlinx.android.synthetic.main.activity_user_images.*

class UserImagesActivity() : AppCompatActivity() {
    private lateinit var adapter: DefaultImageAdapter
    private var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_images)

        val images = intent.getStringArrayListExtra("images")!!
        pos = intent.getIntExtra("pos", 0)

        adapter = DefaultImageAdapter(images)
        userImagesRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userImagesRV.adapter = adapter

        backBtn.setOnClickListener {
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        userImagesRV.layoutManager?.scrollToPosition(pos)
    }
}
