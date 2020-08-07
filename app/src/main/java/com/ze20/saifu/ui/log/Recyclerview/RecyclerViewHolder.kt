package com.ze20.saifu.ui.log.Recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val dayView: TextView = itemView.findViewById(R.id.dayText)
    val categoryView: TextView = itemView.findViewById(R.id.categoryNameText)
    val priceView: TextView = itemView.findViewById(R.id.priceText)
}
