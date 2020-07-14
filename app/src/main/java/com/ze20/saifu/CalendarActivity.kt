package com.ze20.saifu

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_calendar.*

class CalendarActivity : AppCompatActivity() {
    // メモ
    // https://qiita.com/Sab_swiftlin/items/0993c489e7ef1c0f969d
    // これをみよ

    var tateyoko_switch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setTitle(R.string.calendar_title_kari)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // カレンダーアタプターでカレンダーを作る

        val mCalendarAdapter: CalendarAdapter = CalendarAdapter(this)

        prevButton.setOnClickListener {
            mCalendarAdapter.prevMonth()
            setTitle(mCalendarAdapter.getTitle())
        }
        nextButton.setOnClickListener {
            mCalendarAdapter.nextMonth()
            setTitle(mCalendarAdapter.getTitle())
        }

        calendarGridView?.setAdapter(mCalendarAdapter)
        setTitle(mCalendarAdapter.getTitle())
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        // アプリ終了時に画面を縦固定に設定。
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    // メニュー作成時
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_yoko_tate, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 押すたびに縦画面固定と横画面固定が切り替わる
        if (item.getItemId() == R.id.navigation_yoko_tate) {
            if (tateyoko_switch) {
                tateyoko_switch = false
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                tateyoko_switch = true
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        }
        return true
    }
}
