package com.example.rft_2.model
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Organization {
    var Name: String? = null
    var Email: String? = null
    var TillNumber: Int? = 0
    var BookedEvents: MutableMap<String, Boolean>? = HashMap()

    constructor(){}

    constructor(
        Name: String?,
        Email: String?,
        TillNumber: Int?,
        BookedEvents: MutableMap<String, Boolean>?
    )
    {
        this.Name = Name
        this.Email = Email
        this.TillNumber = TillNumber
        this.BookedEvents = BookedEvents

    }

}