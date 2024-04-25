package com.example.urgentshield

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileInput : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_input)

        val saveBtn = findViewById<Button>(R.id.saveProfileBtn)

        saveBtn.setOnClickListener {
            val userNumberEditText = findViewById<EditText>(R.id.userNumberET)
            val number = userNumberEditText.text.toString()
            if(number.length < 10 || number.length > 10){
                Toast.makeText(this, "Number should be of length 10", Toast.LENGTH_SHORT).show()
            }
            else{
                saveProfileData()
            }
        }

    }

    private fun saveProfileData() {
        // Save profile data to SharedPreferences
        // Replace the following lines with your code to save the profile data
        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

        val usernameEditText = findViewById<EditText>(R.id.usernameET)
        val userNumberEditText = findViewById<EditText>(R.id.userNumberET)
        val userEmailEditText = findViewById<EditText>(R.id.userEmailET)
        val parentNumberEditText = findViewById<EditText>(R.id.parentNumberET)

        val username = usernameEditText.text.toString()
        val userNumber = userNumberEditText.text.toString()
        val userEmail = userEmailEditText.text.toString()
        val parentNumber = parentNumberEditText.text.toString()

        // Save data to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("loggedInBefore", true)
        editor.putString("username", username)
        editor.putString("userNumber", userNumber)
        editor.putString("userEmail", userEmail)
        editor.putString("parentNumber", parentNumber)
        editor.apply()

        // Set result and finish activity
        setResult(Activity.RESULT_OK)
        finish()
    }
}