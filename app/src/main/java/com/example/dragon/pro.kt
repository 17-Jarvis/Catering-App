package com.example.dragon


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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

class Pro : AppCompatActivity() {


    private lateinit var textViewFullName: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewDob: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewMobile: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var authProfile: FirebaseAuth
    private lateinit var image: ImageView
    // uploading image


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro)

        supportActionBar?.title = "Home"

        textViewFullName = findViewById(R.id.show_fullname)
        textViewEmail = findViewById(R.id.show_email)
        textViewDob = findViewById(R.id.show_dob)
        textViewLocation = findViewById(R.id.show_location)
        textViewMobile = findViewById(R.id.show_mobile)
        progressBar = findViewById(R.id.progressorbar)
        image=findViewById(R.id.imageview_profile)
        image.setOnClickListener{
            val intent=  Intent(this@Pro, Profile_picture::class.java)
            startActivity(intent)
        }
        authProfile = FirebaseAuth.getInstance()
        val firebaseUser: FirebaseUser? = authProfile.currentUser
        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        } else {
            progressBar.visibility = View.VISIBLE
            showUserProfile(firebaseUser)
        }
    }

    private fun showUserProfile(firebaseUser: FirebaseUser) {
        val userId: String? = firebaseUser.uid
        val referenceProfile: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Registered Users")

        userId?.let {
            referenceProfile.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val readUserDetails: ReadWriteUserDetails? =
                            snapshot.getValue(ReadWriteUserDetails::class.java)

                        if (readUserDetails != null) {
                            // Pass the TextView to the displayDetails method
                            readUserDetails.displayDetails(textViewFullName, textViewEmail, textViewDob, textViewMobile, textViewLocation)

                        }
                        progressBar.visibility = View.GONE

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Pro, "ERROR OCCURS", Toast.LENGTH_SHORT).show()

                    }

                })
        }
    }
}
