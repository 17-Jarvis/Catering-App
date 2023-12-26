package com.example.dragon

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
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

class confirmjob : AppCompatActivity() {

    private lateinit var confirmedJobsListView: ListView
    private lateinit var confirmedJobsList: MutableList<storingDatabase>
    private lateinit var confirmedJobsAdapter: ArrayAdapter<storingDatabase>
    private lateinit var acceptedJobsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmjob)

        confirmedJobsListView = findViewById(R.id.acc_job)
        confirmedJobsList = mutableListOf()
        confirmedJobsAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, confirmedJobsList)
        confirmedJobsListView.adapter = confirmedJobsAdapter

        acceptedJobsRef = FirebaseDatabase.getInstance().getReference("AcceptedJobs")
        auth = FirebaseAuth.getInstance()

        loadConfirmedJobs()

        // Set item click listener for the ListView
        confirmedJobsListView.setOnItemClickListener { _, _, position, _ ->
            // Get the selected job details
            val selectedJob: storingDatabase = confirmedJobsList[position]

            // Start a new activity to display the details of the selected job
            val intent = Intent(this@confirmjob, job_picking::class.java)
            intent.putExtra("jobDetails", selectedJob)
            startActivity(intent)
        }
    }

    private fun loadConfirmedJobs() {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            acceptedJobsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (jobSnapshot in snapshot.children) {
                        val acceptedJobDetails: storingDatabase? =
                            jobSnapshot.getValue(storingDatabase::class.java)

                        if (acceptedJobDetails != null) {
                            confirmedJobsList.add(acceptedJobDetails)
                        }
                    }

                    confirmedJobsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@confirmjob,
                        "Error loading accepted jobs",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            Toast.makeText(this@confirmjob, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
