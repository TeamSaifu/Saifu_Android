package com.ze20.saifu.ui.budget

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.ConvenientFunction
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import kotlinx.android.synthetic.main.activity_in_come_add.*

class inCome_add : AppCompatActivity() {
    private val dbName: String = "SaifuDB"
    private val tableName: String = "budget"
    private val dbVersion: Int = 1
    private val cFunc = ConvenientFunction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_come_add)

        setListeners()

        incomeAdd.setOnClickListener {
            if (incomeName.text.toString()
                    .equals("") == false && incomePrice.text.toString()
                    .equals("") == false) {
                insertData(incomeName.text.toString(), incomePrice.text.toString())
            } else {
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("ERROR")
                    .setMessage("入力して下さい")
                    .setPositiveButton("OK", { dialog, which -> })
                    .show()
            }
        }

        incomeCancel.setOnClickListener {
            onSupportNavigateUp()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // 戻るボタンを押したときの処理
        finish()
        return super.onSupportNavigateUp()
    }

    private fun insertData(name: String, price: String) {
        try {
            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price

            // DBにアクセス
            val SQLiteDB = SQLiteDB(this, dbName, null, dbVersion)
            val database = SQLiteDB.writableDatabase

            val values = ContentValues()
            values.put("name", name)
            values.put("price", price)
            values.put("isincome", 1)

            database.insertOrThrow(tableName, null, values)
        } catch (e: Exception) {
            Log.e("insertIncome", e.toString())
        }
    }

    private fun setListeners() {
        mainLayout.setOnClickListener {
            cFunc.hideKeyboard(this, this@inCome_add.currentFocus)
        }
    }
}
