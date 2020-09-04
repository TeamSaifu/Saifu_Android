package com.ze20.saifu.ui.budget

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import com.ze20.saifu.ui.budget.Recyclerview.RecyclerViewHolder
import com.ze20.saifu.ui.budget.Recyclerview.RowModel
import com.ze20.saifu.ui.budget.Recyclerview.ViewAdapter
import kotlinx.android.synthetic.main.activity_income_settings.*

/*

ここでは表示と削除の処理
追加は別画面

 */

private var arrayListlayout: ArrayList<View> = arrayListOf()
private val dbName: String = "SaifuDB"
private val tableName: String = "budget"
private val dbVersion: Int = 1
val dataList = mutableListOf<RowModel>()

// 削除用の配列
private var deleteList: ArrayList<String> = arrayListOf()

class IncomeSettings : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        val recyclerView = recycler_list
        val adapter = ViewAdapter(createDataList(), object : ViewAdapter.ListListener {
            override fun onClickRow(tappedView: View, rowModel: RowModel) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "固定収入"
    }

    private fun createDataList(): List<RowModel> {
        try {
            val SQLiteDB = SQLiteDB(this, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price
            val sql = "select * from " + tableName + ";"
            val cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arrayListlayout.add(View.inflate(this, R.layout.activity_income_list, null))
                    val data: RowModel = RowModel().also {
                        it.name = cursor.getString(1)
                        it.price = getString(R.string.currency) + cursor.getString(3)
                    }
                    deleteList.add(cursor.getString(0))
                    dataList.add(data)
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e("inCome", e.toString())
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
            }// スワイプ時の処理

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
            val SQLiteDB = SQLiteDB(this, dbName, null, dbVersion)
            val database = SQLiteDB.writableDatabase

            val whereClauses = "id = ?"
            val whereArgs = arrayOf(delete)
            database.delete(tableName, whereClauses, whereArgs)
        } catch (e: Exception) {
            Log.e("inComeDelete", e.toString())
        }
    }

    // メニューアイテムを表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater

        inflater.inflate(R.menu.budget_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.SpendButton -> startActivity(Intent(this, inCome_add::class.java))
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }
}
