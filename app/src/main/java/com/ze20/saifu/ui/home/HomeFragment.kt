package com.ze20.saifu.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.ze20.saifu.R
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val fragmentList = arrayListOf<Fragment>(
            sub_fragment1(),
            sub_fragment2()
        )

        /// adapterのインスタンス生成
        val adapter:PagerAdapter = SubFragmentAdapter(childFragmentManager, fragmentList)
        /// adapterをセット
        aiueo700.adapter = adapter
        return root
    }
}
