package com.example.rft_2.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rft_2.R
import com.example.rft_2.adapter.EventAdapter
import com.example.rft_2.databinding.FragmentHomeBinding
import com.example.rft_2.model.MyEvent
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    // Get a reference to the database


    lateinit var mDataBase:DatabaseReference

    private lateinit var eventList:ArrayList<MyEvent>
    private lateinit var mAdapter: EventAdapter

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAdapter = EventAdapter(requireContext(), arrayListOf())
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** initialize**/
        eventList = arrayListOf<MyEvent>()
        mAdapter = EventAdapter(requireContext(), eventList)
        recyclerHome.layoutManager = LinearLayoutManager(requireContext())
        recyclerHome.setHasFixedSize(true)
        recyclerHome.adapter = mAdapter

        /** get data from Firebase **/
        getEventData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** the function to get the data **/
    private fun getEventData(){
        mDataBase = FirebaseDatabase.getInstance().getReference("Events")
        mDataBase.addValueEventListener(object :ValueEventListener{
            
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()

                if (snapshot.exists()){
                    for (eventSnapshot in snapshot.children){
                        val evnt = eventSnapshot.getValue(MyEvent::class.java)

                        eventList.add(0,evnt!!)
                    }
                    recyclerHome.adapter = mAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),
                error.message, Toast.LENGTH_SHORT).show()         }
        })


    }
}

