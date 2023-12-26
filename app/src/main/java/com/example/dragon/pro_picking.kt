package com.example.dragon

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class pro_picking : AppCompatActivity() {

    private lateinit var edit_name: TextView
    private lateinit var edit_email: TextView
    private lateinit var edit_dob: TextView
    private lateinit var edit_mobile: TextView
    private lateinit var edit_location: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var user_type: RadioGroup
    private lateinit var user_service: RadioButton
    private lateinit var user_dealer: RadioButton
    private lateinit var button_reg:Button
    private lateinit var password:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_picking)

        supportActionBar?.title = "Register"
        Toast.makeText(this@pro_picking, "You can register now", Toast.LENGTH_SHORT).show()


        edit_name=findViewById(R.id.edit_name)
        edit_email=findViewById(R.id.edit_email)
        edit_dob=findViewById(R.id.edit_dob)
        edit_mobile=findViewById(R.id.edit_mobile)
        edit_location=findViewById(R.id.edit_location)
        user_type=findViewById(R.id.edit_type)
        user_type.clearCheck()
        button_reg=findViewById(R.id.reg_button)
        user_service=findViewById(R.id.radio_service)
        user_dealer=findViewById(R.id.radio_dealer)
        password=findViewById(R.id.edit_pass)
        progressBar=findViewById(R.id.pro_progressbar)

        button_reg.setOnClickListener {
            val name=edit_name.text.toString().trim()
            val email = edit_email.text.toString().trim()
            val dob=edit_dob.text.toString().trim()
            val mobile=edit_mobile.text.toString().trim()
            val location=edit_location.text.toString().trim()
            val password=password.text.toString().trim()

            if (isValidEmail(email)) {

                if (TextUtils.isEmpty(name.toString())) {
                    Toast.makeText(this@pro_picking, "Enter Your Name", Toast.LENGTH_SHORT).show()
                    edit_name.error = "User name is Required"
                    edit_name.requestFocus()
                } else if (TextUtils.isEmpty((email.toString()))) {
                    Toast.makeText(this@pro_picking, "Enter Your Email", Toast.LENGTH_SHORT).show()
                    edit_email.error = "Valid Email Id Is Required"
                    edit_email.requestFocus()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this@pro_picking, "Enter The Valid Email Id", Toast.LENGTH_SHORT)
                        .show()
                    edit_email.error = "Valid Email Id Is Required"
                    edit_email.requestFocus()
                } else if (TextUtils.isEmpty(dob.toString())) {
                    Toast.makeText(this@pro_picking, "Enter Your DOB", Toast.LENGTH_SHORT).show()
                    edit_dob.error = "User DOB is Required"
                    edit_dob.requestFocus()
                } else if (TextUtils.isEmpty(mobile.toString())) {
                    Toast.makeText(this@pro_picking, "Enter Your Mobile Number", Toast.LENGTH_SHORT)
                        .show()
                    edit_mobile.error = "Mobile Number is Required"
                    edit_mobile.requestFocus()
                } else if (TextUtils.isEmpty(location.toString())) {
                    Toast.makeText(this@pro_picking, "Enter Your Location", Toast.LENGTH_SHORT)
                        .show()
                    edit_location.error = "User Location is Required"
                    edit_location.requestFocus()
                } else if (edit_mobile.length() != 10) {
                    Toast.makeText(this@pro_picking, "Re-Enter Mobile Number", Toast.LENGTH_SHORT)
                        .show()
                    edit_mobile.error = "Invalid Number"
                    edit_mobile.requestFocus()
                } else if (password.length < 6) {
                    Toast.makeText(this@pro_picking, "Re-Enter The PassWord", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val user = when {
                        user_service.isChecked -> user_service.text.toString()
                        user_dealer.isChecked -> user_dealer.text.toString()
                        else -> {
                            Toast.makeText(this@pro_picking, "User Type Must Be selected", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener // Don't proceed if no radio button is selected
                        }
                    }
                    registerUser(edit_name, edit_email, password, edit_dob, user_type, edit_mobile, edit_location, user)
                }
            }else{
                Toast.makeText(this@pro_picking, "Invalid Email Format", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(
        editName: TextView?,
        editEmail: TextView?,
        password: String,
        editDob: TextView?,
        userType: RadioGroup?,
        editMobile: TextView?,
        editLocation: TextView?,
        userSelection: String// Change the type to String
    ) {
        val trimmedEmail = editEmail?.text.toString().trim()

        val trimmedName = editName?.text.toString().trim()
        val trimmedDOB = editDob?.text.toString().trim()
        val trimmedMobile = editMobile?.text.toString().trim()
        val trimmedLocation = editLocation?.text.toString().trim()


        val trimmedPassword = password.trim()
        Log.d("Registration", "Email: '$trimmedEmail', Password: '$trimmedPassword'")
        var auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@pro_picking, "User Registered Successful", Toast.LENGTH_SHORT).show()
                    val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    firebaseUser?.sendEmailVerification()

                    //storing in firebase
                    val writeUserDetails = ReadWriteUserDetails(trimmedName, trimmedEmail, trimmedDOB, trimmedMobile ,trimmedLocation)

                    val referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users")
                    referenceProfile.child(firebaseUser?.uid ?: "").setValue(writeUserDetails)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.sendEmailVerification()

                                Toast.makeText(this@pro_picking, "User registered successfull ", Toast.LENGTH_SHORT).show()
                                if (firebaseUser?.isEmailVerified == true) {
                                    val intent = Intent(this, upload_job::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                                Intent.FLAG_ACTIVITY_NEW_TASK

                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this@pro_picking, "Please Verify Your email", Toast.LENGTH_SHORT).show()
                                }

                            } else {
                                Toast.makeText(this@pro_picking, "User registration  Failed ", Toast.LENGTH_SHORT).show()
                            }
                        }

                } else {
                    Log.e("Registration", "Registration failed: ${task.exception}")
                }
            }
    }
}