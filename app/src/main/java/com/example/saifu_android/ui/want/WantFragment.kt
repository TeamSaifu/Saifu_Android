package com.example.saifu_android.ui.want

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.saifu_android.R

class WantFragment : Fragment() {

    private lateinit var wantViewModel: WantViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        wantViewModel =
                ViewModelProviders.of(this).get(WantViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_want, container, false)
        val textView: TextView = root.findViewById(R.id.text_want)
        wantViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = "ねもうす"
        })
        return root
    }
}