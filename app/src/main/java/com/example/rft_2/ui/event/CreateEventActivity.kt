package com.example.rft_2.ui.event

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import com.example.rft_2.HomeActivity
import com.example.rft_2.R

import com.example.rft_2.model.MyEvent
import com.example.rft_2.ui.authentication.LoginActivity
import com.example.rft_2.ui.authentication.LoginActivity.Companion.setProgressDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.activity_edit_event_details.*
import java.io.IOException
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private val user = Firebase.auth.currentUser

    // Get a reference to the database
    private val database = Firebase.database
    private val myRef = database.getReference("Events")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        event_datePicker.minDate = System.currentTimeMillis()
        /** time picker **/
        event_starting_time.setIs24HourView(true)
        event_finishing_time.setIs24HourView(true)

        btn_select_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //select image
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                startActivityForResult(galleryIntent, 222)

            } else {
                //Request it
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    121
                )
            }
        }


        btn_add_event.setOnClickListener {
            val dialog =setProgressDialog(this, "Creating Event...")
            dialog.show()
            uploadPoster()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 121) {
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, 222)

        } else {
            Toast.makeText(
                this,
                "The app needs permission to get image from phone",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        event_poster.setImageURI(mSelectedImageFileUri)

                    } catch (e: IOException) {
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


    /** upload image **/
    @RequiresApi(Build.VERSION_CODES.M)
    private fun uploadPoster() {

        if (mSelectedImageFileUri != null) {
            val imageExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(mSelectedImageFileUri!!))

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "Image" + System.currentTimeMillis() + "." + imageExtension
            )

            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imageLink = url.toString()

                            // pass the picture link to the event creator function
                            createEvent(imageLink)

                            Toast.makeText(
                                this,
                                "upload success",
                                Toast.LENGTH_LONG
                            ).show()

                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Image upload failed! ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                }
        } else{

        }


    }


    //date
    fun DatePicker.getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }

    //Add Event
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createEvent(posterURL: String) {
        val id = myRef.push().key
        val eventId = id.toString()
        val eventOrganizer = user?.email
        val eventName = et_event_name.text.toString()
        val dateFormated = SimpleDateFormat("dd/MM/yyyy").format(event_datePicker.getDate())
        val eventDate = dateFormated.toString()
        val eventStartTime = getStartTime()
        val eventFinishingTime = getFinishTime()
        val eventTicketPrice = event_ticket_price.text.toString().toInt()
        val eventSummary = event_summary.text.toString()
        val eventVenue = event_venue.text.toString()
        val eventAvailableTickets = event_available_tickets.text.toString().toInt()
        val eventAttendees = mutableMapOf<String, Any>()

        val event = MyEvent(
            eventId ,eventOrganizer, eventName, posterURL, eventDate, eventStartTime,
            eventFinishingTime, eventTicketPrice, eventSummary, eventVenue ,eventAvailableTickets, eventAttendees
        )

        myRef.child(id!!).setValue(event).addOnSuccessListener {

            Toast.makeText(
                this,
                "Event created successfully",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this@CreateEventActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)


        }.addOnCanceledListener {
            Toast.makeText(
                this,
                "Something went wrong!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getStartTime(): String{
        val hour = event_starting_time.getHour()
        var Hour = ""
        if (hour <10 ){
            Hour = "0" + hour.toString()
        }else{
            Hour = hour.toString()
        }

        var min = event_starting_time.getMinute()
        var Min = ""
        if (min <10 ){
            Min = "0" + min.toString()
        }else{
            Min = min.toString()
        }
        val time = Hour +":"+ Min
        return time
    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getFinishTime(): String{
        val hour = event_finishing_time.getHour()
        var Hour = ""
        if (hour <10 ){
            Hour = "0" + hour.toString()
        }else{
            Hour = hour.toString()
        }

        var min = event_finishing_time.getMinute()
        var Min = ""
        if (min <10 ){
            Min = "0" + min.toString()
        }else{
            Min = min.toString()
        }
        val time = Hour +":"+ Min
        return time
    }

    companion object{


    }

}




