package cn.apier.app.ytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import cn.apier.app.ytask.R
import cn.apier.app.ytask.api.UserApi
import cn.apier.app.ytask.application.YTaskApplication
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.model.AccessToken
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    private var todoFragment: Fragment? = null
    private var newFragment: Fragment? = null
    private var finishedFragment: Fragment? = null

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
