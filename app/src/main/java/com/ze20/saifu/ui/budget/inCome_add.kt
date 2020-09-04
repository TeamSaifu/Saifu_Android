package com.ze20.saifu.ui.budget

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import kotlinx.android.synthetic.main.activity_in_come_add.*

/*
    固定収入を追加する画面
 */

class inCome_add : AppCompatActivity() {
    private val dbName: String = "SaifuDB"
    private val tableName: String = "budget"
    private val dbVersion: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_come_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "新規固定収入"

        incomeAdd.setOnClickListener {
            // 全項目が入力されている場合
            if (incomeName.text.toString().equals("") == false && incomePrice.text.toString()
                    .equals("") == false) {
                insertData(incomeName.text.toString(), incomePrice.text.toString())
                Toast.makeText(this, "登録しました。", Toast.LENGTH_LONG).show()
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("入力して下さい。")
                    .setPositiveButton("OK", { _, _ -> })
                    .show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }

    // Insert処理
    private fun insertData(name: String, price: String) {
        try {
            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price

            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.writableDatabase

            val values = ContentValues()
            values.put("name", name)
            values.put("price", price)
            values.put("type", "isincome")

            database.insertOrThrow(tableName, null, values)
        } catch (e: Exception) {
            Log.e("insertIncome", e.toString())
        }
    }
}
