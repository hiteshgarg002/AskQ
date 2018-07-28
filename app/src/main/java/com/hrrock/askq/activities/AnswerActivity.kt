package com.hrrock.askq.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.hrrock.askq.R
import com.hrrock.askq.fragments.AnswerForYouFragment
import com.hrrock.askq.fragments.AnswerRequestsFragment
import com.hrrock.askq.fragments.AnswerSavedFragment
import com.hrrock.askq.utils.BottomNavigationViewHelper
import com.hrrock.askq.utils.ScrollableViewPager
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.snippet_top_answer.*
import spencerstudios.com.bungeelib.Bungee

class AnswerActivity : AppCompatActivity() {
    private var ctx: Context? = null
    private var activity: Activity? = null
    private var bottomNavigationViewEx: BottomNavigationViewEx? = null
    private var menuItem: MenuItem? = null
    private var menu: Menu? = null

    private companion object {
        private const val ACTIVITY_NUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        ctx = this
        activity = this

        setNavigationStatusBarColor()
        setUpBottomNavigationView()
        setUpTabs()
        toAsk()
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

        //  bottomNavigationViewEx!!.setIconTintList(ACTIVITY_NUM, ColorStateList.valueOf(resources.getColor(R.color.white, Resources.getSystem().newTheme())))
    }

    private fun setUpTabs() {
        val viewPager = findViewById<View>(R.id.answerViewPager) as ScrollableViewPager
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                var fragment: Fragment? = null

                when (position) {
                    0 -> fragment = AnswerForYouFragment()
                    1 -> fragment = AnswerRequestsFragment()
                    2 -> fragment = AnswerSavedFragment()
                }
                return fragment
            }

            override fun getCount(): Int {
                return 3
            }
        }

        val navigationTabStrip = findViewById<View>(R.id.answerTabs) as NavigationTabStrip
        navigationTabStrip.setViewPager(viewPager, 0)
        navigationTabStrip.setTabIndex(0, true)
        viewPager.setCanScroll(false)
    }

    private fun toAsk() {
        PushDownAnim.setPushDownAnimTo(askIconOnAnswer)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    startActivity(Intent(ctx, AskActivity::class.java))
                    Bungee.slideLeft(ctx)
                }
    }
}
