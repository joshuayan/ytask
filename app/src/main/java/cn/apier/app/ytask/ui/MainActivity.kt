package cn.apier.app.ytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.text.TextUtils
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.application.YTaskApplication
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.ui.base.BaseActivity
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


class MainActivity : BaseActivity(), SpeechSynthesizerListener {
    override fun onSynthesizeStart(p0: String?) {
        Log.d(Constants.TAG_LOG, "onSynthesizeStart")
    }

    override fun onSpeechFinish(p0: String?) {
        Log.d(Constants.TAG_LOG, "onSpeechFinish")
    }

    override fun onSpeechProgressChanged(p0: String?, p1: Int) {
        Log.d(Constants.TAG_LOG, "onSpeechProgressChanged")
    }

    override fun onSynthesizeFinish(p0: String?) {
        Log.d(Constants.TAG_LOG, "onSynthesizeFinish")
    }

    override fun onSpeechStart(p0: String?) {
        Log.d(Constants.TAG_LOG, "onSpeechStart")
    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int) {
        Log.d(Constants.TAG_LOG, "onSynthesizeDataArrived")
    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.d(Constants.TAG_LOG, "onError")
    }

    private var todoFragment: TodoFragment? = null
    private var newFragment: NewFragment? = null
    private var finishedFragment: FinishedFragment? = null


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        this.newFragment!!.onActivityResult(requestCode, resultCode, data)
    }

}
