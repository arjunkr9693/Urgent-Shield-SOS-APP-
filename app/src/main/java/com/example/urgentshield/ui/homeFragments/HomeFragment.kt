package com.example.urgentshield.ui.homeFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.urgentshield.PermissionAllUtils
import com.example.urgentshield.R
import com.example.urgentshield.SOS_Notification
import com.example.urgentshield.services.TrackLocation
import com.skyfishjy.library.RippleBackground
import java.util.Timer
import java.util.TimerTask

private const val REQUEST_MESSAGE_CODE = 1
private const val REQUEST_PHONE_CODE = 2
private const val NOTIFICATION_ID = 333

class HomeFragment : Fragment() {

    companion object {
        var sos_trigger = false
        var sendToAll = true
    }

    private val handler = Handler(Looper.getMainLooper())
    private val delay = 500L // Milliseconds between checks (0.5 seconds)
    private var timer: Timer? = null
    private lateinit var timerTV: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val activationTime: Long = 3000
    private val phoneNumber = "+918271627510" // Phone number for demo
    private lateinit var rippleBackground: RippleBackground
    private lateinit var notificationManager: NotificationManager
    private lateinit var sosButton: ImageView
    private var totalContacts = ArrayList<String>()
    private var message = ""
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        countDownTimer = object : CountDownTimer(activationTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000) + 1
                timerTV.text = secondsRemaining.toString()
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                timerTV.visibility = View.INVISIBLE
                rippleBackground.stopRippleAnimation()
                activateSOS()
            }
        }

        val view =  inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCheckingVariable()
        notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // SOS Button Functionality
        timerTV = view.findViewById(R.id.timerTV)
        sosButton = view.findViewById(R.id.sos_button)
        rippleBackground = view.findViewById(R.id.content)

        sosButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(sos_trigger){
                        sos_trigger = false
                        Log.d("SOSError", "Pressed STOP")
                        requireContext().stopService(Intent(requireContext(), TrackLocation::class.java))
                        val notification = SOS_Notification.CreateNotification(
                            requireContext(),
                            555,
                            "SOS Deactivated",
                            "Tap STOP to Deactivate"
                        )
                        Handler().postDelayed({
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }, 1500)
                    }
                    else{
                        startCountdown()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    stopCountdown()
                }
            }
            true
        }

        // Set click listeners for different types of emergency buttons
        val fireBtn = view.findViewById<AppCompatButton>(R.id.fireBtn)
        val medicalBtn = view.findViewById<AppCompatButton>(R.id.medicalBtn)
        val accidentBtn = view.findViewById<AppCompatButton>(R.id.accidentBtn)
        val violenceBtn = view.findViewById<AppCompatButton>(R.id.violenceBtn)
        val rescueBtn = view.findViewById<AppCompatButton>(R.id.rescueBtn)
        val naturalDisBtn = view.findViewById<AppCompatButton>(R.id.naturalDis)

        val buttonClickListener = View.OnClickListener {
            try {
                if(!sos_trigger) {
                    sendToAll = false
                    sos_trigger = true
                    // Needed Variables for sharing Info
                    val totalContact = ArrayList<String>()
                    var numberToShareInfo = ""

                    notificationManager.cancelAll()     // Remove all notification if present
                    // Send Notification that SOS is Activated
                    val notification = SOS_Notification.CreateNotification(
                        requireContext(),
                        NOTIFICATION_ID,
                        "SOS Activated",
                        "Tap STOP to Deactivate"
                    )
                    notificationManager.notify(NOTIFICATION_ID, notification)
                    // Intent for starting Track Location Service
                    val intent = Intent(requireContext(), TrackLocation::class.java)
                    requireContext().startForegroundService(intent)

                    when (it) {
                        fireBtn -> {
                            message = getString(R.string.fireMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                            Log.d("MyLog", message)
                        }

                        medicalBtn -> {
                            message = getString(R.string.medicalMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                        }

                        accidentBtn -> {
                            message = getString(R.string.accidentMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                        }

                        violenceBtn -> {
                            message = getString(R.string.violenceMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                        }

                        rescueBtn -> {
                            message = getString(R.string.resqueMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                        }

                        naturalDisBtn -> {
                            message = getString(R.string.naturalDisMessage) + "\n" + getUserData()
                            numberToShareInfo = phoneNumber
                        }
                    }
                    // Add contact Info to Call and Send SMS
                    totalContact.add(numberToShareInfo)
                    // SMS sending and Calling
                    sendLocationSMS(message, totalContact)
                    sendLocationSMS("\nurjentshield.pythonanywhere.com/location/fetch-location/?user_id=$userId\n", totalContact)
                    makePhoneCall(numberToShareInfo)
                }
                else{
                    Toast.makeText(requireContext(), "Stop the running service first", Toast.LENGTH_LONG).show()
                }
            }
            catch (e:Exception){
                Log.d("SOSError", "Error is: $e")
                Toast.makeText(requireContext(), "Give Required Permissions like Call, Message, Location", Toast.LENGTH_LONG).show()
                PermissionAllUtils.requestAllPermission(requireActivity(), requireContext())
            }

        }

        fireBtn.setOnClickListener(buttonClickListener)
        medicalBtn.setOnClickListener(buttonClickListener)
        accidentBtn.setOnClickListener(buttonClickListener)
        violenceBtn.setOnClickListener(buttonClickListener)
        rescueBtn.setOnClickListener(buttonClickListener)
        naturalDisBtn.setOnClickListener(buttonClickListener)

    }

    // Get user data to send the details like name, number, email in the form of sms
    private fun getUserData(): String {
        val sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        // Retrieve data from SharedPreferences
        val username = sharedPreferences.getString("username", "")
        val userNumber = sharedPreferences.getString("userNumber", "")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val parentNumber = sharedPreferences.getString("parentNumber", "")

        val userDataBuilder = StringBuilder()

        // Check if userNumber is valid and add to userDataBuilder
        if (userNumber?.isNotEmpty() == true) {
            userDataBuilder.append("\nContact No: $userNumber\n")
        }

//        Check if username is valid and add to userDataBuilder
        if (username?.isNotEmpty() == true) {
            userDataBuilder.append("Name: $username\n")
        }

        // Check if parentNumber is valid and add to userDataBuilder

        if(parentNumber?.isNotEmpty() == true){
            userDataBuilder.append("Parent's Contact: $parentNumber\n")
        }

        // Check if userEmail is valid and add to userDataBuilder
        if (userEmail?.isNotEmpty() == true) {
            userDataBuilder.append("Email: $userEmail\n")
        }

        userId = "00000${userNumber?.take(7)}"
        return userDataBuilder.toString()
    }

    private fun startCheckingVariable() {
        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                        // Update UI or perform actions based on myVariable (use handler.post for UI)
                        handler.post {
                            // Update UI elements or perform actions
                            if(sos_trigger){
                                sosButton.setImageResource(R.drawable.stop_button)
                            }
                            else{
                                sosButton.setImageResource(R.drawable.sos_dark_button)
                            }
                        }
                }
            }, 0, delay)
        }
    }

    private fun startCountdown() {
        timerTV.visibility = View.VISIBLE
        rippleBackground.startRippleAnimation()

        countDownTimer.start()
    }

    private fun stopCountdown() {
        timerTV.visibility = View.INVISIBLE
        rippleBackground.stopRippleAnimation()
        countDownTimer.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun activateSOS() {
        if(!sos_trigger) {
            sos_trigger = true
            rippleBackground.stopRippleAnimation()

            notificationManager.cancelAll()     // Remove all notification if present
            val notification = SOS_Notification.CreateNotification(
                requireContext(),
                NOTIFICATION_ID,
                "SOS Activated",
                "Tap STOP to Deactivate"
            )
            notificationManager.notify(NOTIFICATION_ID, notification)

//            val totalContacts = getContactDataFromSharedPreferences()
            totalContacts = arrayListOf(phoneNumber, "+918271627510")
            message = getString(R.string.sosMessage) +"\n"
            sendLocationSMS(message, totalContacts)
            sendLocationSMS("\nurjentshield.pythonanywhere.com/location/fetch-location/?user_id=$userId\n", totalContacts)

            val intent = Intent(requireContext(), TrackLocation::class.java)
            context?.startForegroundService(intent)
        }
        else{
            Toast.makeText(requireContext(), "Stop the running service first", Toast.LENGTH_LONG).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (PermissionAllUtils.isGranted(requireContext(), Manifest.permission.CALL_PHONE)) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
                putExtra("android.telecom.extra.START_CALL_WITH_VIDEO_STATE", 3) // 3 stands for VIDEO_STATE_BIDIRECTIONAL
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            }
        } else {
            PermissionAllUtils.requestForPermission(
                requireActivity(),
                requireContext(),
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_PHONE_CODE
            )
        }
    }

    // get total contacts added by user for emergency to send sms and location
    private fun getContactDataFromSharedPreferences(): ArrayList<String> {
        val sharedPreferences = requireContext().getSharedPreferences("contact_pref", Context.MODE_PRIVATE)
        val data = ArrayList<String>()
        val allEntries = sharedPreferences.all
        for (key in allEntries.keys) {
            data.add(key)
        }
//        val policeContactExists = data.any { it.contactNumber == "100" }
//        if (!policeContactExists) {
//            data.add(Contact("Police", "100"))
//        }
        return data
    }

    private fun sendLocationSMS(message: String, totalContacts: ArrayList<String>) {
        if (PermissionAllUtils.isGranted(requireContext(), Manifest.permission.SEND_SMS)){
            Handler().postDelayed({
                if (sos_trigger){
                    val smsManager:SmsManager = requireContext().getSystemService(SmsManager::class.java)
                    for (contact in totalContacts){
                        if (message.isNotEmpty()) {
                            smsManager.sendTextMessage(contact, null, message, null, null)
                        }
                    }
                }
            }, 5000)
        }
        else{
            PermissionAllUtils.requestForPermission(
                requireActivity(),
                requireContext(),
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_MESSAGE_CODE
            )
            Toast.makeText(requireContext(), "PopUp not showing -> Give permission in App Settings", Toast.LENGTH_LONG).show()
        }

}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PHONE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform the action
                makePhoneCall(phoneNumber)
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Call Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == REQUEST_MESSAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform the action
                Toast.makeText(requireContext(), "Message Sent", Toast.LENGTH_SHORT).show()
                sendLocationSMS(message, totalContacts)
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Message Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}



