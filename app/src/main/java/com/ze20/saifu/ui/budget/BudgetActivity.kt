package com.ze20.saifu.ui.budget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ze20.saifu.R
import kotlinx.android.synthetic.main.budget_config.*

class BudgetActivity : AppCompatActivity() {

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
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
