package com.hrrock.askq.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.hrrock.askq.R
import com.hrrock.askq.activities.signup.SignupActivity
import com.hrrock.askq.interfaces.RetrofitApiInterface
import com.hrrock.askq.models.UserDetailsModel
import com.hrrock.askq.networks.RetrofitApiClient
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import spencerstudios.com.bungeelib.Bungee

class LoginActivity : AppCompatActivity() {
    private var ctx: Context? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var details: List<UserDetailsModel>? = null
    private var apiInterface: RetrofitApiInterface? = null
    private var jsonCall: Call<List<UserDetailsModel>>? = null

    private companion object {
        private const val REQ_CODE = 1
        private const val ACTIVITY = "LoginActivity"
        private const val USER_PREFERENCE = "userinfo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ctx = this

        details = ArrayList()
        apiInterface = RetrofitApiClient.getRetrofitForCredentialController().create(RetrofitApiInterface::class.java)

        preferences = ctx!!.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE)
        editor = preferences!!.edit()

        setNavigationStatusBarColor()
        onSignupPress()
        requestPermission()
        onLoginPress()
    }

    private fun onSignupPress() {
        PushDownAnim.setPushDownAnimTo(signupOnLogin)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    startActivity(Intent(ctx, SignupActivity::class.java))
                    Bungee.slideLeft(ctx)
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

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this@LoginActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE), REQ_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQ_CODE ->
                if (grantResults.isNotEmpty()) {
                    val CAMERA = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val INTERNET = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    val READ_EXTERNAL = grantResults[2] == PackageManager.PERMISSION_GRANTED
                    val WRITE_EXTERNAL = grantResults[3] == PackageManager.PERMISSION_GRANTED
                    val CALL_PHONE = grantResults[4] == PackageManager.PERMISSION_GRANTED

                    if (CAMERA && INTERNET && READ_EXTERNAL && WRITE_EXTERNAL && CALL_PHONE) {

                        Snackbar.make(findViewById(R.id.rootRelLogin), "Permissions Granted!", Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(findViewById(R.id.rootRelLogin), "Permissions Denied!", Snackbar.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun onLoginPress() {
        PushDownAnim.setPushDownAnimTo(relLoginOnLogin)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    if (validateData()) {
                        relLoginOnLogin.visibility= View.GONE
                        relLoadingOnLogin.visibility=View.VISIBLE
                        login()
                    } else {
                        Snackbar.make(rootRelLogin, "Field(s) can not be empty!", Snackbar.LENGTH_LONG).show()
                    }
                }
    }

    private fun validateData(): Boolean {
        return !usernameOnLogin.text.toString().isEmpty() &&
                !passwordOnLogin.text.toString().isEmpty()
    }

    private fun login() {
        jsonCall = apiInterface!!.doLogin("" + usernameOnLogin!!.text.toString(), "" + passwordOnLogin!!.text.toString())

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

                    startActivity(Intent(ctx, HomeActivity::class.java))
                    Bungee.inAndOut(ctx)
                    finish()

                } else {
                    relLoadingOnLogin.visibility=View.GONE
                    relLoginOnLogin.visibility= View.VISIBLE

                    Snackbar.make(rootRelLogin, "Invalid credential(s)", Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<UserDetailsModel>>, t: Throwable) {
                relLoadingOnLogin.visibility=View.GONE
                relLoginOnLogin.visibility= View.VISIBLE

                Snackbar.make(rootRelLogin, "Error! try again.", Snackbar.LENGTH_LONG).show()
            }
        })
    }
}
