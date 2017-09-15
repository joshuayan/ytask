package cn.apier.app.ytask.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import cn.apier.app.ytask.R
import cn.apier.app.ytask.application.YTaskApplication
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