//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.telephony.SmsManager
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.appcompat.widget.AppCompatButton
//import com.example.urgentshield.R
//import com.skyfishjy.library.RippleBackground
//
//private const val SEND_SMS_PERMISSION_REQUEST = 1
//private const val CALL_PHONE_PERMISSION_REQUEST = 2
//
//class HomeFragment : Fragment() {
//
//    private lateinit var timerTV : TextView
//    private lateinit var countDownTimer: CountDownTimer
//    private val activationTime: Long = 3000
//
//    private fun startCountdown() {
//        countDownTimer = object : CountDownTimer(activationTime, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                val secondsRemaining = (millisUntilFinished/1000) + 1
//                timerTV.text = secondsRemaining.toString()
//
//            }
//
//            override fun onFinish() {
//                timerTV.visibility = View.INVISIBLE
//                // Activate SOS feature here
//                Toast.makeText(context, "3 secs completed", Toast.LENGTH_SHORT).show()
//                activateSOS()
//            }
//        }
//        countDownTimer.start()
//    }
//
//    private fun activateSOS() {
//        sendSms("918228033862", "HI")
//        // Implement your SOS feature activation logic here
//    }
//    @SuppressLint("ClickableViewAccessibility")
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_home, container, false)
//
//        timerTV = view.findViewById(R.id.timerTV)
//        val sosButton = view.findViewById<ImageView>(R.id.sos_button)
//        sosButton.setOnTouchListener { _, event ->
//            val rippleBackground = view.findViewById<RippleBackground>(R.id.content)
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    timerTV.visibility = View.VISIBLE
//                    rippleBackground.startRippleAnimation()
//                    startCountdown()
//                }
//                MotionEvent.ACTION_UP -> {
//                    timerTV.visibility = View.INVISIBLE
//                    rippleBackground.stopRippleAnimation()
//                    countDownTimer.cancel()
//                }
//            }
//            true
//        }
//
//        // Request permissions if not granted
//        if (!isCallPhonePermissionGranted()) {
//            requestCallPhonePermission()
//        }
//        if (!isSendSmsPermissionGranted()) {
//            requestSendSmsPermission()
//        }
//
//        // Set click listeners for buttons
//        val fireBtn = view.findViewById<AppCompatButton>(R.id.fireBtn)
//        val medicalBtn = view.findViewById<AppCompatButton>(R.id.medicalBtn)
//        val accidentBtn = view.findViewById<AppCompatButton>(R.id.accidentBtn)
//        val violenceBtn = view.findViewById<AppCompatButton>(R.id.violenceBtn)
//        val rescueBtn = view.findViewById<AppCompatButton>(R.id.rescueBtn)
//        val naturalDisBtn = view.findViewById<AppCompatButton>(R.id.naturalDis)
//
//        val phoneNumber = "+918228033862" // Phone number for demo
//
//        fireBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        medicalBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        accidentBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        violenceBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        naturalDisBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        rescueBtn.setOnClickListener {
//            if (isCallPhonePermissionGranted()) {
//                makePhoneCall(phoneNumber)
//            } else {
//                requestCallPhonePermission()
//                showToast("Give Call Permission...")
//            }
//            sendSms(phoneNumber, "HI")
//        }
//
//        return view
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun isCallPhonePermissionGranted(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.CALL_PHONE
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestCallPhonePermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.CALL_PHONE),
//            CALL_PHONE_PERMISSION_REQUEST
//        )
//    }
//
//    private fun isSendSmsPermissionGranted(): Boolean {
//        return ActivityCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.SEND_SMS
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestSendSmsPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.SEND_SMS),
//            SEND_SMS_PERMISSION_REQUEST
//        )
//    }
//
//    private fun makePhoneCall(phoneNumber: String) {
//        val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
//        startActivity(dialIntent)
//    }
//
//    private fun sendSms(phoneNumber: String, message: String) {
//        if (isSendSmsPermissionGranted()) {
//            val smsManager = SmsManager.getDefault()
//            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
//        } else {
//            requestSendSmsPermission()
//        }
//    }
//}
