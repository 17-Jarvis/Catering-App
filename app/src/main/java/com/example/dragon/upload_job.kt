package com.example.dragon

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import storingDatabase

class upload_job : AppCompatActivity() {

    private lateinit var dealername: TextView
    private lateinit var contact: TextView
    private lateinit var location: TextView
    private lateinit var members: TextView
    private lateinit var time: TextView
    private lateinit var service: TextView
    private lateinit var post: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_job)
        supportActionBar?.title = "Upload_job"
        Toast.makeText(this@upload_job, "You can register now", Toast.LENGTH_SHORT).show()

        dealername = findViewById(R.id.edit_name)
        contact = findViewById(R.id.edit_mobile)
        location = findViewById(R.id.edit_location)
        members = findViewById(R.id.edit_members)
        time = findViewById(R.id.edit_Time)
        service = findViewById(R.id.edit_serve)
        post = findViewById(R.id.reg_button)

        post.setOnClickListener {
            val name = dealername.text.toString().trim()
            val contact = contact.text.toString().trim()
            val location = location.text.toString().trim()
            val members = members.text.toString().trim()
            val time = time.text.toString().trim()
            val service = service.text.toString().trim()

            if (name.isNotEmpty() && contact.isNotEmpty() && location.isNotEmpty() &&
                members.isNotEmpty() && time.isNotEmpty() && service.isNotEmpty()
            ) {
                // All fields are non-empty, proceed with registration
                registerUser(name, contact, location, members, time, service)
            } else {
                Toast.makeText(this@upload_job, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(
        name: String,
        contact: String,
        location: String,
        members: String,
        time: String,
        service: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val writeUserDetails = storingDatabase(name, contact, location, members, time, service)
            val referenceProfile = FirebaseDatabase.getInstance().getReference("jobs")
            referenceProfile.child(userId).setValue(writeUserDetails)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@upload_job,
                            "Job Uploaded Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, view_point::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@upload_job, "Job Uploaded Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(this@upload_job, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
