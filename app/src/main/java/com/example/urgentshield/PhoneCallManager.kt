package com.example.urgentshield

import android.content.Context
import android.telephony.TelephonyManager
import android.telephony.PhoneStateListener
import android.util.Log
import android.content.Intent
import android.net.Uri

class PhoneCallManager(private val context: Context) {
    private var phoneNumberList: List<String> = emptyList()
    private var currentPhoneIndex = -1

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            super.onCallStateChanged(state, incomingNumber)
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    currentPhoneIndex++
                    if (currentPhoneIndex < phoneNumberList.size) {
                        makePhoneCall(phoneNumberList[currentPhoneIndex])
                    } else {
                        Log.d(TAG, "All calls completed")
                    }
                }
            }
        }
    }

    fun makeCalls(phoneNumbers: List<String>) {
        phoneNumberList = phoneNumbers
        currentPhoneIndex = -1
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(
            phoneStateListener,
            PhoneStateListener.LISTEN_CALL_STATE
        )
        if (phoneNumberList.isNotEmpty()) {
            currentPhoneIndex++
            makePhoneCall(phoneNumberList[currentPhoneIndex])
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        Log.d(TAG, "Calling $phoneNumber")
        val dialIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(dialIntent)

    }

    companion object {
        private const val TAG = "PhoneCallManager"
    }
}
