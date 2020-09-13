package com.ze20.saifu.ui.budget

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R
import com.ze20.saifu.UtilityFunClass
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
        val utilityFunClass = UtilityFunClass()
        incomeSum = utilityFunClass.incomeSum(this)
        spendSum = utilityFunClass.spendSum(this)
        budgetSum = incomeSum - spendSum

        budgetSumText.text = "%,d".format(budgetSum) + "円"
        incomeSumText.text = "%,d".format(incomeSum) + "円"
        spendSumText.text = "%,d".format(spendSum) + "円"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
