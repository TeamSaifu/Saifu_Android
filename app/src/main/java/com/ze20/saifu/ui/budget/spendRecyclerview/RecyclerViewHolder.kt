package com.ze20.saifu.ui.budget.spendRecyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // アイテムのViewを生成し保持する
    val nameView: TextView = itemView.findViewById(R.id.spendName)
    val priceView: TextView = itemView.findViewById(R.id.spendPrice)
}
