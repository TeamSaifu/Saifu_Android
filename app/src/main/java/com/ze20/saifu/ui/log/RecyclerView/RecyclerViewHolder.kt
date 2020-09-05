package com.ze20.saifu.ui.log.RecyclerView

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // アイテムのViewを生成し保持する
    val dayView: TextView = itemView.findViewById(R.id.dayText)
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val nameView: TextView = itemView.findViewById(R.id.NameText)
    val priceView: TextView = itemView.findViewById(R.id.priceText)
}
