package com.hrrock.askq.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.Toast
import com.hrrock.askq.R

class ReplyActivity : AppCompatActivity() {
    private var ctx:Context?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)
        ctx=this

        Toast.makeText(ctx,intent.extras.getString("askid"),Toast.LENGTH_SHORT).show()

        setNavigationStatusBarColor()
    }

    private fun setNavigationStatusBarColor() {
        val window = this.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(ctx!!, R.color.white)

        window.navigationBarColor = resources.getColor(R.color.white, null)
    }
}
