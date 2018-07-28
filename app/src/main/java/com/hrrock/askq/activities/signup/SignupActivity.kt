package com.hrrock.askq.activities.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.hrrock.askq.R
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_signup.*
import spencerstudios.com.bungeelib.Bungee

class SignupActivity : AppCompatActivity() {
    private var ctx: Context? = null

    private companion object {
        private const val USER_PREFERENCE = "userinfo"
        private const val SIGNUP_ACTIVITY = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        ctx = this

        setNavigationStatusBarColor()
        onNextPress()
        toLogin()
    }

    private fun onNextPress() {
        PushDownAnim.setPushDownAnimTo(relNextButtonOnSignup)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    if(validateData()) {
                        val myIntent = Intent(ctx, SignupFinalActivity::class.java)
                        myIntent.putExtra("email", emailOnSignup.text.toString())
                        myIntent.putExtra("mobile", mobileOnSignup.text.toString())
                        myIntent.putExtra("name", nameOnSignup.text.toString())
                        startActivity(myIntent)
                        Bungee.slideLeft(ctx)
                    }else{
                        Snackbar.make(rootRelSignup,"Field(s) can not be empty!",Snackbar.LENGTH_LONG).show()
                    }
                })
    }

    private fun toLogin() {
        PushDownAnim.setPushDownAnimTo(backToLogin)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    onBackPressed()
                })

        PushDownAnim.setPushDownAnimTo(loginOnSignup)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    onBackPressed()
                })
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

    private fun validateData(): Boolean {
        return !emailOnSignup.text.toString().isEmpty() &&
                !mobileOnSignup.text.toString().isEmpty() &&
                !nameOnSignup.text.toString().isEmpty()
    }
}
