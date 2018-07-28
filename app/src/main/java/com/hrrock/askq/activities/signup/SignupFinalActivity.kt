package com.hrrock.askq.activities.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.hrrock.askq.R
import com.hrrock.askq.activities.LoginActivity
import com.hrrock.askq.networks.CallHttpRequest
import com.hrrock.askq.networks.VolleyConnect
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_signup_final.*
import spencerstudios.com.bungeelib.Bungee
import java.io.*

class SignupFinalActivity : AppCompatActivity(), OnMenuItemClickListener<PowerMenuItem> {
    private var ctx: Context? = null
    private var powerMenu: PowerMenu? = null
    private var destination: File? = null
    private var userName: List<String>? = null
    private var mobile: String? = null
    private var name: String? = null
    private var email: String? = null
    private var requestQueue: RequestQueue? = null
    private var stringRequest: StringRequest? = null

    private companion object {
        private const val USER_PREFERENCE = "userinfo"
        private const val CAM_REQ_CODE = 1
        private const val GAL_REQ_CODE = 2
        private const val CAMERA = "camera"
        private const val GALLERY = "gallery"
        private const val SIGNUP_ACTIVITY = "SignupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_final)
        ctx = this

        email = intent.getStringExtra("email")
        userName = intent.getStringExtra("email").split("@").toList()
        mobile = intent.getStringExtra("mobile")
        name = intent.getStringExtra("name")
        usernameOnSignupFinal.text = userName!![0]

        requestQueue = VolleyConnect.getInstance().requestQueue

        setNavigationStatusBarColor()
        backToSignup()
        onDpClick()
        onSignupClick()
    }

    private fun backToSignup() {
        PushDownAnim.setPushDownAnimTo(backToSignup)
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

    private fun onSignupClick() {
        PushDownAnim.setPushDownAnimTo(relSignupOnSignupFinal)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    if (validateData()) {
                        if (checkPwd()) {
                            startUploading()

                            relSignupOnSignupFinal.visibility = View.GONE
                            relLoadingOnSignupFinal.visibility = View.VISIBLE
                            signUp()
                        } else {
                            Snackbar.make(rootRelSignupFinal, "Passwords do not match!", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        Snackbar.make(rootRelSignupFinal, "Field(s) can not be empty!", Snackbar.LENGTH_LONG).show()
                    }
                })
    }

    private fun onDpClick() {
        PushDownAnim.setPushDownAnimTo(dpOnSignup)
                .setScale(PushDownAnim.MODE_SCALE, 0.89f)
                .setOnClickListener({
                    showPhotoSourceDialog()
                })
    }

    private fun showPhotoSourceDialog() {
        powerMenu = PowerMenu.Builder(ctx)
                //     .setHeaderView(R.layout.layout_dialog_header) // header used for title
                //   .setFooterView(R.layout.layout_dialog_footer) // footer used for yes and no buttons
                .addItem(PowerMenuItem("Gallery", false)) // this is body
                .addItem(PowerMenuItem("Camera", false))
                // .setLifecycleOwner(this)
                .setAnimation(MenuAnimation.ELASTIC_CENTER)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setWidth(600)
                .setSelectedEffect(true)
                .setOnMenuItemClickListener(this)
                .build()

        powerMenu!!.showAsAnchorCenter(rootRelSignupFinal)
    }

    override fun onItemClick(position: Int, item: PowerMenuItem?) {
        when (item!!.title) {
            "Gallery" -> {
                val myIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(myIntent, GAL_REQ_CODE)
                powerMenu!!.dismiss()
            }
            "Camera" -> {
                val myIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(myIntent, CAM_REQ_CODE)
                powerMenu!!.dismiss()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
                && resultCode != Activity.RESULT_CANCELED
                && requestCode == CAM_REQ_CODE
                && data != null) {

            createApplicationFolder(data, CAMERA)
        }
        if (resultCode == Activity.RESULT_OK
                && resultCode != Activity.RESULT_CANCELED
                && requestCode == GAL_REQ_CODE
                && data != null) {
            try {
                createApplicationFolder(data, GALLERY)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createApplicationFolder(data: Intent, from: String) {
        val folder = File(Environment.getExternalStorageDirectory(), File.separator + "Pictures/AskQ/")
        folder.mkdirs()

        when (from) {
            CAMERA -> saveImageCamera(data)
            GALLERY -> saveImageGallery(data.data)
        }
    }

    @Throws(IOException::class)
    private fun saveImageGallery(data: Uri?) {
        val thumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, data)
        val bytes = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, bytes)

        destination = File("sdcard/Pictures/AskQ/", "${userName!![0]}.jpg")
        val fo: FileOutputStream
        try {
            destination!!.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val photoDestination = "sdcard/Pictures/AskQ/" + userName!![0] + ".jpg"
        loadSelectedPhoto(photoDestination)
        // startUploading()
    }

    private fun saveImageCamera(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, bytes)

        destination = File("sdcard/Pictures/AskQ/", "${userName!![0]}.jpg")
        val fo: FileOutputStream
        try {
            destination!!.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val photoDestination = "sdcard/Pictures/AskQ/" + userName!![0] + ".jpg"
        loadSelectedPhoto(photoDestination)
        // startUploading()
    }

    private fun loadSelectedPhoto(photoDestination: String) {
        Glide.with(ctx!!).load(photoDestination).into(dpOnSignup)
    }

    private fun startUploading() {
        val dpURL = arrayOf("http://${getString(R.string.ip)}/AskQ/index.php/PhotoController/uploadDP")
        CallHttpRequest(applicationContext, destination!!, SIGNUP_ACTIVITY).execute(dpURL[0])
    }

    private fun validateData(): Boolean {
        return !pwdOnSignupFinal.text.toString().isEmpty() &&
                !cpwdOnSignupFinal.text.toString().isEmpty()
    }

    private fun checkPwd(): Boolean {
        return pwdOnSignupFinal.text.toString() == cpwdOnSignupFinal.text.toString()
    }

    private fun signUp() {
        val url = "http://${getString(R.string.ip)}/AskQ/index.php/CredentialController/signUp?" +
                "email=$email&username=${userName!![0]}&name=$name&mobile=$mobile&password=${pwdOnSignupFinal.text}&profile=${userName!![0]}.jpg"

        stringRequest = StringRequest(url, Response.Listener<String> { response ->
            when (response) {
                "success" -> {
                    startActivity(Intent(ctx, LoginActivity::class.java))
                    Bungee.slideLeft(ctx)
                    finish()
                }
                "failed" -> {
                    Snackbar.make(rootRelSignupFinal, "Failed to register! try again.", Snackbar.LENGTH_LONG).show()

                    relLoadingOnSignupFinal.visibility = View.GONE
                    relSignupOnSignupFinal.visibility = View.VISIBLE
                }
                else -> {
                    Snackbar.make(rootRelSignupFinal, "Failed to register! try again.", Snackbar.LENGTH_LONG).show()

                    relLoadingOnSignupFinal.visibility = View.GONE
                    relSignupOnSignupFinal.visibility = View.VISIBLE
                }
            }
        }, Response.ErrorListener { error ->
            Snackbar.make(rootRelSignupFinal, "Error! try again.", Snackbar.LENGTH_LONG).show()
        })

        requestQueue!!.add(stringRequest)
    }
}