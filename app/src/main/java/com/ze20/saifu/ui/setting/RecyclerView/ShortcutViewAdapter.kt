package com.ze20.saifu.ui.setting.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R
import com.ze20.saifu.UtilityFunClass
import com.ze20.saifu.ui.Recyclerview.ShortcutRowModel

// ViewHolderを生成しViewModelをセットする
// 詳しくはココ参照→ https://qiita.com/saiki-ii/items/78ed73134784f3e5db7e

class ShortcutViewAdapter(

    private val list: List<ShortcutRowModel>,
    private val listener: ListListener
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    // rowViewを生成しそれを元にRecyclerViewHolderを生成しreturnする
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        Log.d("Adapter", "onCreateViewHolder")
        val rowView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_edit_shortcut_list, parent, false)
        return RecyclerViewHolder(rowView)
    }

    // positionをlistのindexとしてRecyclerViewHolderに値をセットする
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        if (list[position].category != "-1") {
            UtilityFunClass().CategoryImage(list[position].category.toInt())?.let {
                holder.categoryView.setImageResource(it)
                holder.categoryView.visibility = View.VISIBLE
            } ?: run {
                holder.categoryView.setImageDrawable(null)
                holder.categoryView.visibility = View.GONE
            }
        }
        if (list[position].name == "" || list[position].name == " " || list[position].name.isEmpty()) {
            holder.mainView.text = list[position].price
            holder.subView.visibility = View.GONE
        } else {
            holder.mainView.text = list[position].name
            holder.subView.text = list[position].price
        }
        // holder.categoryView.text = list[position].category
        holder.itemView.setOnClickListener {
            listener.onClickRow(it, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    // インターフェイースの生成
    interface ListListener {
        fun onClickRow(tappedView: View, rowModel: ShortcutRowModel)
    }
}
