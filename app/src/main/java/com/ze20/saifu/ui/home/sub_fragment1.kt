package com.ze20.saifu.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ze20.saifu.DataInputActivity
import com.ze20.saifu.R
import kotlinx.android.synthetic.main.activity_sub_fragment1.view.*

class sub_fragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_sub_fragment1, container, false)
        view.input_button.setOnClickListener() {
            startActivity(Intent(getActivity(), DataInputActivity::class.java))
        }
        return view
    }
}
