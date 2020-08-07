package com.ze20.saifu.ui.log

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import com.ze20.saifu.ui.log.Recyclerview.RowModel
import com.ze20.saifu.ui.log.Recyclerview.ViewAdapter
import kotlinx.android.synthetic.main.fragment_log.*

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

        //logShow()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list
        val adapter = ViewAdapter(
            createDataList(),
            object :
                ViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: RowModel) {
                    this@logFragment.onClickRow(tappedView, rowModel)
                }
            })

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun createDataList(): List<RowModel> {
        val dataList = mutableListOf<RowModel>()
        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDB(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成
            val sql =
                "select *,strftime('%Y/%m/%d', payDate) from " + tableName + " order by 1 desc;"
            val cursor = database.rawQuery(sql, null)

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arrayListlayout.add(View.inflate(context, R.layout.fragment_log_list, null))
                    val data: RowModel = RowModel()
                        .also {
                            it.day = cursor.getString(8) + " "

                            if (cursor.getString(4) == "0") {
                                it.category = ""
                            } else {
                                it.category = cursor.getString(4)
                            }

                            it.price =
                                getString(R.string.currency) + cursor.getString(3).toString() + " "
                        }
                    dataList.add(data)
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e("logShow", e.toString())
        }
        return dataList
    }

    // for (i in 0..49) {
    //     val data: RowModel = RowModel()
    //         .also {
    //             it.day = "タイトル" + i + "だよ"
    //             it.category = "詳細" + i + "個目だよ"
    //             it.price = "aa" + i + "ss"
    //         }
    //     dataList.add(data)
    // }
    // return dataList

    fun onClickRow(tappedView: View, rowModel: RowModel) {
        Snackbar.make(
            tappedView,
            "Replace with your own action tapped ${rowModel.price}",
            Snackbar.LENGTH_LONG
        )
            .setAction("Action", null).show()
    }

    // private fun logShow() {
    //     try {
    //         // DBにアクセス
    //         val SQLiteDB = SQLiteDB(requireContext(), dbName, null, dbVersion)
    //         val database = SQLiteDB.readableDatabase
    //
    //         // SQL文を構成
    //         val sql =
    //             "select *,strftime('%Y/%m/%d', payDate) from " + tableName + " order by 1 desc;"
    //         val cursor = database.rawQuery(sql, null)
    //
    //         // log表
    //         // inputDate primary key,payDate,name,price,category,splitCount,picture
    //         if (cursor.count > 0) {
    //             cursor.moveToFirst()
    //             while (!cursor.isAfterLast) {
    //                 arrayListlayout.add(View.inflate(context, R.layout.fragment_log_list, null))
    //                 root.recycler_list.addView(
    //                     arrayListlayout[arrayListlayout.size - 1].apply {
    //
    //                         dayText.text = cursor.getString(7) + "   "
    //
    //                         // カテゴリが設定されていなければ非表示
    //                         if (cursor.getString(4) == "0") {
    //                             categoryNameText.text = ""
    //                         } else {
    //                             categoryNameText.text = cursor.getString(4)
    //                         }
    //
    //                         priceText.text =
    //                             getString(R.string.currency) + cursor.getInt(3).toString() + " "
    //                     }
    //                 )
    //                 cursor.moveToNext()
    //             }
    //         }
    //     } catch (e: Exception) {
    //         println(e)
    //     }
    // }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle((R.menu.search_view))
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)
    }
}
