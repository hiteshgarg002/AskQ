package com.hrrock.askq.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.hrrock.askq.R
import com.hrrock.askq.interfaces.RetrofitApiInterface
import com.hrrock.askq.models.UserDetailsModel
import com.hrrock.askq.networks.RetrofitApiClient
import kotlinx.android.synthetic.main.activity_splash_screen.*
import retrofit2.Call
import retrofit2.Callback
import spencerstudios.com.bungeelib.Bungee

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var handler: Handler
    private lateinit var ctx: Context
    private lateinit var preferences: SharedPreferences
    private var editor: SharedPreferences.Editor? = null
    private lateinit var URL: String
    private lateinit var activityRef: Activity
    private lateinit var progressBar: ProgressBar
    private var details: List<UserDetailsModel>? = null
    private var apiInterface: RetrofitApiInterface? = null
    private var jsonCall: Call<List<UserDetailsModel>>? = null

    private companion object {
        private const val USER_PREFERENCE = "userinfo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        ctx = this
        activityRef = this

        preferences = ctx.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE)
        editor = preferences.edit()

        details = ArrayList()
        apiInterface = RetrofitApiClient.getRetrofitForCredentialController().create(RetrofitApiInterface::class.java)

        handler = Handler()
        setNavigationStatusBarColor()
        exitSplashScreen()
    }

    private fun setNavigationStatusBarColor() {
        val window = this.window

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // finally change the color
        window.statusBarColor = ContextCompat.getColor(ctx, R.color.white)

        window.navigationBarColor = resources.getColor(R.color.white, null)
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    private fun exitSplashScreen() {
        handler.postDelayed({
            if (isConnected()) {
                checkIfAlreadyLogin()
            } else {
                Toast.makeText(ctx, "Check your network settings!", Toast.LENGTH_LONG).show()
                finish()
            }
        }, 2000)
    }

    private fun checkIfAlreadyLogin() = if (preferences.getBoolean("login", false)) {
        splashScreenProgress.visibility = View.VISIBLE
        doLogin()
    } else {
        startActivity(Intent(ctx, LoginActivity::class.java))
        Bungee.slideRight(ctx)
        finish()
    }

    private fun doLogin() {
        jsonCall = apiInterface!!.doLogin("" + preferences.getString("email", ""), "" + preferences.getString("password", ""))

        jsonCall!!.enqueue(object : Callback<List<UserDetailsModel>> {
            override fun onResponse(call: Call<List<UserDetailsModel>>, response: retrofit2.Response<List<UserDetailsModel>>) {
                details = response.body()

                if (details!!.isNotEmpty()) {
                    val uDetails = details!![0]

                    editor!!.putString("email", uDetails.email)
                    editor!!.putString("password", uDetails.password)
                    editor!!.putString("name", uDetails.name)
                    editor!!.putString("mobile", uDetails.mobile)
                    editor!!.putString("username", uDetails.username)
                    editor!!.putString("profile", uDetails.profile)
                    editor!!.putBoolean("login", true)
                    editor!!.apply()

                    splashScreenProgress.visibility = View.GONE

                    startActivity(Intent(ctx, HomeActivity::class.java))
                    Bungee.shrink(ctx)
                    finish()
                } else {
                    startActivity(Intent(ctx, LoginActivity::class.java))
                    Bungee.slideRight(ctx)
                    finish()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsModel>>, t: Throwable) {
                startActivity(Intent(ctx, LoginActivity::class.java))
                Bungee.slideRight(ctx)
                finish()
            }
        })
    }
}
