package com.example.rft_2
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MyEvent(
    val EventOrganizer : String? = "",
    val EventId : String? = "",
    val EventName : String? = "",
    val EventPoster : String? = "",
    val EventDate : String? = "",
    val EventStartTime : String? = "",
    val EventEndingTime : String? = "",
    val EventTicketPrice : Int? = 0,
    val EventTillNumber : Int? = 0,
    val EventSummary : String? = ""

)
