package com.hrrock.askq.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hrrock.askq.R

class AnswerForYouFragment:Fragment() {
    private var ctx: Context?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_answer_for_you,container,false)
        ctx=activity

        return v
    }
}