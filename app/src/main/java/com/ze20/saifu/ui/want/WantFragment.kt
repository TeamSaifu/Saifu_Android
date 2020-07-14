package com.ze20.saifu.ui.want

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ze20.saifu.R

class WantFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_want, container, false)
        val textView: TextView = root.findViewById(R.id.text_want)
            textView.text = "ねもうす"
        return root
    }
}