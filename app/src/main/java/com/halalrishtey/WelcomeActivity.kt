package com.halalrishtey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        welcome_viewPager.adapter = WelcomeViewPagerAdapter(supportFragmentManager, lifecycle)
    }
}

private const val ARG_RES_ID = "image_res_id"
private val imageResources =
    listOf(R.mipmap.welcome_0, R.mipmap.welcome_1, R.mipmap.welcome_2, R.mipmap.welcome_3)

class WelcomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = WelcomePageFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_RES_ID, imageResources[position])
        }

        return fragment
    }
}
