package com.ze20.saifu.ui.setting.Recyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R

class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // アイテムのViewを生成し保持する
    val mainView: TextView = itemView.findViewById(R.id.textViewMain)
    val subView: TextView = itemView.findViewById(R.id.textViewSub)
    // val categoryView: TextView = itemView.findViewById(R.id.categoryImage)
}
