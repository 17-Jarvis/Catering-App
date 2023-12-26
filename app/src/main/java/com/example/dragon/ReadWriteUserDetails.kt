package com.example.dragon

import android.widget.TextView

class ReadWriteUserDetails() {

    var username: String = ""
    var email: String = ""
    var dob: String = ""
    var mobile: String = ""
    var location: String = ""

    // No-argument constructor
    constructor(username: String, email: String, dob: String, mobile: String, location: String) : this() {
        this.username = username
        this.email = email
        this.dob = dob
        this.mobile = mobile
        this.location = location
    }

    // Other properties and methods as needed

    // Pass the TextView as a parameter to update the UI
    fun displayDetails(
        fullNameTextView: TextView,
        emailTextView: TextView,
        dobTextView: TextView,
        mobileTextView: TextView,
        locationTextView: TextView
    ) {
        // Update the UI within the main activity class
        fullNameTextView.text = "$username"
        emailTextView.text = "$email"
        dobTextView.text = "$dob"
        mobileTextView.text = "$mobile"
        locationTextView.text = "$location"
    }

    // Other println statements can be removed or adapted as needed
}

