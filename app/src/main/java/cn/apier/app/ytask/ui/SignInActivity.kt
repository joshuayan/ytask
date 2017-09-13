package cn.apier.app.ytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.application.YTaskApplication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Created by yanjunhua on 2017/9/5.
 */
class SignInActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setContentView(R.layout.activity_sign_in)

        val txtMobile = find<EditText>(R.id.txtMobile)
        val txtPassword = find<EditText>(R.id.txtPassword)
        val lltForm = find<LinearLayout>(R.id.llt_form)
        val btnSignIn = find<Button>(R.id.btnSignIn)
        val progressBar = find<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        btnSignIn.setOnClickListener {

            val mobile = txtMobile.text.toString()
            val password = txtPassword.text.toString()
            if (mobile.isNullOrBlank() || mobile.isNullOrEmpty()) {
                txtMobile.error = this.getString(R.string.err_mobile_required)
                txtMobile.requestFocus()
                return@setOnClickListener
            }

            if (password.isNullOrEmpty() || password.isNullOrBlank()) {
                txtPassword.error = this.getString(R.string.err_password_required)
                txtPassword.requestFocus()
                return@setOnClickListener
            }

            it.isEnabled = false

            lltForm.visibility = View.GONE
            progressBar.visibility = View.VISIBLE


            val application = this.application as YTaskApplication
            application.retrofit().create(UserApi::class.java).signIn(mobile, password).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe { result ->
                Log.d("ytask", result.toString())
                if (result.success) {
                    application.signedIn(mobile)
                    toast("signed in")
                    gotoMain()
                } else {
                    toast("Fail to sign in.")
                    progressBar.visibility = View.GONE
                    lltForm.visibility = View.VISIBLE
                    it.isEnabled = true
                }
            }


        }

    }

    private fun gotoMain() {
        val intent = Intent(this.applicationContext, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}