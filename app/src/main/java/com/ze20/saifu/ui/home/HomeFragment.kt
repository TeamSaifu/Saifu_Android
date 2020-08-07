package com.ze20.saifu.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ze20.saifu.R
import com.ze20.saifu.SQLiteDB
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // スワイプできるフラグメント一覧
        val fragmentList = arrayListOf<Fragment>(
            sub_fragment1()
        )

        val SQLiteDB = SQLiteDB(requireContext(), "SaifuDB", null, 1)
        val database = SQLiteDB.readableDatabase
        val sql =
            "select * from shortcut order by 1 asc;"
        val cursor = database.rawQuery(sql, null)
        if (cursor.count >= 5) {
            tablayout.visibility = View.VISIBLE
            fragmentList.add(sub_fragment2())
        }
        if (cursor.count >= 13) fragmentList.add(sub_fragment3())
        cursor.close()

        // レイアウト呼び出し
        val pager =
            root.findViewById(R.id.pager) as ViewPager

        // レイアウト呼び出し
        val tablayout = root.findViewById(R.id.tablayout) as TabLayout

        // adapterのインスタンス生成
        val adapter: PagerAdapter = SubFragmentAdapter(childFragmentManager, fragmentList)

        // adapterをセット
        pager.adapter = adapter

        // tablayoutを表示
        tablayout.setupWithViewPager(pager, true)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationButton.setOnClickListener {
            startActivity(Intent(activity, NotificationActivity::class.java))
        }
    }
}
