package com.example.stopwatch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stopwatch.realm.Lap
import com.example.stopwatch.R

class TimeListAdapter(private var values: List<Lap>) : RecyclerView.Adapter<TimeListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.saved_time_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.savedTime.text = values[position].lap
    }

    override fun getItemCount(): Int {
        return values.size
    }

    fun setData(list: List<Lap>) {
        values = list
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var savedTime: TextView = itemView.findViewById(R.id.saved_time)
    }
}