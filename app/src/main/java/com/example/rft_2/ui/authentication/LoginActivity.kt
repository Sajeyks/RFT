package com.example.rft_2.ui.authentication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.rft_2.HomeActivity
import com.example.rft_2.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        tv_register.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btn_login.setOnClickListener {
            when{
                TextUtils.isEmpty(login_email.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(login_password.text.toString().trim { it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = login_email.text.toString().trim { it <= ' ' }
                    val password: String = login_password.text.toString().trim { it <= ' ' }

                    // Log em in using Fire base
                    val dialog = setProgressDialog(this, "Logging you in ..")
                    dialog.show()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener{ task->
                            if (task.isSuccessful){
                                dialog.cancel()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "You are logged in successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // After Signing in

                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra(
                                    "user_id",
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()

                            } else{
                                // If login aint a success lets complain
                                dialog.cancel()
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                }
            }
        }

    }


  companion object {
      fun setProgressDialog(context: Context, message:String):AlertDialog {
          val llPadding = 30
          val ll = LinearLayout(context)
          ll.orientation = LinearLayout.HORIZONTAL
          ll.setPadding(llPadding, llPadding, llPadding, llPadding)
          ll.gravity = Gravity.CENTER
          var llParam = LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT,
              LinearLayout.LayoutParams.WRAP_CONTENT)
          llParam.gravity = Gravity.CENTER
          ll.layoutParams = llParam

          val progressBar = ProgressBar(context)
          progressBar.isIndeterminate = true
          progressBar.setPadding(0, 0, llPadding, 0)
          progressBar.layoutParams = llParam

          llParam = LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT)
          llParam.gravity = Gravity.CENTER
          val tvText = TextView(context)
          tvText.text = message
          tvText.setTextColor(Color.parseColor("#000000"))
          tvText.textSize = 14.toFloat()
          tvText.layoutParams = llParam

          ll.addView(progressBar)
          ll.addView(tvText)

          val builder = AlertDialog.Builder(context)
          builder.setCancelable(false)
          builder.setView(ll)

          val dialog = builder.create()
          val window = dialog.window
          if (window != null) {
              val layoutParams = WindowManager.LayoutParams()
              layoutParams.copyFrom(dialog.window?.attributes)
              layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
              layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
              dialog.window?.attributes = layoutParams
          }
          return dialog
      }
  }


}