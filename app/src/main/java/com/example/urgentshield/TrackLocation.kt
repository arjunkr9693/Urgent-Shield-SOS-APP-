package com.example.urgentshield

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import java.util.Timer
import java.util.TimerTask

class TrackLocation : Service() {

    private val sharedPreferencesKey = "contact_pref"
    private val firebase_DB_Reference = FirebaseDatabase.getInstance().getReference("Users")
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var handler: Handler
    private lateinit var notificationManager: NotificationManager
    private lateinit var timer: Timer
//    private lateinit var totalContacts: ArrayList<String>
    private lateinit var dbKey: String
//    private var smsManager: SmsManager = SmsManager.getDefault()

    private var message: String = ""

    companion object {
        private const val TAG = "TrackLocation"
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "LocationForegroundServiceChannel"
        private const val STOP_SERVICE_DELAY_MS = 120000L // 2 minutes
        private const val SMS_INTERVAL_MS = 20000L // 20 seconds
        private const val DESTINATION_PHONE_NUMBER = "+918228033862"
    }


    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        HomeFragment.sos_trigger = true
        val sharedPreference = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val userNumber = sharedPreference.getString("userNumber", "").toString()
        dbKey = "00000${userNumber.take(7)}"
        // get all the contacts
//        val sharedPreferences = this.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
//        val allEntries = sharedPreferences.all
//        for ((key, value) in allEntries) {
//            totalContacts.add(key)
//        }

//        totalContacts = ArrayList()
//        totalContacts.add(DESTINATION_PHONE_NUMBER)
//        totalContacts.add("+918271627510")

        Log.d(TAG, "Service created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        createLocationCallback()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = SOS_Notification.CreateNotification(this, NOTIFICATION_ID, "SOS Service is running", "Your information is being shared. Tap STOP button to Deactivate the service")
        startForeground(NOTIFICATION_ID, notification)


        handler = Handler()
        handler.postDelayed({ stopSelf() }, STOP_SERVICE_DELAY_MS)

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
//                sendLocationSMS()
            }
        }, 11, SMS_INTERVAL_MS)

        requestLocationUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")

        //Set value that user is not sharing data
        setValueInDB(false, 0.0, 0.0)

        HomeFragment.sendToAll = true
        HomeFragment.sos_trigger = false
        stopLocationUpdates()
        // Cancel the notification when service is destroyed
//        notificationManager.cancel(123)
//        notificationManager.cancel(333)
        notificationManager.cancelAll()
        timer.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000 // Update interval in milliseconds
            fastestInterval = 5000 // Fastest update interval in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d(TAG, "Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                    // Handle location updates here
                    setValueInDB(true, location.latitude, location.longitude)
                }
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//
//        dbKey = intent?.getStringExtra("dbKey").toString()
//        return START_STICKY
//    }

    private fun setValueInDB(isSharing: Boolean, latitude: Double, longitude: Double){
        firebase_DB_Reference.child(dbKey).setValue(LocationData(isSharing, latitude, longitude)).addOnSuccessListener {
            Log.d(TAG, "Location Saved to DB")
        }.addOnFailureListener{

        }
    }
//    private fun sendLocationSMS() {
//        if(HomeFragment.sendToAll){
//            for (contact in totalContacts){
//                if (message.isNotEmpty()) {
//                    smsManager.sendTextMessage(contact, null, message, null, null)
//                }
//            }
//        }
//        else{
//            if (message.isNotEmpty()) {
//                smsManager.sendTextMessage(DESTINATION_PHONE_NUMBER, null, message, null, null)
//            }
//        }
//    }

}
