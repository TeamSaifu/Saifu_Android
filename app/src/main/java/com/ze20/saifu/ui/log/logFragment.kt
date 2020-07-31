package com.ze20.saifu.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import kotlinx.android.synthetic.main.fragment_log.view.*

lateinit var root: View

class logFragment : Fragment() {

    private val dbName: String = "SaifuDB"
    private val tableName: String = "log"
    private val dbVersion: Int = 1
    private var arrayListlayout: ArrayList<View> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = View.inflate(context, R.layout.fragment_log, null)

        logShow()

        return root
    }

    private fun logShow() {
        try {

            // DBにアクセス
            val SQLiteDB = SQLiteDB(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成
            val sql =
                "select *,strftime('%Y/%m/%d', payDate) as day from " + tableName + " order by 1 desc;"
            val cursor = database.rawQuery(sql, null)

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture,sign

            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arrayListlayout.add(View.inflate(context, R.layout.fragment_log, null))
                    root.linearlayout.addView(arrayListlayout[arrayListlayout.size - 1].apply {
                        dayText.text = cursor.getString(7) + "   "
                        if (cursor.getString(4) == "0") {

                            categoryNameText.text = ""
                        }
                        priceText.text = "¥" + cursor.getInt(3).toString()
                    })
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    override

    fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle((R.menu.search_view))
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)
    }
}
