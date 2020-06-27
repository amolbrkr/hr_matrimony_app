package com.makeshaadi


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_welcome_page.*


class WelcomePageFragment : Fragment() {
    private val ARG_RES_ID = "image_res_id"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_RES_ID) }?.apply {
            welcome_page_imageView.setImageResource(getInt(ARG_RES_ID))
            welcome_page_imageView.contentDescription = getString("imgDesc")
        }
    }
}
