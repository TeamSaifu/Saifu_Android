package com.ze20.saifu.ui.category

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import com.ze20.saifu.ui.category.Recyclerview.RowModel
import com.ze20.saifu.ui.category.Recyclerview.ViewAdapter
import kotlinx.android.synthetic.main.activity_category_list2.*

class categoryList : AppCompatActivity() {
    private val dbName: String = "SaifuDB"
    private val tableName: String = "category"
    private val dbVersion: Int = 1

    private var arrayListlayout: ArrayList<View> = arrayListOf()
    private var mode = "Normal"
    val dataList = mutableListOf<RowModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "カテゴリ一覧"
        mode = intent.getStringExtra("mode") ?: "Normal"
        val recyclerView = caRecycle
        val adapter = ViewAdapter(
            createDataList(),
            object :
                ViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: RowModel) {
                    if (mode == "Select") {
                        val intent = Intent()
                        intent.putExtra("id", rowModel.id)
                        intent.putExtra("name", rowModel.name)
                        intent.putExtra("image", rowModel.image)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            })

// アダプターとレイアウトマネージャをセットする
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }

    private fun createDataList(): List<RowModel> {
        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase
            // SQL文を構成
            val sql =
                "select * from " + tableName + ";"
            val cursor = database.rawQuery(sql, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    arrayListlayout.add(
                        View.inflate(
                            this,
                            R.layout.activity_category_recycler,
                            null
                        )
                    )

                    val data: RowModel = RowModel()
                        .also {
                            it.id = cursor.getInt(0)
                            it.name = cursor.getString(1)
                            it.image = cursor.getInt(2)
                        }
                    dataList.add(data)
                    cursor.moveToNext()
                }
            }
            Log.d("aaa", dataList.toString())
        } catch (e: Exception) {
            Log.e("categoryShow", e.toString())
        }
        return dataList
    }
}
