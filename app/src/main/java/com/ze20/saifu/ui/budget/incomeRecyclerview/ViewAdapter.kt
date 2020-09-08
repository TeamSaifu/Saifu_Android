package com.ze20.saifu.ui.budget.incomeRecyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R

// ViewHolderを生成しViewModelをセットする
// 詳しくはココ参照→ https://qiita.com/saiki-ii/items/78ed73134784f3e5db7e

class ViewAdapter(

    private val list: List<RowModel>,
    private val listener: ListListener
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    // rowViewを生成しそれを元にRecyclerViewHolderを生成しreturnする
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val rowView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_income_list, parent, false)
        return RecyclerViewHolder(rowView)
    }

    // positionをlistのindexとしてRecyclerViewHolderに値をセットする
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.nameView.text = list[position].name
        holder.priceView.text = list[position].price
        holder.itemView.setOnClickListener {
            listener.onClickRow(it, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // インターフェイースの生成
    interface ListListener {
        fun onClickRow(tappedView: View, rowModel: RowModel)
    }
}
