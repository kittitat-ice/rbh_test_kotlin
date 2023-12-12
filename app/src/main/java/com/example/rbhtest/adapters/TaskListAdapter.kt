package com.example.rbhtest.adapters

import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rbhtest.R
import com.example.rbhtest.data.Task
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TaskListAdapter() : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {


  val dataSet = mutableListOf<Task>()

  fun submitList(newData: List<Task>) {
    dataSet.clear()
    dataSet.addAll(newData)
    notifyDataSetChanged()
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvDateHeader: TextView = view.findViewById(R.id.tvDateHeader)
    val tvTitle: TextView = view.findViewById(R.id.tvTitle)
    val tvDesc: TextView = view.findViewById(R.id.tvDesc)
    val cvDot: CardView = view.findViewById(R.id.cvDot)
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    // Create a new view, which defines the UI of the list item
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.list_item, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    // Get element from your dataset at this position and replace the
    // contents of the view with that element
    val colorDoing = ContextCompat.getColor(holder.itemView.context, R.color.doing)
    val colorTodo = ContextCompat.getColor(holder.itemView.context, R.color.todo)
    val colorDone = ContextCompat.getColor(holder.itemView.context, R.color.done)
//
    val thisRow = dataSet[position]
    val thisColor = when (thisRow.status) {
      "TODO" -> colorTodo
      "DOING" -> colorDoing
      "DONE" -> colorDone
      else -> {
        colorTodo
      }
    }

    holder.tvTitle.text = thisRow.title
    holder.tvDesc.text = thisRow.description
    holder.cvDot.setCardBackgroundColor(thisColor)

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val date = LocalDateTime.parse(thisRow.createdAt, DateTimeFormatter.ISO_DATE_TIME)

    if (position == 0) { // First row
      holder.tvDateHeader.visibility = View.VISIBLE
      holder.tvDateHeader.text = date.format(formatter)
      return
    }

    val prevDate =
      LocalDateTime.parse(dataSet[position - 1].createdAt, DateTimeFormatter.ISO_DATE_TIME)

    val sameYear = date.year == prevDate.year
    val sameDay = date.dayOfYear == prevDate.dayOfYear

    if (sameDay && sameYear) return // ignore same day

    holder.tvDateHeader.visibility = View.VISIBLE
    holder.tvDateHeader.text = date.format(formatter)


  }

  override fun getItemCount(): Int {
    return dataSet.size
  }
}