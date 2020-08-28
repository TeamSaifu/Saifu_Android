package com.ze20.saifu.ui.category.Recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.ConvenientFunction
import com.ze20.saifu.R

class ViewAdapter(

    private val list: List<RowModel>,
    private val listener: ListListener
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    // rowViewを生成しそれを元にRecyclerViewHolderを生成しreturnする
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val rowView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_category_recycler, parent, false)
        return RecyclerViewHolder(rowView)
    }

    // positionをlistのindexとしてRecyclerViewHolderに値をセットする
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.nameView.text = list[position].name
        holder.itemView.setOnClickListener {
            listener.onClickRow(it, list[position])
        }
        Log.i("DBBBBBB", list[position].image.toString())
        ConvenientFunction().CategoryImage(list[position].image)?.let {
            holder.imageView.setImageResource(it)
        } ?: holder.imageView.setImageDrawable(null)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // インターフェイースの生成
    interface ListListener {
        fun onClickRow(tappedView: View, rowModel: RowModel)
    }
}
