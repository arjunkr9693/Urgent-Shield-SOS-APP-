package com.example.urgentshield.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.example.urgentshield.ui.homeFragments.ContactFragment
import com.example.urgentshield.ui.homeFragments.DevelopersFragment
import com.example.urgentshield.PermissionAllUtils
import com.example.urgentshield.ui.homeFragments.ProfileFragment
import com.example.urgentshield.ui.ProfileInput
import com.example.urgentshield.R
import com.example.urgentshield.adapters.ViewPagerAdapter
import com.example.urgentshield.ui.homeFragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var pagerAdapter: ViewPagerAdapter

    private val onPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            bottomNavigation.menu.getItem(position).isChecked = true
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreferences = getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
        val loggedInBefore = sharedPreferences.getBoolean("loggedInBefore", false)
        if (!loggedInBefore) {
            startActivity(Intent(this, ProfileInput::class.java))
        }

        PermissionAllUtils.requestAllPermission(this, this)
        bottomNavigation = findViewById(R.id.navigationView)
        bottomNavigation.itemTextColor = ContextCompat.getColorStateList(this,
            R.color.navbar_item_colors
        )
        bottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this,
            R.color.navbar_item_colors
        )

        initializeViews()
        setupViewPager()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.navigationView)
    }

    private fun setupViewPager() {
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        pagerAdapter.addFragment(HomeFragment())
        pagerAdapter.addFragment(ContactFragment())
        pagerAdapter.addFragment(DevelopersFragment())
        pagerAdapter.addFragment(ProfileFragment())
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(onPageChangeListener)
        viewPager.offscreenPageLimit = 3 // Retain 3 pages on either side of the current page
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> viewPager.currentItem = 0
                R.id.contact -> viewPager.currentItem = 1
                R.id.developers -> viewPager.currentItem = 2
                R.id.profile -> viewPager.currentItem = 3
            }
            true
        }
    }
}


//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class HomeActivity : AppCompatActivity() {
//
//    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
//
//        PermissionAllUtils.requestAllPermission(this, this)
//        val bottomNavigation = findViewById<BottomNavigationView>(R.id.navigationView)
//        bottomNavigation.itemTextColor = ContextCompat.getColorStateList(this, R.color.navbar_item_colors)
//        bottomNavigation.itemIconTintList = ContextCompat.getColorStateList(this, R.color.navbar_item_colors)
//
//        replaceWithFragment(HomeFragment())
//        bottomNavigation.setOnItemSelectedListener {
//
//            when(it.itemId){
//                R.id.home -> replaceWithFragment(HomeFragment())
//                R.id.contact -> replaceWithFragment(ContactFragment())
//                R.id.developers -> replaceWithFragment(DevelopersFragment())
//                R.id.profile -> replaceWithFragment(ProfileFragment())
//                else ->{
//
//                }
//            }
//            true
//        }
//
//    }
//
//
//    private fun replaceWithFragment(fragment: Fragment) {
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, fragment)
//        fragmentTransaction.commit()
//    }
//}