package com.example.rft_2

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_event.*
import java.io.IOException
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private val user = Firebase.auth.currentUser


// Get a reference to the database
    private val database = Firebase.database
    private val myRef = database.getReference("Events")

    val currentTimestamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

//        event_organizer.text = user?.photoUrl.toString()
//        event_organizer.visibility = View.VISIBLE

        btn_select_image.setOnClickListener {
            if( ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //select image
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, 222)

            }else{
                //Request it
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    121
                )
            }
        }


        btn_add_event.setOnClickListener {
            uploadPoster()
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 121){
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 222)

        }else{
            Toast.makeText(this,
            "The app needs permission to get image from phone",
            Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == 222){
                if (data != null){
                    try {
                        mSelectedImageFileUri = data.data!!
                        event_poster.setImageURI(mSelectedImageFileUri)

                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@CreateEventActivity,
                            "Image selection Failed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

    // upload image
    private fun uploadPoster(){
        if (mSelectedImageFileUri != null){
            val imageExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(mSelectedImageFileUri!!))

            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "Image" + System.currentTimeMillis() + "." + imageExtension
            )

            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imageLink = url.toString()

                            // pass the picture link to the event creator function
                            createEvent(imageLink)

                            Toast.makeText(this,
                                "upload success",
                                Toast.LENGTH_LONG).show()

                        }.addOnFailureListener{ exception ->
                            Toast.makeText(this,
                                "Image upload failed! ",
                                Toast.LENGTH_LONG).show()
                        }

                }
        }


    }

    //date
    fun DatePicker.getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }

    //Add Event
    private fun createEvent( posterURL : String){
        val eventOrganizer = user?.email
        val eventName = et_event_name.text.toString()
        val eventId = eventName + currentTimestamp
        val eventDate = event_datePicker.getDate().toString()
        val eventStartTime = event_starting_time.text.toString()
        val eventFinishingTime = event_finishing_time.text.toString()
        val eventTicketPrice = event_ticket_price.text.toString().toInt()
        val eventTillNumber = event_till.text.toString().toInt()
        val eventSummary = event_summary.text.toString()


        val event = MyEvent(
            eventOrganizer,eventId, eventName, posterURL, eventDate, eventStartTime,
            eventFinishingTime, eventTicketPrice, eventTillNumber, eventSummary
        )

        myRef.child(eventId).setValue(event).addOnSuccessListener {
            Toast.makeText(
                this,
                "Event created sucessfully",
                Toast.LENGTH_LONG
            ).show()
        }
    }





}