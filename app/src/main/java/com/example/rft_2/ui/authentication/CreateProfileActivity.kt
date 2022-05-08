package com.example.rft_2.ui.authentication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.rft_2.HomeActivity
import com.example.rft_2.R
import com.example.rft_2.model.Organization
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_profile.*

class CreateProfileActivity : AppCompatActivity() {

    private val user = Firebase.auth.currentUser

    // Get a reference to the database
    private val database = Firebase.database
    private val myRef = database.getReference("Organizations")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_create_profile)

        btnSaveButton.setOnClickListener {
            set_profile(getProgressDrawable(this))
            Toast.makeText(this, "Profile created!", Toast.LENGTH_SHORT).show()

            val intent =
                Intent(this@CreateProfileActivity, HomeActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }

    }

    fun set_profile(progressDrawable: CircularProgressDrawable) {
        val id = user?.uid
        val name = Et_Name.text.toString()
        val till = Et_till.text.toString().toInt()
        val email = user?.email
        val bookedEvents = mutableMapOf<String, Boolean>()

        if (name.isNullOrEmpty()) {
            Toast.makeText(this, "All fields required!", Toast.LENGTH_LONG).show()
        }
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "All fields required!", Toast.LENGTH_LONG).show()
        } else {
            val organization = Organization(name, email, till, bookedEvents)
            myRef.child(id!!).setValue(organization)
        }
    }


    private fun getProgressDrawable(c: Context): CircularProgressDrawable {
        return CircularProgressDrawable(c).apply {
            strokeWidth = 3f
            centerRadius = 40f
            start()
        }
    }
}