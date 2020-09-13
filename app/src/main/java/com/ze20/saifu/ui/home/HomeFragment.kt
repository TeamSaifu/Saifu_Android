package com.ze20.saifu.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDBClass
import com.ze20.saifu.UtilityFunClass
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {
    var mode = "Standard"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onResume() {
        super.onResume()
        reload(requireView())
        saifuCalculation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationButton.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }

    private fun reload(view: View) {
        // スワイプできるフラグメント一覧
        val fragmentList = arrayListOf<Fragment>(SubFragment1())

        val SQLiteDB = SQLiteDBClass(requireContext(), "SaifuDB", null, 1)
        val database = SQLiteDB.readableDatabase
        val sql = "select * from shortcut order by 1 asc;"
        val cursor = database.rawQuery(sql, null)
        if (cursor.count >= 5) {
            tablayout.visibility = View.VISIBLE
            fragmentList.add(SubFragment2())
        }
        if (cursor.count >= 13) fragmentList.add(SubFragment3())
        cursor.close()

        // レイアウト呼び出し
        val pager = view.findViewById(R.id.pager) as ViewPager

        // レイアウト呼び出し
        val tablayout = view.findViewById(R.id.tablayout) as TabLayout

        // adapterのインスタンス生成
        val adapter: PagerAdapter = SubFragmentAdapter(childFragmentManager, fragmentList)

        // adapterをセット
        pager.adapter = adapter

        // tablayoutを表示
        tablayout.setupWithViewPager(pager, true)
        money.setOnClickListener {
            modeChange()
        }
        limit.setOnClickListener {
            modeChange()
        }
        today.setOnClickListener {
            modeChange()
        }
    }

    private fun modeChange() {
        mode = when (mode) {
            "Standard" -> "Now / Max"
            "Now / Max" -> "Monthly"
            "Monthly" -> "Standard"
            else -> "Standard"
        }
        saifuCalculation()
    }

    fun saifuCalculation() {
        var date = java.util.Date()
        var month = SimpleDateFormat("M", Locale.JAPANESE).format(date).toInt()
        var dayOfMonth = SimpleDateFormat("d", Locale.JAPANESE).format(date).toInt()
        var monthend = UtilityFunClass().monthEnd(month)
        var budgetSum: Int =
            UtilityFunClass().incomeSum(requireContext()) - UtilityFunClass().spendSum(
                requireContext()
            )
        var per = dayOfMonth * 1.0 / monthend * 1.0
        var maxv = budgetSum * per
        var now = maxv - monthSum(
            SimpleDateFormat("yyyy", Locale.JAPANESE).format(date).toInt(),
            SimpleDateFormat("M", Locale.JAPANESE).format(date).toInt(),
            SimpleDateFormat("d", Locale.JAPANESE).format(date).toInt() + 1
        )
        var nowm = budgetSum - monthSum(
            SimpleDateFormat("yyyy", Locale.JAPANESE).format(date).toInt(),
            SimpleDateFormat("M", Locale.JAPANESE).format(date).toInt(),
            32
        )
        when (mode) {
            "Standard" -> {
                money.setTextSize(56F)
                money.text =
                    getString(R.string.currency) + " " + "%,d".format(now.toInt())
                today.text = SimpleDateFormat(
                    "yyyy 年 M 月 d 日", Locale.JAPANESE
                ).format(date)
                limit.visibility = View.GONE
                progressBar.progress = (now / maxv * 1000).toInt()
            }
            "Now / Max" -> {
                money.setTextSize(36F)
                money.text =
                    getString(R.string.currency) + " " + "%,d".format(now.toInt())
                limit.visibility = View.VISIBLE
                limit.text = " / " + getString(R.string.currency) + " " + "%,d".format(
                    maxv.toInt()
                )
                today.text = SimpleDateFormat(
                    "yyyy 年 M 月 の ", Locale.JAPANESE
                ).format(date) + "%d 日目 までの予算".format(dayOfMonth, monthend)
                progressBar.progress = (now / maxv * 1000).toInt()
            }
            "Monthly" -> {
                money.setTextSize(36F)
                money.text =
                    getString(R.string.currency) + " " + "%,d".format(nowm.toInt())
                limit.visibility = View.VISIBLE
                limit.text =
                    " / " + getString(R.string.currency) + " " + "%,d".format(
                        budgetSum.toInt()
                    )
                today.text = SimpleDateFormat(
                    "yyyy 年 M 月 の予算", Locale.JAPANESE
                ).format(date)
                progressBar.progress = (nowm * 1.0 / budgetSum * 1.0 * 1000).toInt()
            }
        }
    }

    fun monthSum(year: Int, month: Int, day: Int): Int {
        val dbName: String = "SaifuDB"
        val tableName: String = "log"
        val dbVersion: Int = 1
        var minusy = 0
        var plusy = 0

        try {
            // DBにアクセス
            val SQLiteDB = SQLiteDBClass(requireContext(), dbName, null, dbVersion)
            val database = SQLiteDB.readableDatabase

            // SQL文を構成

            var sql =
                "select sum(price),count(price) from $tableName where sign = 0 AND payDate >= '$year-" + "%02d".format(
                    month
                ) + "-01' AND payDate < '" + year + "-" + "%02d".format(month) + "-" + "%02d".format(
                    day
                ) + "'"

            var cursor = database.rawQuery(sql, null)

            if (cursor.count > 0) {
                cursor.moveToFirst()
                minusy = cursor.getInt(0)
            }
            cursor.close()
            sql =
                "select sum(price),count(price) from $tableName WHERE sign = 1 AND payDate >= '$year-" + "%02d".format(
                    month
                ) + "-01' AND payDate <= '" + year + "-" + "%02d".format(month) + "-" + "%02d".format(
                    day
                ) + "';"

            var cursor2 = database.rawQuery(sql, null)

            if (cursor2.count > 0) {
                cursor2.moveToFirst()
                plusy = cursor2.getInt(0)
            }
            return minusy - plusy
        } catch (e: Exception) {
            Log.e("DBSelectError", e.toString())
        }
        return 0
    }
}

