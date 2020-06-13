package com.halalrishtey

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.halalrishtey.adapter.PlansAdapter
import com.halalrishtey.models.PlanItem
import com.halalrishtey.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.activity_plans.*

class PlansActivity : AppCompatActivity() {
    private val userVM: UserViewModel by viewModels()
    private lateinit var adapter: PlansAdapter
    private lateinit var plans: ArrayList<PlanItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plans)
        setSupportActionBar(plansToolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        plans = ArrayList()
        adapter = PlansAdapter(plans)
        plansRV.layoutManager = LinearLayoutManager(this);
        plansRV.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        userVM.observeUser(userVM.currentUid.value!!).observe(this, Observer {
            val plan = it.currentPlan

            pTitleText.text = "You are on: ${plan?.name}, which has remaining"
            val msgText = SpannableString("${plan?.chatCount} \n Chat Messages")
            msgText.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
            msgText.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryColor)),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            msgLimitText.text = msgText

            val dcText = SpannableString("${plan?.dcCount} \n Direct Contacts")
            dcText.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
            dcText.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryColor)),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            dcLimitText.text = dcText

            val mText = SpannableString("${plan?.meetupCount} \n Meetups")
            mText.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
            mText.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryColor)),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            meetupLimitText.text = mText
        })

        userVM.getAllPlans().observe(this, Observer {
            if (it.size > 0) {
                plans.clear()
                plans.addAll(it)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "No Plans to Show!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
