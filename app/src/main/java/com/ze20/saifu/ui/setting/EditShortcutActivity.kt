package com.ze20.saifu.ui.setting

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import com.ze20.saifu.ui.Recyclerview.ShortcutRowModel
import com.ze20.saifu.ui.setting.RecyclerView.RecyclerViewHolder
import com.ze20.saifu.ui.setting.RecyclerView.ShortcutViewAdapter
import kotlinx.android.synthetic.main.fragment_log.*

class EditShortcutActivity : AppCompatActivity() {
    private val dbName: String = "SaifuDB"
    private val tableName: String = "shortcut"
    private val dbVersion: Int = 1
    private var arrayListlayout: ArrayList<View> = arrayListOf()

    // 削除用の配列
    private var deleteList: ArrayList<String> = arrayListOf()
    val dataList = mutableListOf<ShortcutRowModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_shortcut)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "ショートカット編集"

        val recyclerView = recycler_list
        val adapter = ShortcutViewAdapter(
            createDataList(),
            object : ShortcutViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: ShortcutRowModel) {
                }
            })

        // アダプターとレイアウトマネージャをセットする
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // メソッドをインスタンス化しそれにrecyclerViewをアタッチする
        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }

    private fun createDataList(): List<ShortcutRowModel> {
        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成
            val sql =
                "select name,price,category,id from $tableName order by id asc;"
            val cursor = database.rawQuery(sql, null)

            Log.i("DB", cursor.count.toString())
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    Log.i(
                        "DB",
                        cursor.getString(0) + " : " + cursor.getInt(1) + " : " + cursor.getString(2)
                    )
                    arrayListlayout.add(
                        View.inflate(
                            this,
                            R.layout.activity_edit_shortcut_list,
                            null
                        )
                    )

                    val data: ShortcutRowModel = ShortcutRowModel()
                        .also {
                            it.name = cursor.getString(0) + " "

                            if (cursor.getString(2) == "0") {
                                it.category = ""
                            } else {
                                it.category = cursor.getString(2)
                            }
                            it.price =
                                getString(R.string.currency) + cursor.getInt(1).toString() + " "
                        }
                    deleteList.add(cursor.getString(3))
                    dataList.add(data)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e("ShortcutShow", e.toString())
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
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.writableDatabase

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(delete)
            database.delete(tableName, whereClauses, whereArgs)
        } catch (e: Exception) {
            Log.e("ShortcutDelete", e.toString())
        }
    }
}
