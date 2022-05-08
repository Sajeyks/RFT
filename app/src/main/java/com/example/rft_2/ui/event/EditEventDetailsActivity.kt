package com.example.rft_2.ui.event

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rft_2.HomeActivity
import com.example.rft_2.R
import com.example.rft_2.Uitel.getProgressDrawable
import com.example.rft_2.Uitel.loadImage
import com.example.rft_2.model.MyEvent
import com.example.rft_2.ui.authentication.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.activity_edit_event_details.*
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.*
import kotlin.math.log

class EditEventDetailsActivity : AppCompatActivity() {

    private var mSelectedImageFileUri: Uri? = null
    private val user = Firebase.auth.currentUser
    // Get a reference to the database
    private val database = Firebase.database
    private val myRef = database.getReference("Events")

    private var eventPoster: String = ""
    private var eventId: String = ""

    private var eventattendees : HashMap<String, Any> = HashMap()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event_details)
        /** preps **/
        event_datePicker_edit.minDate = System.currentTimeMillis()
        /** time picker **/
        event_starting_time_edit.setIs24HourView(true)
        event_finishing_time_edit.setIs24HourView(true)

        val eventIntent = intent

        eventId = eventIntent.getStringExtra("id").toString()
        val eventOrganizer = eventIntent.getStringExtra("organizer")

        val eventDate = eventIntent.getStringExtra("date")
        val eventstartTime = eventIntent.getStringExtra("start")
        val eventendTime = eventIntent.getStringExtra("end")
        val eventTicketPrice = eventIntent.getIntExtra("price",0)
        val eventAvailableTickets = eventIntent.getIntExtra("totaltickets",0)
        val eventName = eventIntent.getStringExtra("name")
        eventPoster = eventIntent.getStringExtra("poster").toString()
        val eventSummary = eventIntent.getStringExtra("summary")
        val eventVenue = eventIntent.getStringExtra("venue")

        eventattendees = eventIntent.getSerializableExtra("attendees") as HashMap<String, Any>

        /** call the texts & image **/
        et_event_name_edit.setText(eventName)
        Picasso.with(this).load(eventPoster.toString()).into(event_poster_edit)
        event_ticket_price_edit.setText(eventTicketPrice.toString())
        event_available_tickets_edit.setText(eventAvailableTickets.toString())
        event_summary_edit.setText(eventSummary)
        event_venue_edit.setText(eventVenue)


        /** date **/

            if (eventDate != null) {
            val defaultDate = eventDate.toString().split(Regex("/"))
            val dd = defaultDate[0].toInt()
            val mm = defaultDate[1].toInt()
            val yy = defaultDate[2].toInt()

                event_datePicker_edit.updateDate(yy,mm -1,dd)

        }

        // time
        if (eventstartTime!=null && eventendTime!=null){
            val defStart = eventstartTime.toString().split(Regex(":"))
            val hhs = defStart[0].toInt()
            val mms = defStart[1].toInt()

            event_starting_time_edit.hour= hhs
            event_starting_time_edit.minute= mms

            val defStartf = eventendTime.toString().split(Regex(":"))
            val hhf = defStartf[0].toInt()
            val mmf = defStartf[1].toInt()

            event_finishing_time_edit.hour= hhf
            event_finishing_time_edit.minute= mmf


        }

        btn_select_image_edit.setOnClickListener {

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


        btn_update_event.setOnClickListener {
            val dialog = LoginActivity.setProgressDialog(this, "Updating Event...")
            if (mSelectedImageFileUri == null) {
                dialog.show()
                updateEvent(eventPoster)
                }
            else{
                dialog.show()
                uploadPoster()

            }
        }

    }
    /** Permission for image selection **/
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
                        event_poster_edit.setImageURI(mSelectedImageFileUri)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@EditEventDetailsActivity,
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

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(eventPoster)

            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imageLink = url.toString()

                            // pass the picture link to the event creator function
                            updateEvent(imageLink)

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


    /** for getting the date **/
    fun DatePicker.getDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        return calendar.time
    }

    //Add Event
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateEvent(posterURL: String) {
        val eventOrganizer = user?.email
        val eventName = et_event_name_edit.text.toString()
        val dateFormated = SimpleDateFormat("dd/MM/yyyy").format(event_datePicker_edit.getDate())
        val eventDate = dateFormated.toString()
        val eventStartTime = getStartTime()
        val eventFinishingTime = getFinishTime()
        val eventTicketPrice = event_ticket_price_edit.text.toString().toInt()
        val eventSummary = event_summary_edit.text.toString()
        val eventAvailableTickets = event_available_tickets_edit.text.toString().toInt()
        val eventVenue = event_venue_edit.text.toString()

        val event = MyEvent(
            eventId ,eventOrganizer, eventName, posterURL, eventDate, eventStartTime,
            eventFinishingTime, eventTicketPrice, eventSummary, eventVenue,eventAvailableTickets, eventattendees
        )

        myRef.child(eventId!!).setValue(event).addOnSuccessListener {
            Toast.makeText(
                this,
                "Event updated sucessfully",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this@EditEventDetailsActivity, HomeActivity::class.java)
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
        val hour = event_starting_time_edit.getHour()
        var Hour = ""
        if (hour <10 ){
            Hour = "0" + hour.toString()
        }else{
            Hour = hour.toString()
        }

        var min = event_starting_time_edit.getMinute()
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
        val hour = event_finishing_time_edit.getHour()
        var Hour = ""
        if (hour <10 ){
            Hour = "0" + hour.toString()
        }else{
            Hour = hour.toString()
        }

        var min = event_finishing_time_edit.getMinute()
        var Min = ""
        if (min <10 ){
            Min = "0" + min.toString()
        }else{
            Min = min.toString()
        }
        val time = Hour +":"+ Min
        return time
    }
}