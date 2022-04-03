package com.example.rft_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tv_login.setOnClickListener{
            onBackPressed()
        }

        btn_register.setOnClickListener {
            when{
                TextUtils.isEmpty(register_email.text.toString().trim {it <= ' '}) ->{
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter Email.",
                        LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(register_password.text.toString().trim{it <= ' '}) ->{
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter Password.",
                        LENGTH_SHORT
                    ).show()
            }
                else -> {
                    val email: String = register_email.text.toString().trim { it <= ' ' }
                    val password: String = register_password.text.toString().trim() { it <= ' ' }

                    // Firebase stuff
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {  task ->

                                //Success
                                if (task.isSuccessful) {
                                    // Firebase Reg user
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Successfully Registered.",
                                        LENGTH_SHORT
                                    ).show()

                                    val intent =
                                        Intent(this@RegisterActivity, HomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    // Failure
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception.toString(),
                                        LENGTH_SHORT
                                    ).show()

                                }

                        }
                }
            }
        }

    }
}