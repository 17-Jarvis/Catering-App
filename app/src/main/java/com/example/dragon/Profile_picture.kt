package com.example.dragon

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.squareup.picasso.Picasso
import javax.annotation.Nonnull


class Profile_picture : AppCompatActivity() {

    private lateinit var progressbar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var storagereference: StorageReference
    private lateinit var firebaseuser:FirebaseUser
    val PICK_IMAGE_REQUEST = 1
    private lateinit var uriImage: Uri
    private lateinit var uri: Uri




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture)
        supportActionBar?.title = "Upload Profile Pic"
        var auth = FirebaseAuth.getInstance()
        var text_head = findViewById<TextView>(R.id.upload_head)
        var upload_button= findViewById<TextView>(R.id.upload_pic)
        progressbar=findViewById(R.id.progressbar)
        imageView=findViewById(R.id.image_profile)

        firebaseuser = auth.currentUser!!
        storagereference= FirebaseStorage.getInstance().getReference("DisplayPics")

        val uri: Uri? = firebaseuser.photoUrl
        // here we are using picaso because here the image is downloaded from the google database
        Picasso.with(this@Profile_picture).load(uri).into(imageView)

        upload_button.setOnClickListener{
            progressbar.visibility = View.VISIBLE
            uploadpic()
        }

        text_head.setOnClickListener{
            openFileChooser()
        }




    }
    private fun uploadpic(){
        if (uriImage!= null){
            val fileReference: StorageReference = storagereference.child(auth.currentUser!!.uid+"."+getfileExtension(uriImage) )
            // uploading to storage
            fileReference.putFile(uriImage).addOnSuccessListener {
                fun onDataChange(uploadTask: UploadTask.TaskSnapshot ,taskSnapshot: TaskSnapshot) {
                    fileReference.downloadUrl.addOnSuccessListener {
                        val download: Uri = uri
                        firebaseuser= auth.currentUser!!

                        // finally setting the image of the user
                        val profileUpdates = UserProfileChangeRequest.Builder().setPhotoUri(download).build()
                        firebaseuser.updateProfile(profileUpdates)

                    }
                    progressbar.visibility= View.GONE
                    Toast.makeText(this@Profile_picture, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    val intent=Intent(this@Profile_picture,Pro::class.java)
                    startActivity(intent)
                    finish()

                }
            }.addOnFailureListener(){
                fun onFailure(@Nonnull e: Exception) {
                    Toast.makeText(this@Profile_picture, e.message, Toast.LENGTH_SHORT).show()

                }
            }
        }else{
            progressbar.visibility=View.GONE
            Toast.makeText(this@Profile_picture, "No File Is Selected", Toast.LENGTH_SHORT).show()

        }
    }

    // obtaining file extension of the image
    private fun getfileExtension(uri: Uri): String? {
        val cR: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))


    }



    private fun openFileChooser(){
        var intent =Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            uriImage = data.data!!
            imageView.setImageURI(uriImage)

        }
    }
}