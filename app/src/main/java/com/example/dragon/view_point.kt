package com.example.dragon

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import storingDatabase

class view_point : AppCompatActivity() {

    private lateinit var jobListView: ListView
    private lateinit var jobList: MutableList<String>
    private lateinit var jobAdapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_point)
        supportActionBar?.title = "Job List"

        jobListView = findViewById(R.id.job_list)
        jobList = mutableListOf()
        jobAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jobList)
        jobListView.adapter = jobAdapter

        loadJobs()
    }

    private fun loadJobs() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val referenceProfile: DatabaseReference =
                FirebaseDatabase.getInstance().getReference("jobs")

            referenceProfile.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    jobList.clear()

                    for (jobSnapshot in snapshot.children) {
                        val jobDetails: storingDatabase? =
                            jobSnapshot.getValue(storingDatabase::class.java)

                        if (jobDetails != null) {
                            val jobInfo = "Location: ${jobDetails.dlocation}, Time: ${jobDetails.dtime}"
                            jobList.add(jobInfo)
                        }
                    }

                    jobAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@view_point, "Error loading jobs", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@view_point, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
