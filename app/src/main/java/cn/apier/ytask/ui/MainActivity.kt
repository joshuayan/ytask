package cn.apier.ytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.util.Log
import cn.apier.ytask.R
import cn.apier.ytask.api.UserApi
import cn.apier.ytask.application.YTaskApplication
import cn.apier.ytask.common.Constants
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.AccessToken
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity(),Speaker, SpeechSynthesizerListener {
    override fun onSynthesizeStart(p0: String?) {
        Log.d(Constants.TAG_LOG,"onSynthesizeStart")
    }

    override fun onSpeechFinish(p0: String?) {
        Log.d(Constants.TAG_LOG,"onSpeechFinish")
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        Log.d(Constants.TAG_LOG,"onSpeechProgressChanged")
    }

    override fun onSynthesizeFinish(p0: String?) {
        Log.d(Constants.TAG_LOG,"onSynthesizeFinish")
    }

    override fun onSpeechStart(p0: String?) {
        Log.d(Constants.TAG_LOG,"onSpeechStart")
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        Log.d(Constants.TAG_LOG,"onSynthesizeDataArrived")
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.d(Constants.TAG_LOG,"onError")
    }

    private var todoFragment: TodoFragment? = null
    private var newFragment: NewFragment? = null
    private var finishedFragment: FinishedFragment? = null

    private val speechSynthesizer = SpeechSynthesizer.getInstance()

    private lateinit var yTaskApplication: YTaskApplication

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        switchTab(item.itemId)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoFragment = TodoFragment()
        val fragmentTrans = this.supportFragmentManager.beginTransaction()
        fragmentTrans.add(R.id.fragment_container, todoFragment)
        fragmentTrans.commit()
        nv_main.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        this.yTaskApplication = this.getApplication() as YTaskApplication

        initAccessToken()

        initSpeech()

    }

    private fun initSpeech() {
        this.speechSynthesizer.setContext(this)
        this.speechSynthesizer.setSpeechSynthesizerListener(this)

        this.speechSynthesizer.setApiKey("ILo4xDbLmdmIE7peI60cec3n", "iAEX4xSreMCcTp4hUzLHl4fzDrOjpCua")
        this.speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        this.speechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT)
        val authInfo = this.speechSynthesizer.auth(TtsMode.MIX)

        if (authInfo.isSuccess()) {
            Log.d(Constants.TAG_LOG, "auth success")
        } else {
            val errorMsg = authInfo.getTtsError().getDetailMessage()
            Log.d(Constants.TAG_LOG, "auth failed errorMsg=$errorMsg")
        }

        // 初始化tts
        speechSynthesizer.initTts(TtsMode.MIX)


    }


    override fun speak(txt: String) {
        val result = this.speechSynthesizer.speak(txt)

        Log.d(Constants.TAG_LOG, "speak result: $result")
        if (result < 0) {
            Log.e(Constants.TAG_LOG, "error,please look up error code in doc or URL:http://yuyin.baidu.com/docs/tts/122 ")
        }

    }

    private fun switchTab(itemId: Int) {
        var targetFragment: Fragment? = null
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        when (itemId) {
            R.id.nav_finished -> {

                if (finishedFragment == null) {
                    finishedFragment = FinishedFragment()
                    fragmentTransaction.add(R.id.fragment_container, finishedFragment)
                }
                targetFragment = this.finishedFragment!!
            }

            R.id.nav_todo -> {
                if (todoFragment == null) {
                    todoFragment = TodoFragment()
                    fragmentTransaction.add(R.id.fragment_container, todoFragment)
                }
                targetFragment = this.todoFragment!!
            }
            R.id.nav_new -> {
                if (newFragment == null) {
                    newFragment = NewFragment()

                    newFragment!!.setSpeaker(this)
                    fragmentTransaction.add(R.id.fragment_container, newFragment)
                }
                targetFragment = this.newFragment!!
            }
        }
        hideTab(fragmentTransaction)
        fragmentTransaction.show(targetFragment)
        fragmentTransaction.commit()
    }


    private fun hideTab(fragmentTransaction: FragmentTransaction) {

        listOf(todoFragment, newFragment, finishedFragment).forEach { it?.also { fg -> fragmentTransaction.hide(fg) } }
    }

    /**
     * 为了防止破解app获取ak，sk，建议您把ak，sk放在服务器端。
     */
    private fun initAccessToken() {

        APIService.getInstance().init(applicationContext)
        this.yTaskApplication.apiProxy(UserApi::class.java).queryBDApplicationInfo().subscribeOn(Schedulers.io()).subscribe {

            APIService.getInstance().initAccessToken(object : OnResultListener<AccessToken> {
                override fun onResult(result: AccessToken) {
                    val accessToken = result.accessToken
                    Log.i("MainActivity", "AccessToken->" + result.accessToken)
                    if (!TextUtils.isEmpty(accessToken)) {
                    }

                }

                override fun onError(error: UnitError) {
                    Log.i("wtf", "AccessToken->" + error.errorMessage)
                }
            }, it.data?.appKey, it.data?.secretKey)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        this.newFragment!!.onActivityResult(requestCode, resultCode, data)
    }

}
