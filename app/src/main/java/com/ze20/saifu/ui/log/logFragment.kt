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

class logFragment : Fragment() {

    private val dbName: String = "SaifuDB"
    private val tableName: String = "log"
    private val dbVersion: Int = 1
    private val arraypayDate: ArrayList<String> = arrayListOf()
    private val arrayName: ArrayList<String> = arrayListOf()
    private val arrayPrice: ArrayList<Int> = arrayListOf()
    private val arrayCategory: ArrayList<String> = arrayListOf()
    private val arraySplitCount: ArrayList<Int> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_log, container, false)
        try {

            // DBにアクセス
            val SQLiteDB = SQLiteDB(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成
            val sql = "select * from " + tableName

            val cursor = database.rawQuery(sql, null)
            println(cursor.count)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arraypayDate.add(cursor.getString(0))
                    arrayName.add(cursor.getString(1))
                    arrayPrice.add(cursor.getInt(3))
                    cursor.moveToNext()
                }

                root.text1.setText(arraypayDate.toString())
                root.text2.setText(arrayPrice.toString())
            }
            cursor.close()
        } catch (e: Exception) {
            println(e)
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle((R.menu.search_view))
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)
    }
}
