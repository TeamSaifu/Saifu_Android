package com.ze20.saifu.ui.budget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import kotlinx.android.synthetic.main.budget_config.*

class BudgetActivity : AppCompatActivity() {

    private var budgetSum: Int = 0
    private var incomeSum: Int = 0
    private var spendSum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_config)
        setTitle("予算設定画面")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        button_send.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }
        button_send2.setOnClickListener {
            startActivity(Intent(this, SpendActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val budgetSumText: TextView = findViewById(R.id.budgetSum)
        val incomeSumText: TextView = findViewById(R.id.incomeSumText)
        val spendSumText: TextView = findViewById(R.id.spendSumText)

        incomeSum = incomeSum()
        spendSum = spendSum()
        budgetSum = incomeSum() - spendSum()

        budgetSumText.text = budgetSum.toString() + "円"
        incomeSumText.text = incomeSum.toString() + "円"
        spendSumText.text = spendSum.toString() + "円"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun incomeSum(): Int {
        val dbName: String = "SaifuDB"
        val tableName: String = "budget"
        val dbVersion: Int = 1

        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成

            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price
            val sql =
                "select sum(price) from " + tableName + " where type = 'income';"
            val cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                incomeSum = cursor.getInt(0)
            }
        } catch (e: Exception) {
            Log.e("logShow", e.toString())
        }
        return incomeSum
    }

    private fun spendSum(): Int {
        val dbName: String = "SaifuDB"
        val tableName: String = "budget"
        val dbVersion: Int = 1

        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(this, dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成

            // budget表
            // id INTEGER primary key AUTOINCREMENT,name,type,price
            val sql =
                "select sum(price) from " + tableName + " where type = 'spend';"
            val cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                spendSum = cursor.getInt(0)
            }
        } catch (e: Exception) {
            Log.e("logShow", e.toString())
        }
        return spendSum
    }
}
