package com.ze20.saifu.ui.log

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import com.ze20.saifu.ui.log.RecyclerView.LogRowModel
import com.ze20.saifu.ui.log.RecyclerView.RecyclerViewHolder
import com.ze20.saifu.ui.log.RecyclerView.ViewAdapter
import kotlinx.android.synthetic.main.fragment_log.*

lateinit var root: View

class logFragment : Fragment() {

    private val dbName: String = "SaifuDB"
    private val tableName: String = "log"
    private val dbVersion: Int = 1
    private var arrayListlayout: ArrayList<View> = arrayListOf()

    // 削除用の配列
    private var deleteList: ArrayList<String> = arrayListOf()
    val dataList = mutableListOf<LogRowModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = View.inflate(context, R.layout.fragment_log, null)

        return root
    }

    // メニューアイテムを表示
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_log, menu)
    }

    // 検索アイコンを表示
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.setTitle(R.menu.menu_log)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = recycler_list
        val adapter = ViewAdapter(
            createDataList(),
            object :
                ViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: LogRowModel) {
                }
            })

        // アダプターとレイアウトマネージャをセットする
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        // メソッドをインスタンス化しそれにrecyclerViewをアタッチする
        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun createDataList(): List<LogRowModel> {
        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成
            val sql =
                "select *,strftime('%m/%d', payDate) from " + tableName + " order by 1 desc;"
            val cursor = database.rawQuery(sql, null)

            // log表
            // inputDate primary key,payDate,name,price,category,splitCount,picture,sign
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arrayListlayout.add(View.inflate(context, R.layout.fragment_log_list, null))

                    val data: LogRowModel = LogRowModel()
                        .also {
                            it.day = cursor.getString(8) + " "

                            it.image = cursor.getInt(4)

                            it.name = cursor.getString(2)

                            if (cursor.getInt(7) == 1) {
                                it.price =
                                    getString(R.string.plus) + getString(R.string.currency) + cursor.getString(
                                        3
                                    )
                                        .toString() + " "
                            } else {
                                it.price =
                                    getString(R.string.minus) + getString(R.string.currency) + cursor.getString(
                                        3
                                    )
                                        .toString() + " "
                            }
                        }
                    deleteList.add(cursor.getString(0))
                    dataList.add(data)
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e("logShow", e.toString())
        }
        return dataList
    }

    // 項目を左にスワイプすると一覧とDBからデータを削除する
    // 詳しくはココ→ https://qiita.com/naoi/items/f8004f906db16d6b38da

    private fun getSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<RecyclerViewHolder>) =
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                // ドラッグアンドドロップとスワイプの方向指定
                ItemTouchHelper.LEFT,
                ItemTouchHelper.LEFT
            ) {

            // ドラッグアンドドロップした際に呼び出されるメソッドの実装
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // スワイプ時の処理
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                // データベースから削除
                val delete = viewHolder.adapterPosition
                dbDelete(deleteList[delete])
                deleteList.removeAt(viewHolder.adapterPosition)

                // データリストからスワイプしたデータを削除
                dataList.removeAt(viewHolder.adapterPosition)

                // リストからスワイプしたカードを削除
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            // スワイプ時の背景設定
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

    fun dbDelete(delete: String) {

        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.writableDatabase

            val whereClauses = "inputDate = ?"
            val whereArgs = arrayOf(delete)
            database.delete(tableName, whereClauses, whereArgs)
        } catch (e: Exception) {
            Log.e("logDelete", e.toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 画面遷移

        when (item.itemId) {
            R.id.graph -> {
                startActivity(Intent(activity, GraphActivity::class.java))
            }
            R.id.report -> {
                startActivity(Intent(activity, ReportActivity::class.java))
            }
        }
        return true
    }
}
