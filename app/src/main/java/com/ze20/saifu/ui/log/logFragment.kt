package com.ze20.saifu.ui.log

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import com.ze20.saifu.ui.log.Recyclerview.RecyclerViewHolder
import com.ze20.saifu.ui.log.Recyclerview.RowModel
import com.ze20.saifu.ui.log.Recyclerview.ViewAdapter
import kotlinx.android.synthetic.main.fragment_log.*

lateinit var root: View

class logFragment : Fragment() {

    private val dbName: String = "SaifuDB"
    private val tableName: String = "log"
    private val dbVersion: Int = 1
    private var arrayListlayout: ArrayList<View> = arrayListOf()

    val dataList = mutableListOf<RowModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = View.inflate(context, R.layout.fragment_log, null)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle((R.menu.search_view))
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list
        val adapter = ViewAdapter(
            createDataList(),
            object :
                ViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: RowModel) {
                }
            })

        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun createDataList(): List<RowModel> {
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

    private fun getSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<RecyclerViewHolder>) =
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT,
                ItemTouchHelper.LEFT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                dataList.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentActivity: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentActivity
                )

                val itemView = viewHolder.itemView
                val background = ColorDrawable()
                background.color = Color.parseColor("#f44336")

                if (dX < 0)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                background.draw(c)
            }
        })
}
