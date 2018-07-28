package com.hrrock.askq.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.hrrock.askq.R
import com.hrrock.askq.fragments.HomeBookmarkFragment
import com.hrrock.askq.fragments.HomeFeedFragment
import com.hrrock.askq.fragments.HomeNewFragment
import com.hrrock.askq.utils.BottomNavigationViewHelper
import com.hrrock.askq.utils.ScrollableViewPager
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.snippet_top_home.*
import spencerstudios.com.bungeelib.Bungee

class HomeActivity : AppCompatActivity() {
    private var ctx: Context? = null
    private var activity: Activity? = null
    private var bottomNavigationViewEx: BottomNavigationViewEx? = null
    private var menuItem: MenuItem? = null
    private var menu: Menu? = null

    private companion object {
        private const val ACTIVITY_NUM = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
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

        // bottomNavigationViewEx!!.setIconTintList(ACTIVITY_NUM, ColorStateList.valueOf(resources.getColor(R.color.white, Resources.getSystem().newTheme())))
    }

    private fun setUpTabs() {
        val viewPager = findViewById<View>(R.id.homeViewPager) as ScrollableViewPager
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                var fragment: Fragment? = null

                when (position) {
                    0 -> fragment = HomeFeedFragment()
                    1 -> fragment = HomeBookmarkFragment()
                    2 -> fragment = HomeNewFragment()
                }

                return fragment
            }

            override fun getCount(): Int {
                return 3
            }
        }

        val navigationTabStrip = findViewById<View>(R.id.homeTabs) as NavigationTabStrip
        navigationTabStrip.setViewPager(viewPager, 0)
        navigationTabStrip.setTabIndex(0, true)
        viewPager.setCanScroll(false)
    }

    private fun toAsk() {
        PushDownAnim.setPushDownAnimTo(askIconOnHome)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    startActivity(Intent(ctx, AskActivity::class.java))
                    Bungee.slideLeft(ctx)
                }
    }
}
