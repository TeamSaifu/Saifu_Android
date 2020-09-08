package com.ze20.saifu.ui.calendar

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.TextView
import com.ze20.saifu.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class CalendarAdapter(context: Context) : BaseAdapter() {
    private var dateArray: List<Date> = ArrayList()
    private val mContext: Context = context
    private var mDateManager: DateManager = DateManager()
    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    // カスタムセルを拡張したらここでWigetを定義
    private class ViewHolder {
        var dateText: TextView? = null
    }

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertViewm: View? = convertView
        val holder: ViewHolder
        if (convertViewm == null) {
            convertViewm = mLayoutInflater.inflate(R.layout.calendar_cell, null)
            holder = ViewHolder()
            holder.dateText = convertViewm!!.findViewById(R.id.dateText)
            convertViewm.tag = holder
        } else {

            holder = convertViewm.tag as ViewHolder
        }

        //
        val dp = mContext.resources.displayMetrics.density
        val params: AbsListView.LayoutParams = AbsListView.LayoutParams(
            parent.width / 7 - dp.toInt(),
            (parent.height - dp.toInt() * mDateManager.getWeeks()) / mDateManager.getWeeks()
        )
        convertView?.layoutParams = params

        // 日付のみ表示させる
        val dateFormat =
            SimpleDateFormat("d", Locale.US)
        holder.dateText!!.text = dateFormat.format(dateArray[position])

        // 当月以外のセルをグレーアウト
        if (mDateManager.isCurrentMonth(dateArray[position])) {
            convertViewm.setBackgroundColor(Color.WHITE)
        } else {
            convertViewm.setBackgroundColor(Color.LTGRAY)
        }

        // 日曜日を赤、土曜日を青に
        val colorId: Int = when (mDateManager.getDayOfWeek(dateArray[position])) {
            1 -> Color.RED
            7 -> Color.BLUE
            else -> Color.BLACK
        }
        holder.dateText!!.setTextColor(colorId)
        return convertViewm
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    // 表示月を取得
    open fun getTitle(): String? {
        val format = SimpleDateFormat("yyyy年MM月", Locale.US)
        return format.format(mDateManager.mCalendar.time)
    }

    // 翌月表示
    fun nextMonth() {
        mDateManager.nextMonth()
        dateArray = mDateManager.days
        notifyDataSetChanged()
    }

    // 前月表示
    fun prevMonth() {
        mDateManager.prevMonth()
        dateArray = mDateManager.days
        notifyDataSetChanged()
    }

    init {
        dateArray = mDateManager.days
    }
}
