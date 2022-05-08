package com.example.rft_2.adapter

import android.app.Application
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseHandler : Application() {
    override fun onCreate(){
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)

    }
}


