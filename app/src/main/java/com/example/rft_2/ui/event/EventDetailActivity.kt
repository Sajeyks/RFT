package com.example.rft_2.ui.event

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.rft_2.R
import com.example.rft_2.Uitel.getProgressDrawable
import com.example.rft_2.Uitel.loadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_event_details.*
import kotlinx.android.synthetic.main.activity_event_detail.*
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Toast

class EventDetailActivity : AppCompatActivity() {

    private val user = Firebase.auth.currentUser
    // Get a reference to the database
    private val email = user!!.email
    private val database = Firebase.database
    private val myRef = database.getReference("Events")
    var eventAvailableTickets = 0
    private var eventattendees : HashMap<String, Any> = HashMap()
    private var currentprogress = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        window.statusBarColor = ContextCompat.getColor(this, R.color.grey)

        val eventIntent = intent

        val eventId = eventIntent.getStringExtra("id")
        val eventOrganizer = eventIntent.getStringExtra("organizer")
        val eventDate = eventIntent.getStringExtra("date")
        val eventstartTime = eventIntent.getStringExtra("start")
        val eventendTime = eventIntent.getStringExtra("end")
        val eventTicketPrice = eventIntent.getIntExtra("price", 0)
            eventAvailableTickets = eventIntent.getIntExtra("totaltickets", 0)
        val eventName = eventIntent.getStringExtra("name")
        val eventPoster = eventIntent.getStringExtra("poster")
        val eventSummary = eventIntent.getStringExtra("summary")
        val eventVenue = eventIntent.getStringExtra("venue")


        eventattendees = eventIntent.getSerializableExtra("attendees") as HashMap<String, Any>

        /** call the texts and images **/
        name.text = eventName
        img.loadImage(eventPoster, getProgressDrawable(this))
        info.text = eventSummary
        tv_eventDate.text = eventDate
        tv_eventStartTime.text = eventstartTime
        tv_eventEndingTime.text = eventendTime
        tv_Organizers.text = eventOrganizer
        venue.text = eventVenue


        if (eventTicketPrice == 0) {
            tv_TicketPrice.text = "Free"
        } else {
            tv_TicketPrice.text = eventTicketPrice.toString()
        }

        if (user != null) {
            if (eventOrganizer == user.email.toString()) {
                admin_view.visibility = View.VISIBLE
                btn_book_event.visibility = View.INVISIBLE
            }
        }

        rewardTicket()


        edit_event.setOnClickListener {
            val mIntent = Intent(this@EventDetailActivity, EditEventDetailsActivity::class.java)

            mIntent.putExtra("id", eventId)
            mIntent.putExtra("organizer", eventOrganizer)
            mIntent.putExtra("date", eventDate)
            mIntent.putExtra("start", eventstartTime)
            mIntent.putExtra("end", eventendTime)
            mIntent.putExtra("price", eventTicketPrice)
            mIntent.putExtra("totaltickets", eventAvailableTickets)
            mIntent.putExtra("name", eventName)
            mIntent.putExtra("poster", eventPoster)
            mIntent.putExtra("summary", eventSummary)
            mIntent.putExtra("venue", eventVenue)

            startActivity(mIntent)
        }

        val myRefAtendees = database.getReference("Events").child(eventId!!).child("eventAttendees")
        val attendees : MutableMap<String, Any> = mutableMapOf()
        if (email != null) {
            attendees[user!!.uid] = true
        }


        btn_book_event.setOnClickListener {
            myRefAtendees.updateChildren(attendees).
                    addOnSuccessListener {
                        Toast.makeText(this,"Booked event", Toast.LENGTH_SHORT).show()
                        rewardTicket()

                    }
        }
        updateProgressResult()

        if(eventattendees.size == eventAvailableTickets){
            btn_book_event.visibility = View.INVISIBLE
        }

        cancel_event.setOnClickListener {
          val D =  myRef.child(eventId)
            D.removeValue().addOnSuccessListener {
                Toast.makeText(this,"Event Deleted Succesfully", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun rewardTicket(){
        for (id in eventattendees.keys){
            if (user != null) {
                if( user.uid == id  ){
                    btn_book_event.visibility = View.INVISIBLE
                    tv_Ticket.visibility = View.VISIBLE
                }
            }
        }
    }

    fun updateProgressResult(){
        progressbar.max= eventAvailableTickets
        while(currentprogress < eventattendees.size){
            currentprogress ++
        }
        progressbar.progress = currentprogress
        text_progress.text = "$currentprogress/$eventAvailableTickets"
    }

}