package com.example.rft_2.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rft_2.ui.authentication.LoginActivity
import com.example.rft_2.databinding.FragmentProfileBinding
import com.example.rft_2.model.Organization
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    lateinit var mDataBase: DatabaseReference
    private val user = Firebase.auth.currentUser


    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textProfile
//        profileViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (user != null) {
            mDataBase = FirebaseDatabase.getInstance().getReference("Organizations").child(user.uid)
        }
        mDataBase.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val org = snapshot.getValue(Organization::class.java)

                if (org != null) {
                    tv_name.setText(org.Name)
                    tv_email.setText(org.Email)
                    tv_till.setText(org.TillNumber.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),
                    error.message, Toast.LENGTH_SHORT).show()
            }
        })

        buttonEditName.setOnClickListener {
            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Edit Name")

            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener{
                dialog, which ->
                var newName = input.text.toString()

                if (user != null) {
                    mDataBase = FirebaseDatabase.getInstance().getReference("Organizations").child(user.uid).child("name")
                }
                mDataBase.setValue(newName)
            })
            
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                dialog, which ->  dialog.cancel()
            })
            builder.show()

        }

        buttonEditTillNo.setOnClickListener{
            val builder1: AlertDialog.Builder = android.app.AlertDialog.Builder(requireContext())
            builder1.setTitle("Edit Till Number")

            val input1 = EditText(requireContext())
            input1.inputType = InputType.TYPE_CLASS_NUMBER
            builder1.setView(input1)

            builder1.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialog, which ->
                var newTill = input1.text.toString().toInt()

                if (user != null) {
                    mDataBase = FirebaseDatabase.getInstance().getReference("Organizations").child(user.uid).child("tillNumber")
                }
                mDataBase.setValue(newTill)


            })

            builder1.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                    dialog, which ->  dialog.cancel()
            })
            builder1.show()
        }





        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }






}