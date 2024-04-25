package com.example.urgentshield

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.Manifest
import androidx.fragment.app.Fragment


class ContactFragment : Fragment() {

    private val request_code = 55
    private val sharedPreferencesKey = "contact_pref"
    private lateinit var data : ArrayList<Contact>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_contact, container, false)

        data = ArrayList()
        data = getDataFromSharedPreferences()
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = MyAdapter(requireActivity(), data)

        val addBtn = view.findViewById<ImageView>(R.id.addContact)
        addBtn.setOnClickListener {
//            val preferences = requireContext().getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
//            val editor = preferences.edit()
//            editor.clear() // Clear all data
//
//            editor.apply() // Apply changes

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, request_code)
        }

        // to make call when any contact item is clicked
        listView.setOnItemClickListener { parent, view, position, id ->
            makePhoneCall(data[position].contactNumber)
        }

        return view
    }

    private fun saveContactDetails(number: String, name: String) {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(number, name)
        editor.apply()
    }

    private fun getDataFromSharedPreferences(): ArrayList<Contact> {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val data = ArrayList<Contact>()
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            data.add(Contact(value.toString(), key))
        }
//        val policeContactExists = data.any { it.contactNumber == "100" }
//        if (!policeContactExists) {
//            data.add(Contact("Police", "100"))
//        }
        return data
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (PermissionAllUtils.isGranted(requireContext(), Manifest.permission.CALL_PHONE)) {
            val dialIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(dialIntent)
        } else {
            PermissionAllUtils.requestForPermission(
                requireActivity(),
                requireContext(),
                arrayOf(Manifest.permission.CALL_PHONE),
                7
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == request_code && resultCode == Activity.RESULT_OK){
            val contactUri = data?.data ?: return
            val contact = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            val resolver = requireContext().contentResolver.query(contactUri, contact, null, null, null)

            if(resolver?.moveToFirst() == true){
                val contactNumber = resolver.getString(0)
                val contactName = resolver.getString(1)
                saveContactDetails(contactNumber, contactName)

                val data = getDataFromSharedPreferences()
                val listView = view?.findViewById<ListView>(R.id.listView)
                val adapter = listView?.adapter as? MyAdapter
                adapter?.apply {
                    clear()
                    addAll(data)
                    notifyDataSetChanged()
                }
                // Save contact details to SharedPreferences
            }
            resolver?.close()
        }
    }

}