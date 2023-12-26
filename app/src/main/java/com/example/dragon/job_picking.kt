package com.example.dragon

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import storingDatabase

class job_picking : AppCompatActivity() {

    private lateinit var name: TextView
    private lateinit var mobile: TextView
    private lateinit var location: TextView
    private lateinit var members: TextView
    private lateinit var time: TextView
    private lateinit var service: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var acceptButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var acceptedJobsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_picking)

        // Initialize your views
        name = findViewById(R.id.dealer_name)
        mobile = findViewById(R.id.show_mobile)
        location = findViewById(R.id.show_location)
        members = findViewById(R.id.show_members)
        time = findViewById(R.id.show_time)
        service = findViewById(R.id.show_service)
        progressBar = findViewById(R.id.progressorbar)
        acceptButton = findViewById(R.id.accept_button)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize Firebase database reference for accepted jobs
        acceptedJobsRef = FirebaseDatabase.getInstance().getReference("AcceptedJobs")

        // Retrieve the current user
        val firebaseUser: FirebaseUser? = auth.currentUser

        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        } else {
            progressBar.visibility = View.VISIBLE
            showJobDetails(firebaseUser)
        }

        // Set click listener for the acceptButton
        acceptButton.setOnClickListener {
            // Handle button click, e.g., accept the job
            // You can add your logic here to update the database
            updateAcceptedJobs(firebaseUser!!)
        }
    }

    private fun showJobDetails(firebaseUser: FirebaseUser) {
        val userId: String? = firebaseUser.uid
        val referenceProfile: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Jobs")

        userId?.let {
            referenceProfile.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val readJobDetails: storingDatabase? =
                            snapshot.getValue(storingDatabase::class.java)
                        if (readJobDetails != null) {
                            // Set details to TextViews
                            name.text = readJobDetails.dname
                            mobile.text = readJobDetails.dnumber
                            location.text = readJobDetails.dlocation
                            members.text = readJobDetails.dmembers
                            time.text = readJobDetails.dtime
                            service.text = readJobDetails.dservice
                        }
                        progressBar.visibility = View.GONE
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@job_picking, "ERROR OCCURS", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun updateAcceptedJobs(firebaseUser: FirebaseUser) {
        val userId: String? = firebaseUser.uid
        userId?.let {
            val referenceProfile: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("Jobs")

            referenceProfile.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val acceptedJob: storingDatabase? =
                            snapshot.getValue(storingDatabase::class.java)
                        if (acceptedJob != null) {
                            // Add the accepted job to the AcceptedJobs node
                            acceptedJobsRef.child(userId).setValue(acceptedJob)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@job_picking,
                                            "Job Accepted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@job_picking,
                                            "Error accepting job",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@job_picking, "ERROR OCCURS", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
