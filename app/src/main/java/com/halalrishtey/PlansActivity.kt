package com.halalrishtey

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.halalrishtey.adapter.PlansAdapter
import com.halalrishtey.models.PlanItem
import com.halalrishtey.viewmodels.UserViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_plans.*
import org.json.JSONObject


class PlansActivity : AppCompatActivity(), PaymentResultListener {
    private val userVM: UserViewModel by viewModels()
    private lateinit var adapter: PlansAdapter
    private lateinit var plans: ArrayList<PlanItem>
    private lateinit var checkout: Checkout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plans)
        setSupportActionBar(plansToolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        plans = ArrayList()
        adapter = PlansAdapter(plans, userVM.currentUid.value!!) { opts -> startPayment(opts) }
        plansRV.layoutManager = LinearLayoutManager(this);
        plansRV.adapter = adapter

        //Payment Gateway
        checkout = Checkout()
        checkout.setKeyID("rzp_test_dvq5JlDApXAZWu")
        Checkout.preload(applicationContext)
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

    //p0: Error code, p1: Error string
    override fun onPaymentError(p0: Int, p1: String?) {
        val snack = Snackbar.make(
            window.decorView.findViewById(android.R.id.content),
            "Payment Error: $p0, $p1", Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("OK") { snack.dismiss() }
    }

    //p0: Payment Id
    override fun onPaymentSuccess(p0: String?) {
        val snack = Snackbar.make(
            window.decorView.findViewById(android.R.id.content),
            "Thank you for choosing us! Your plan will be updated in a minute",
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("OK") { snack.dismiss() }
    }

    fun startPayment(options: JSONObject) {
//        checkout.setImage(R.drawable.ic_star) //For Logo
        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("PlansActivity", "Error in starting Razorpay Checkout", e)
        }
    }
}
