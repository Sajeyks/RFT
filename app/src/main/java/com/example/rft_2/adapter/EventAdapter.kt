package com.example.rft_2.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rft_2.BuildConfig.DEBUG
import com.example.rft_2.R
import com.example.rft_2.databinding.ItemListBinding
import com.example.rft_2.model.MyEvent
import com.example.rft_2.ui.event.EventDetailActivity

class EventAdapter(var c:Context, var eventList:ArrayList<MyEvent>
):RecyclerView.Adapter<EventAdapter.EventViewHolder>()

{
    inner class EventViewHolder(var v:ItemListBinding): RecyclerView.ViewHolder(v.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = DataBindingUtil.inflate<ItemListBinding>(inflater, R.layout.item_list, parent,
            false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val detailsList = eventList[position]

        holder.v.isEvent = eventList[position]
        holder.v.root.setOnClickListener{
            val eventid = detailsList.EventId
            val eventorganizer = detailsList.EventOrganizer
            val eventdate = detailsList.EventDate
            val eventstarttime = detailsList.EventStartTime
            val eventendtime = detailsList.EventEndingTime
            val eventticketprice = detailsList.EventTicketPrice
            val eventavailabletickets = detailsList.EventTAvailableTickets
            val eventname = detailsList.EventName
            val eventposter = detailsList.EventPoster
            val eventsummary = detailsList.EventSummary
            val eventvenue = detailsList.EventVenue

            val eventattendees : HashMap<String, Any> = detailsList.EventAttendees as HashMap<String, Any>


            val mIntent = Intent(c, EventDetailActivity::class.java)

            mIntent.putExtra("id",eventid )
            mIntent.putExtra("organizer",eventorganizer)
            mIntent.putExtra("date",eventdate )
            mIntent.putExtra("start",eventstarttime )
            mIntent.putExtra("end",eventendtime )
            mIntent.putExtra("price",eventticketprice )
            mIntent.putExtra("totaltickets",eventavailabletickets )
            mIntent.putExtra("name",eventname )
            mIntent.putExtra("poster",eventposter )
            mIntent.putExtra("summary",eventsummary )
            mIntent.putExtra("venue", eventvenue)
            mIntent.putExtra("attendees", eventattendees)

            c.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }


}