package com.example.urgentshield.adapters

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.urgentshield.data.Contact
import com.example.urgentshield.R

class ContactListAdapter(private var context: Activity, private var arrayList: ArrayList<Contact>) : ArrayAdapter<Contact>(context,
    R.layout.contact_layout, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.contact_layout, parent, false)
        }

        val contactName = view!!.findViewById<TextView>(R.id.contactNameTV)
        val contactNumber = view.findViewById<TextView>(R.id.contactNumberTV)
        val deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)

        val contact = arrayList[position]

        contactName.text = contact.contactName
        contactNumber.text = contact.contactNumber

        // Set click listener for delete button
        deleteBtn.setOnClickListener {
            // Get contact number associated with the clicked item
            val contactNumberToDelete = contact.contactNumber
            // Delete contact detail from SharedPreferences
            deleteContactFromSharedPreferences(contactNumberToDelete)
            // Remove the contact from the list and notify the adapter
            arrayList.remove(contact)
            notifyDataSetChanged()
        }
        return view
    }

    private fun deleteContactFromSharedPreferences(contactNumber: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("contact_pref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(contactNumber).apply()
    }

}