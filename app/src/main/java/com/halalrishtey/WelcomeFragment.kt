package com.halalrishtey


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        welcome_viewPager.adapter = WelcomeViewPagerAdapter(this)

        getStartedButton.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }
}


private const val ARG_RES_ID = "image_res_id"
private val imageResources =
    listOf(R.mipmap.welcome_0, R.mipmap.welcome_1, R.mipmap.welcome_2, R.mipmap.welcome_3)

class WelcomeViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = WelcomePageFragment()
        fragment.arguments = Bundle().apply {
            putInt(ARG_RES_ID, imageResources[position])
        }

        return fragment
    }
}