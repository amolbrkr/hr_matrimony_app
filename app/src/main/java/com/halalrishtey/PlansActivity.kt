package com.halalrishtey

import android.os.Bundle
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

        userVM.getPlans().observe(this, Observer {
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
