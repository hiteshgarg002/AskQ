package com.hrrock.askq.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.hrrock.askq.R
import com.hrrock.askq.networks.VolleyConnect
import com.jaredrummler.materialspinner.MaterialSpinner
import com.thekhaeng.pushdownanim.PushDownAnim
import de.mateware.snacky.Snacky
import kotlinx.android.synthetic.main.activity_ask.*
import kotlinx.android.synthetic.main.snippet_top_ask.*
import org.json.JSONArray
import spencerstudios.com.bungeelib.Bungee
import java.text.SimpleDateFormat
import java.util.*


class AskActivity : AppCompatActivity() {
    private var ctx: Context? = null
    private var topic: MaterialSpinner? = null
    private var topicList: MutableList<String>? = null
    private var requestQueue: RequestQueue? = null
    private var jsonArrayRequest: JsonArrayRequest? = null
    private var stringRequest: StringRequest? = null
    private var preferences: SharedPreferences? = null

    private companion object {
        private const val USER_PREFERENCE = "userinfo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask)
        ctx = this

        requestQueue = VolleyConnect.getInstance().requestQueue
        preferences = ctx!!.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE)
        nameOnAsk.text=preferences!!.getString("name","")

        topic = findViewById(R.id.topicSpinnerOnAsk)
        topicList = ArrayList()
        getTopics()

        setNavigationStatusBarColor()
        backPress()
        postPress()
    }

    private fun backPress() {
        PushDownAnim.setPushDownAnimTo(backFromAsk)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    onBackPressed()
                }
    }

    private fun postPress() {
        PushDownAnim.setPushDownAnimTo(relPostOnAsk)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener {
                    if (validateData()) {
                        postQuestion()
                    } else {
                        Snackbar.make(rootRelAsk, "Either question or topic is blank!", Snackbar.LENGTH_LONG).show()
                    }
                }
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

    private fun getTopics() {
        topicList!!.add("--Topic--")
        val url = "http://${getString(R.string.ip)}/AskQ/index.php/AskController/getTopics"

        jsonArrayRequest = JsonArrayRequest(url, Response.Listener<JSONArray> { response ->
            if (response.length() > 0) {
                for (i in 0 until response.length()) {
                    val jsonObject = response.optJSONObject(i)
                    topicList!!.add(jsonObject.optString("name"))
                }

                topic!!.setItems(topicList!!)
            }
        }, Response.ErrorListener { error -> })

        requestQueue!!.add(jsonArrayRequest)
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()).toString()
    }

    private fun postQuestion() {
        val url = "http://${getString(R.string.ip)}/AskQ/index.php/AskController/postQuestion?" +
                "username=${if (anonymousCheckOnAsk.isChecked) {
                    "Anonymous"
                } else {
                    preferences!!.getString("username", "")
                }}&question=${questionOnAsk.text}&topic=${topic!!.selectedIndex}&date=${getCurrentDate()}"

        stringRequest = StringRequest(url, Response.Listener<String> { response ->
         //   Toast.makeText(ctx,""+response,Toast.LENGTH_LONG).show()
            when (response) {
                "success" -> {
                    onBackPressed()
                    Bungee.slideLeft(ctx)
                    successSnack()
                }
                "failed" -> Snackbar.make(rootRelAsk, "Error! try again.", Snackbar.LENGTH_LONG).show()
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(ctx,""+error.message,Toast.LENGTH_LONG).show()
            Snackbar.make(rootRelAsk, "Error! try again.", Snackbar.LENGTH_LONG).show()
        })

        requestQueue!!.add(stringRequest)
    }

    private fun validateData(): Boolean {
        return questionOnAsk.text.isNotEmpty() && topic!!.selectedIndex != 0
    }

    private fun successSnack() {
        Snacky.builder()
                .setActivity(this@AskActivity)
                .setText("Question has been posted!")
                .setDuration(Snacky.LENGTH_LONG)
                .success()
                .show()
    }
}
