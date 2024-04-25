package com.example.urgentshield

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

object PermissionAllUtils {
    fun requestAllPermission(activity: Activity, context: Context){
        val list = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS)

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val permissions = list.toTypedArray()


        if(!hasPermission(context, permissions)){
            ActivityCompat.requestPermissions(activity, permissions, 3)
        }
    }

    private fun hasPermission(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions){
            if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    fun requestForPermission(activity: Activity, context: Context, permissions: Array<String>, requestCode: Int){
        for(permission in permissions){
            if(!isGranted(context, permission)){
//                if(permission == Manifest.permission.SEND_SMS){
//                    requestCode = 1
//                }
//                else if (permission == Manifest.permission.CALL_PHONE){
//                    requestCode = 2
//                }
//                else if(permission == Manifest.permission.ACCESS_FINE_LOCATION || permission == Manifest.permission.ACCESS_COARSE_LOCATION || permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION){
//                    requestCode = 3
//                }else{
//                    requestCode = 10
//                }
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
                break
            }
        }
    }

    fun isGranted(context: Context, permission: String):Boolean{
        if(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        else{
            return false
        }
    }


}

