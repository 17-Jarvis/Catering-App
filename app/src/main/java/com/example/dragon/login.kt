package com.example.dragon

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth=FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_pass)
        btnLogin = findViewById(R.id.btnlogin)
        btnSignup = findViewById(R.id.btnsignup)

        btnSignup.setOnClickListener{
            val intent = Intent(this,Signup::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{
            val email = edtEmail.text.toString()
            val password= edtPassword.text.toString()

            Login(email,password)
        }


    }


@SuppressLint("NotConstructor")
    private fun Login(email: String,password:String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val intent =Intent(this@login,pro_picking::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this@login,"User does not exist",Toast.LENGTH_SHORT).show()

                }
        }
    }



}