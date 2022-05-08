package com.example.rft_2.model
import android.net.Uri
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class MyEvent {

    var EventId: String? = null
    var EventOrganizer: String? = null
    var EventName: String? = null
    var EventPoster: String? = null
    var EventDate: String? = null
    var EventStartTime: String? = null
    var EventEndingTime: String? = null
    var EventTicketPrice: Int? = 0
    var EventSummary: String? = null
    var EventVenue : String? = null
    var EventTAvailableTickets: Int? = 0
    var EventAttendees: MutableMap<String, Any> ? = HashMap()

    constructor(){}

    constructor(
        EventId: String?,
        EventOrganizer: String?,
        EventName: String?,
        EventPoster: String?,
        EventDate: String?,
        EventStartTime: String?,
        EventEndingTime: String?,
        EventTicketPrice: Int?,
        EventSummary: String?,
        EventVenue: String?,
        EventTAvailableTickets: Int?,
        EventAttendees: MutableMap<String, Any>?
    ){
        this.EventId = EventId
        this.EventOrganizer = EventOrganizer
        this.EventName = EventName
        this.EventPoster = EventPoster
        this.EventDate = EventDate
        this.EventStartTime = EventStartTime
        this.EventEndingTime = EventEndingTime
        this.EventTicketPrice = EventTicketPrice
        this.EventSummary = EventSummary
        this.EventVenue = EventVenue
        this.EventTAvailableTickets = EventTAvailableTickets
        this.EventAttendees = EventAttendees
    }

}

