package com.makeshaadi

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
import com.makeshaadi.adapter.PlansAdapter
import com.makeshaadi.models.PlanItem
import com.makeshaadi.viewmodels.UserViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_plans.*
import org.json.JSONObject


class PlansActivity : AppCompatActivity(), PaymentResultListener {
    private val userVM: UserViewModel by viewModels()
    private lateinit var adapter: PlansAdapter
    private lateinit var plans: ArrayList<PlanItem>
    private lateinit var checkout: Checkout
    private lateinit var selectedPlan: PlanItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plans)
        setSupportActionBar(plansToolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        plans = ArrayList()
        adapter = PlansAdapter(plans, userVM.currentUid.value!!) { plan, opts ->
            selectedPlan = plan
            startPayment(opts)
        }
        plansRV.layoutManager = LinearLayoutManager(this);
        plansRV.adapter = adapter

        //Payment Gateway
        checkout = Checkout()
        checkout.setKeyID("rzp_live_KNKY3eOWZH3p4g")
        Checkout.preload(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        userVM.observeUser(userVM.currentUid.value!!).observe(this, Observer {
            val plan = it.currentPlan

            pTitleText.text = plan?.name
            val exp =
                userVM.currentUser.value?.planStart!! + CustomUtils.convertMonthsToMillis(plan?.validity!!)
            pValidityText.text = "Plan Expires on: ${CustomUtils.genDateString(exp)}"

            val msgText = SpannableString("${plan.chatCount} \n Chat Messages")
            msgText.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
            msgText.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryColor)),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            msgLimitText.text = msgText

            val dcText = SpannableString("${plan.dcCount} \n Direct Contacts")
            dcText.setSpan(RelativeSizeSpan(2f), 0, 2, 0)
            dcText.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.primaryColor)),
                0,
                2,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            dcLimitText.text = dcText

            val mText = SpannableString("${plan.meetupCount} \n Meetups")
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
        val cUid = userVM.currentUid.value!!

        //Since payment is successful, update user's current plan
        userVM.updateUserData(
            cUid, mapOf(
                "currentPlan" to selectedPlan,
                "planStart" to System.currentTimeMillis()
            )
        ).observe(this, Observer {
            val snack = Snackbar.make(
                window.decorView.findViewById(android.R.id.content),
                "Thank you for choosing us! Your plan will be updated in a minute",
                Snackbar.LENGTH_INDEFINITE
            )
            if (it.contains("Error"))
                snack.setText(it)
            snack.setAction("OK") { snack.dismiss() }
        })

        //Add payment to our database for future reference.
        userVM.createPayment(cUid, selectedPlan.id, p0!!)
    }

    fun startPayment(options: JSONObject) {
        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment Error: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("PlansActivity", "Error in starting Razorpay Checkout", e)
        }
    }
}
