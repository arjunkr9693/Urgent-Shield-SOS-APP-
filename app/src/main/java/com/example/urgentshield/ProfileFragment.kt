package com.example.urgentshield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class ProfileFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_CODE_PROFILE_INPUT = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        // Retrieve data from SharedPreferences and display in TextViews
        displayProfileData()

        // Set OnClickListener for the Edit Profile button
        val profileEdit = view.findViewById<Button>(R.id.editProfileBtn)
        profileEdit.setOnClickListener {
            startActivityForResult(Intent(context, ProfileInput::class.java), REQUEST_CODE_PROFILE_INPUT)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Retrieve data from SharedPreferences and display in TextViews
        displayProfileData()
    }

    private fun displayProfileData() {
        sharedPreferences = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        // Retrieve data from SharedPreferences
        val username = sharedPreferences.getString("username", "")
        val userNumber = sharedPreferences.getString("userNumber", "")
        val userEmail = sharedPreferences.getString("userEmail", "")
        val parentNumber = sharedPreferences.getString("parentNumber", "")

        // Find TextViews in the layout
        val userNameTextView = view?.findViewById<TextView>(R.id.userName1)
        val userName2TextView = view?.findViewById<TextView>(R.id.userName2)
        val userNumberTextView = view?.findViewById<TextView>(R.id.userNumber)
        val userEmailTextView = view?.findViewById<TextView>(R.id.userEmail)
        val parentNumberTextView = view?.findViewById<TextView>(R.id.parentNumber)

        // Set retrieved data to TextViews
        userNameTextView?.text = username
        userName2TextView?.text = username
        userNumberTextView?.text = userNumber
        userEmailTextView?.text = userEmail
        parentNumberTextView?.text = parentNumber
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROFILE_INPUT && resultCode == Activity.RESULT_OK) {
            // ProfileInput activity finished successfully
            // Refresh profile data in TextViews
            displayProfileData()
        }
    }
}