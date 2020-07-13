package com.ze20.saifu.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ze20.saifu.DataInputActivity
import com.ze20.saifu.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val buttonshow =
            root.findViewById(R.id.show_button) as Button
        buttonshow.setOnClickListener {
            //表示テスト用ボタン
            //DataInputActivityの部分を表示したい名前に変更してね
            startActivity(Intent(getActivity(), DataInputActivity::class.java))
        }
        //メイン画面作るときにこの行は消してもらって構いません。
        return root
    }
}
