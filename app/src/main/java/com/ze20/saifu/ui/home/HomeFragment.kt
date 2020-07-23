package com.ze20.saifu.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ze20.saifu.R


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val fragmentList = arrayListOf<Fragment>(
            sub_fragment1(),
            sub_fragment2(),
            sub_fragment3()
        )


        val pager =
            root.findViewById(R.id.pager) as ViewPager

        val tablayout=root.findViewById(R.id.tablayout) as TabLayout
        /// adapterのインスタンス生成
        val adapter: PagerAdapter = SubFragmentAdapter(childFragmentManager, fragmentList)
        /// adapterをセット
        pager.adapter = adapter
        tablayout.setupWithViewPager(pager,true)
        return root
    }
}
