package com.ze20.saifu

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data_input.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DataInputActivity : AppCompatActivity() {
    var date = java.util.Date() // 今日の日付を格納
    var sign = false // プラス・マイナス
    var UserSetDate: java.util.Date = java.util.Date() // 設定された日付・初期値は今日
    var BusyFlag = false // ローディング中はいろいろ動かなくするためのフラグ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_input)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // UserSetDateを表示しておく
        day_text.setText(SimpleDateFormat("yyyy/MM/dd").format(UserSetDate))

        plus_minus.setOnClickListener { view ->
            // プラスマイナス切り替え
            plusminus()
        }
        money_text.setOnClickListener() {
            // 選択位置を末尾にする
            money_text.setSelection(money_text.length())
        }
        money_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 自動で入力幅を広げる
                emsauto()
            }
        })
        day_text.setOnClickListener() {
            // カレンダーを表示する
            showDatePicker()
        }
        m7d.setOnClickListener() {
            setdayquick(-7)
        }
        m1d.setOnClickListener() {
            setdayquick(-1)
        }
        today.setOnClickListener() {
            setdayquick(0)
        }
        p1d.setOnClickListener() {
            setdayquick(1)
        }
        p7d.setOnClickListener() {
            setdayquick(7)
        }
    }

    // メニュー作成時
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_apply, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    fun plusminus() {
        // プラスマイナスを切り替えて表示も切り替える
        sign = !sign
        plus_minus.setText(if (sign) R.string.plus else R.string.minus)
    }

    fun emsauto() {
        // 文字列の幅を適切にする
        money_text.setEms(Math.max((money_text.length() + 1) / 2, 1))
    }

    fun showDatePicker() {
        // カレンダーを表示する
        if (BusyFlag == false) {
            BusyFlag = true

            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
                    // 表示テキストを作成
                    day_text.text = "${year}/${month}/${dayOfMonth}"
                    // UserSetDate に選択された日付を格納
                    UserSetDate =
                        SimpleDateFormat("yyyy/M/d").parse("${year}/${month}/${dayOfMonth}")
                    val sdFormat =
                        SimpleDateFormat("yyyy/MM/dd")
                    // 日付の差を計算する
                    val datediff = dateDiff(sdFormat.format(UserSetDate), sdFormat.format(date))
                    // 差に応じて表示を変更する
                    if (datediff == 0) {
                        day_text2.text = R.string.Today_Parentheses.toString()
                    } else if (datediff > 0) {
                        day_text2.text = getString(R.string.prev_day, datediff)
                    } else {
                        day_text2.text = getString(R.string.next_day, (datediff * -1))
                    }
                }, // Dateピッカーの初期値にUserSetDateを設定
                SimpleDateFormat("yyyy").format(UserSetDate).toInt(),
                SimpleDateFormat("MM").format(UserSetDate).toInt(),
                SimpleDateFormat("dd").format(UserSetDate).toInt()
            )
            // 表示します
            datePickerDialog.show()

            BusyFlag = false
        }
    }

    fun dateDiff(dateFromStrig: String?, dateToString: String?): Int {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        var dateTo: Date? = null
        var dateFrom: Date? = null
        // Date型に変換
        try {
            dateFrom = sdf.parse(dateFromStrig)
            dateTo = sdf.parse(dateToString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        // 差分の日数を計算する
        val dateTimeTo = dateTo!!.time
        val dateTimeFrom = dateFrom!!.time
        val dayDiff = (dateTimeTo - dateTimeFrom) / (1000 * 60 * 60 * 24)
        return dayDiff.toInt()
    }

    fun setdayquick(num: Int) {
        if (num != 0) {
            var calendar: Calendar = Calendar.getInstance()
            calendar.setTime(UserSetDate)
            calendar.add(Calendar.DAY_OF_MONTH, num)
            UserSetDate = calendar.getTime()
        } else {
            UserSetDate = date
        }
        // UserSetDateを表示しておく
        day_text.setText(SimpleDateFormat("yyyy/MM/dd").format(UserSetDate))
        val sdFormat =
            SimpleDateFormat("yyyy/MM/dd")
        // 日付の差を計算する
        val datediff = dateDiff(sdFormat.format(UserSetDate), sdFormat.format(date))
        // 差に応じて表示を変更する
        if (datediff == 0) {
            day_text2.text = getString(R.string.Today_Parentheses)
        } else if (datediff > 0) {
            day_text2.text = getString(R.string.prev_day, datediff)
        } else {
            day_text2.text = getString(R.string.next_day, (datediff * -1))
        }
    }
}
