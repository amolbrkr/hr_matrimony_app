package com.makeshaadi

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.bold
import androidx.lifecycle.Observer
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.User
import com.makeshaadi.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_schedule_meetup.*
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class ScheduleMeetupActivity : AppCompatActivity() {
    private val userVM: UserViewModel by viewModels()
    private var currentId: String? = null
    private var targetId: String? = null
    private var mode: String? = "new"
    private var meetupId: String? = null
    private var userData: ArrayList<User>? = null
    private var meetupDate: Date? = null
    private var meetupLoc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meetup)
        setSupportActionBar(smToolbar as Toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Schedule Meetup"

        currentId = intent.extras?.getString("currentId")
        targetId = intent.extras?.getString("targetId")
        mode = intent.extras?.getString("mode")
        Log.d("ScheduleMeetup", "current: $currentId, target: $targetId")
    }

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()

        if (currentId != null && targetId != null) {
            userVM.getProfilesByIds(arrayListOf(currentId!!, targetId!!))
                .observe(this, Observer {
                    userData = it
                    val i = it.size - 1

                    if (it[i].photoUrl.length > 5)
                        Picasso.get().load(it[i].photoUrl).into(smUserImg1)

                    smTitleText.text = SpannableStringBuilder().append("Meetup with ")
                        .bold { append(it[i].displayName) }.append(", select a date")

                    if (i > 0) {
                        smLocSpinner.adapter = ArrayAdapter(
                            this,
                            R.layout.dropdown_menu_popup_item,
                            arrayOf(
                                "Select Location",
                                "${it[i - 1].displayName}'s House",
                                "${it[i].displayName}'s House",
                                "Custom Location"
                            )
                        )
                    } else {
                        if (it[i].photoUrl.length > 5)
                            Picasso.get().load(it[i].photoUrl).into(smUserImg2)
                    }
                })
        }

        val cal = Calendar.getInstance()
        smTimeBtn.setOnClickListener {
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, h, m ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, m)
                    smTimeBtn.text = "Meetup Time is ${SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                    ).format(
                        Time(h, m, 0)
                    )}, tap to change"
                    meetupDate = cal.time
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        smCalender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            meetupDate = cal.time
        }

        smLocSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 3)
                    smLocInp.visibility = View.VISIBLE
                else smLocInp.visibility = View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        val autocompleteFragment =
//            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
//
//        autocompleteFragment!!.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
//            override fun onPlaceSelected(p0: Place) {
//                meetupLoc = p0.address.toString()
//                Log.d("ScheduleMeetup", "Selected place: ${p0.name}")
//            }
//
//            override fun onError(p0: Status) {
//                Log.d("ScheduleMeetup", "Error: ${p0.statusMessage}")
//            }
//
//        })

        if (mode == "new") {
            smConfirmBtn.setOnClickListener {
                if (userData?.size == 2) {
                    if (validateMeetup() == null) {
                        val meetupAddr =
                            if (smLocSpinner.selectedItemPosition != 3)
                                smLocSpinner.selectedItem.toString()
                            else smLocInp.editText?.text.toString()

                        val meetupLat = when (smLocSpinner.selectedItemPosition) {
                            1 -> userData!![0].locationLat
                            2 -> userData!![1].locationLat
                            else -> 0.0
                        }
                        val meetupLong = when (smLocSpinner.selectedItemPosition) {
                            1 -> userData!![0].locationLong
                            2 -> userData!![1].locationLong
                            else -> 0.0
                        }

                        val m = MeetupItem(
                            sourceId = userData!![0].uid!!,
                            sourceName = userData!![0].displayName,
                            sourcePhoto = userData!![0].photoUrl,
                            targetId = userData!![1].uid!!,
                            targetName = userData!![1].displayName,
                            targetPhoto = userData!![1].photoUrl,
                            timestamp = System.currentTimeMillis(),
                            date = meetupDate?.time!!,
                            address = meetupAddr,
                            locLat = meetupLat,
                            locLong = meetupLong
                        )

                        userVM.schedMeetup(m).observe(this, Observer {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                            this.finish()
                        })
                    } else Toast.makeText(
                        this,
                        "Error: ${validateMeetup()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            //Resched Meetup
            meetupId = intent.getStringExtra("meetupId")
            if (meetupId == null) {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                smConfirmBtn.setOnClickListener {
                    if (validateMeetup() == null) {
                        userVM.updateMeetup(
                            meetupId!!, mapOf(
                                "date" to meetupDate?.time!!
                            )
                        ).observe(this, Observer {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        })
                        finish()
                    } else {
                        Toast.makeText(this, validateMeetup(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateMeetup(): String? {
        return when {
            meetupDate == null -> {
                "Please set a date & time for Meetup!"
            }
            meetupDate != null && meetupDate!!.time < System.currentTimeMillis() -> {
                "Invalid date for Meetup!"
            }
            else -> null
        }
    }
}
