package com.ze20.saifu.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SubFragmentAdapter(fm: FragmentManager, private val fragmentList: List<Fragment>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        init {
            getItem(0)
        }

        override fun getItem(position: Int): Fragment {
            // ※ どのFragmentを表示させるかを設定
            return fragmentList[position]
        }

        override fun getCount(): Int {
            // ※ ページングする画面の数を設定
            return fragmentList.size
        }

    }
