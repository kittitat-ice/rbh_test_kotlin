package com.example.rbhtest.utils

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback(context: Context, dragDir: Int, swipeDir: Int) :
  ItemTouchHelper.SimpleCallback(dragDir, swipeDir) {
  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }
}