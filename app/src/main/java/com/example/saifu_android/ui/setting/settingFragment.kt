package com.example.saifu_android.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.saifu_android.R

class settingFragment : Fragment() {

    private lateinit var SettingViewModel: settingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SettingViewModel =
            ViewModelProviders.of(this).get(settingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_setting, container, false)
        val textView: TextView = root.findViewById(R.id.text_setting)
        SettingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = "設定"
        })
        return root
    }
}