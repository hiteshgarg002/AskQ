package com.hrrock.askq.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.hrrock.askq.R
import com.hrrock.askq.utils.BottomNavigationViewHelper
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class ProfileActivity : AppCompatActivity() {
    private var ctx: Context? = null
    private var activity: Activity? = null
    private var bottomNavigationViewEx: BottomNavigationViewEx? = null
    private var menuItem: MenuItem? = null
    private var menu: Menu? = null

    private companion object {
        private const val ACTIVITY_NUM = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ctx = this
        activity = this

        setNavigationStatusBarColor()
        setUpBottomNavigationView()
    }

    private fun setNavigationStatusBarColor() {
        val window = this.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(ctx!!, R.color.black)

        window.navigationBarColor = resources.getColor(R.color.white, null)
    }

    private fun setUpBottomNavigationView() {
        bottomNavigationViewEx = findViewById(R.id.bottomNavigationView)
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx)
        BottomNavigationViewHelper.enableBottomNavigationView(ctx, activity, bottomNavigationViewEx)

        menu = bottomNavigationViewEx!!.menu
        menuItem = menu!!.getItem(ACTIVITY_NUM)
        menuItem!!.isChecked = true

        //bottomNavigationViewEx!!.setIconTintList(ACTIVITY_NUM, ColorStateList.valueOf(resources.getColor(R.color.white, Resources.getSystem().newTheme())))
    }
}
