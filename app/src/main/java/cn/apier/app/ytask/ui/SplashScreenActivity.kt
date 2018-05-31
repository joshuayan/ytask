package cn.apier.app.ytask.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import cn.apier.app.ytask.R
import cn.apier.app.ytask.application.YTaskApplication
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.READ_PHONE_STATE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (!report.areAllPermissionsGranted()) {
                            toast("请重启应用允许请求的权限")
                        }
//                        onPermissionCheck()
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()


        val app = this.application as YTaskApplication
//        app.initToken()


        val tvCountDown = find<TextView>(R.id.tvCountDown)
        tvCountDown.text = "10"
        val rangeFlow = Flowable.intervalRange(0L, 10L, 1000L, 1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tvCountDown.text = "${9 - it}"
                }, {}, {
                    if (app.tokenExist()) {
                        gotoMain()
                    } else {
                        toast(this.getString(R.string.err_get_token_failed))
                    }
                })

        val tvSkip = find<TextView>(R.id.tvSkip)
        tvSkip.setOnClickListener {
            if (app.tokenExist()) {
                rangeFlow.dispose()
                gotoMain()
            } else {

                toast(this.getString(R.string.err_get_token_failed))
            }
        }

    }


    private fun gotoMain() {
        val intent = Intent(this.applicationContext, SignInActivity::class.java)
        startActivity(intent)
        this.finish()
    }


}
