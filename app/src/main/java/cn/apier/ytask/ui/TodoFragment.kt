package cn.apier.ytask.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import cn.apier.ytask.R
import cn.apier.ytask.wakeup.MyWakeup
import cn.apier.ytask.wakeup.SimpleWakeupListener
import cn.apier.ytask.wakeup.WakeupParams

/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class TodoFragment : Fragment() {

    private lateinit var myWakeup: MyWakeup
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWakeUp()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_task_list, container, false)

        val btnWakeUp = view.findViewById<Button>(R.id.btnWakeup)
        btnWakeUp.setOnClickListener {
            start()
        }

        val btnStop = view.findViewById<Button>(R.id.btnStop)

        btnStop.setOnClickListener {

            myWakeup.stop()
        }

        return view
    }

    private fun start() {
        val wakeupParams = WakeupParams(this.activity)
        val params = wakeupParams.fetch()
        myWakeup.start(params)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
//        if (context is OnListFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
    }


    private fun initWakeUp() {
        val listener = SimpleWakeupListener()
        myWakeup = MyWakeup(this.activity, listener)
    }


    override fun onDestroy() {
        myWakeup.release()
        super.onDestroy()
    }

}