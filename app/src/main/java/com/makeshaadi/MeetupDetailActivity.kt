package com.makeshaadi

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.makeshaadi.models.MeetupItem
import com.makeshaadi.models.MeetupStatus
import com.makeshaadi.viewmodels.UserAuthViewModel
import com.makeshaadi.viewmodels.UserViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_meetup_detail.*


class MeetupDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    //    private var googleMap: GoogleMap? = null
    private var data: MeetupItem? = null
    private val userVM: UserViewModel by viewModels()
    private val userAuthVM: UserAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetup_detail)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        data = intent.getParcelableExtra<MeetupItem>("meetupData")

        if (data != null) {
            if (data!!.sourcePhoto.length > 5)
                Picasso.get().load(data!!.sourcePhoto).into(mdUserImg1)

            if (data!!.targetPhoto.length > 5)
                Picasso.get().load(data!!.targetPhoto).into(mdUserImg2)

            mdTitleText.text = SpannableStringBuilder().append("Meetup with ")
                .bold { append(data!!.targetName) }
                .append(" on ${CustomUtils.genDateString(data!!.date)}")

            mdLocText.text = data!!.address

//            mFeedbackBtn.setOnClickListener {
//                Toast.makeText(this, "Thanks for your feedback.", Toast.LENGTH_SHORT).show()
//            }

            mdBackBtn.setOnClickListener {
                this.finish()
            }

            mCompleteBtn.setOnClickListener {
                userVM.updateMeetupStatus(data!!.meetupId, MeetupStatus.Done)
                    .observe(this, Observer {
                        Toast.makeText(this, "Thanks!", Toast.LENGTH_SHORT).show()
                        this.finish()
                    })
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (data != null && data!!.locLat != 0.0 && data!!.locLong != 0.0) {
            val loc = LatLng(data!!.locLat, data!!.locLong)
//            val userCircle = googleMap!!.addCircle(
//                CircleOptions().center(loc).radius(1000.0)
//                    .strokeWidth(10.0F)
//                    .strokeColor(Color.GREEN).fillColor(Color.GREEN).clickable(true)
//            )

            userAuthVM.locationUpdates.observe(this, Observer {
                if (it != null) {

//                    userCircle.center = LatLng(it.latitude, it.longitude)

//                    val path: MutableList<List<LatLng>> = ArrayList()
//                    val urlDirections =
//                        "https://maps.googleapis.com/maps/api/directions/json?origin=${it.latitude},${it.longitude}&destination=${loc.latitude},${loc.longitude}&key=AIzaSyCsDLdcuW_HeLU0Uus4ppE4M8-uwZhED-k"
//                    Log.d("MeetupDetails", "Request Url: $urlDirections")
//                    val directionsRequest = object : StringRequest(
//                        Request.Method.GET,
//                        urlDirections,
//                        Response.Listener<String> { response ->
//                            val jsonResponse = JSONObject(response)
//                            if (jsonResponse.get("error_message").toString().isNotEmpty()) {
//                                Toast.makeText(
//                                    this,
//                                    jsonResponse.get("error_message").toString(),
//                                    Toast.LENGTH_SHORT
//                                ).show();
//                                this.finish();
//                            }
//                            Log.d("MeetupDetails", "API Res: $response")
//                            // Get routes
//                            val routes = jsonResponse.getJSONArray("routes")
//                            val legs = routes.getJSONObject(0).getJSONArray("legs")
//                            val steps = legs.getJSONObject(0).getJSONArray("steps")
//                            for (i in 0 until steps.length()) {
//                                val points = steps.getJSONObject(i).getJSONObject("polyline")
//                                    .getString("points")
//                                path.add(PolyUtil.decode(points))
//                            }
//                            for (i in 0 until path.size) {
//                                googleMap.addPolyline(
//                                    PolylineOptions().addAll(path[i]).color(Color.RED)
//                                )
//                            }
//                        },
//                        Response.ErrorListener { _ ->
//                        }) {}
//                    val requestQueue = Volley.newRequestQueue(this)
//                    requestQueue.add(directionsRequest)
                }

            })

            googleMap?.addMarker(
                MarkerOptions().position(loc)
                    .title("Meetup Location")
            )
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f))
        }
    }
}

