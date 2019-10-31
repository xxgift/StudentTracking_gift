package com.egco428.StudentTracking

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.egco428.StudentTracking.Models.Data
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker
import com.estimote.coresdk.observation.region.beacon.BeaconRegion
import com.estimote.coresdk.recognition.packets.Beacon
import com.estimote.coresdk.service.BeaconManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.home_teacher.*
import kotlinx.android.synthetic.main.layout_navbar.*
import kotlinx.android.synthetic.main.register.*
import kotlinx.android.synthetic.main.row_studentlist.view.*
import java.util.*


class HomeTeacher : AppCompatActivity() {
    private lateinit var adapter:myCustomAdapter
    private var beaconManager: BeaconManager? = null
    private var region: BeaconRegion? = null
    private var PLACES_BY_BEACONS: Map<String, List<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_teacher)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        val NavView:NavigationView=findViewById(R.id.nav_view)

        val placesByBeacons = HashMap<String, ArrayList<String>>()
        placesByBeacons.put("38845:58352", arrayListOf("Blue", "Mint", "Coco", "Mash"))
        placesByBeacons.put("51284:49927", arrayListOf("Mint", "Blue","Coco", "Mash"))
        placesByBeacons.put("31937:52697", arrayListOf("Coco", "Blue", "Mint", "Mash"))
        placesByBeacons.put("12436:27016", arrayListOf("Mash", "Blue", "Mint", "Coco"))

        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons)
        beaconManager = BeaconManager(this)
        region = BeaconRegion("mint region",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null)
        beaconManager!!.connect {
            beaconManager!!.startMonitoring(BeaconRegion("coco region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 31937, 52697))
        }
        beaconManager!!.connect {
            beaconManager!!.startMonitoring(BeaconRegion("mash region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 12436, 27016))
        }
        beaconManager!!.connect {
            beaconManager!!.startMonitoring(BeaconRegion("blue region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 38845, 58352))
        }
        beaconManager!!.connect {
            beaconManager!!.startMonitoring(BeaconRegion("mint region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 51284, 49927))
        }
        beaconManager!!.setRangingListener(object :BeaconManager.BeaconRangingListener{
            override fun onBeaconsDiscovered(
                beaconRegion: BeaconRegion?,
                beacons: MutableList<Beacon>?
            ) {
                if (!beacons!!.isEmpty()) {
                    val nearestBeacon = beacons.get(0)
                    val places = placesNearBeacon(nearestBeacon)
                    // TODO: update the UI here
                    Log.d("Airport", "Nearest places: $places")
                }
            }

        })
        beaconManager!!.setMonitoringListener(object : BeaconManager.BeaconMonitoringListener {
            override fun onExitedRegion(beaconRegion: BeaconRegion?) {

            }

            override fun onEnteredRegion(beaconRegion: BeaconRegion?, beacons: MutableList<Beacon>?) {
                showNotification(
                    "Your gate closes in 47 minutes.",
                    "Current security wait time is 15 minutes, "
                            + "and it's a 5 minute walk from security to the gate. "
                            + "Looks like you've got plenty of time!");
                placesNearBeacon(beacons!![0])
            }

        })

        adapter = myCustomAdapter(dataListView)
        main_listView.adapter = adapter
        main_listView.setOnItemClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as String
        }
        backBtn2.setOnClickListener {
            finish()
        }
        settingBtn.setOnClickListener {
            finish()
        }

        adapter.notiChange()

    }
    private fun showNotification(title: String, message: String) {
        val notifyIntent = Intent(this, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivities(this, 0,
            arrayOf(notifyIntent), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(this)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
    private fun placesNearBeacon(beacon: Beacon): List<String>? {
        var beaconKey = String.format("%d:%d", beacon.major, beacon.minor)
//        beacon.rssi,beacon.measuredPower ทำ%dแล้วรันไม่ได้
        if (PLACES_BY_BEACONS!!.containsKey(beaconKey)) {
            Toast.makeText(this,"fon $beaconKey", Toast.LENGTH_LONG).show()
            return PLACES_BY_BEACONS?.get(beaconKey)
        }
        return Collections.emptyList()
    }

    override fun onResume() {
        super.onResume()
        SystemRequirementsChecker.checkWithDefaultDialogs(this)
        beaconManager!!.connect(object : BeaconManager.ServiceReadyCallback {
            override fun onServiceReady() {
                beaconManager!!.startRanging(region);
            }

        })
    }

    override fun onPause() {
        beaconManager!!.stopRanging(region);
        super.onPause()
    }


    override fun onStart() {
        super.onStart()
        adapter.notiChange()

    }

    private class myCustomAdapter(val data:ArrayList<Data>): BaseAdapter(){

        fun notiChange(){
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }


        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val rowMain: View

            val blueColor = Color.parseColor("#2196F3")
            val orangeColor = Color.parseColor("#FF5722")
            if(convertView == null){
                val layoutInflator = LayoutInflater.from(viewGroup!!.context)
                rowMain = layoutInflator.inflate(R.layout.row_studentlist, viewGroup, false)
                val viewHolder = ViewHolder(
                    rowMain.resultText,
                    rowMain.dateText,
                    rowMain.textImgmain
                )
                rowMain.tag = viewHolder
            }else{
                rowMain = convertView
            }

            val viewHolder = rowMain.tag as ViewHolder

            viewHolder.resultTextView.text = data[position].cookie.message
            viewHolder.dateTextView.text = "Date : ${data[position].date}"
            viewHolder.textImgView.text = data[position].cookie.message
            if (data[position].cookie.status == "positive"){
                viewHolder.resultTextView.setTextColor(blueColor)
            }else{
                viewHolder.resultTextView.setTextColor(orangeColor)
            }
            rowMain.setBackgroundColor(Color.parseColor("#FFFFFF"))



            rowMain.setOnClickListener{
                rowMain.animate().setDuration(500).alpha(0f).withEndAction {
                    data.removeAt(position)
                    notifyDataSetChanged()
                    rowMain.alpha = 1.0F
                }
            }
            return rowMain
        }

        private class ViewHolder(val resultTextView: TextView, val dateTextView: TextView, val textImgView: TextView)


    }
}
