package com.example.saifu_android.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.saifu_android.R

class logFragment : Fragment() {

    private lateinit var LogViewModel: logViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        LogViewModel =
            ViewModelProviders.of(this).get(logViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_log, container, false)
        val textView: TextView = root.findViewById(R.id.text_log)
        LogViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = "ろぐ"
        })
        return root
    }
}