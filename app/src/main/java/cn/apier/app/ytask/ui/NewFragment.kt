package cn.apier.app.ytask.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cn.apier.app.ytask.R
import cn.apier.app.ytask.common.Constants
import cn.apier.app.ytask.util.JsonParser
import com.iflytek.aiui.AIUIAgent
import com.iflytek.aiui.AIUIConstant
import com.iflytek.aiui.AIUIMessage
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import java.io.IOException

class NewFragment : Fragment() {


    private lateinit var msgAdapter: MessageViewAdapter
    private lateinit var mIatDialog: RecognizerDialog
    private lateinit var mToast: Toast

    private lateinit var aiuiAgent: AIUIAgent
    private var aiuiState = AIUIConstant.STATE_IDLE

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToast = Toast.makeText(this.activity, "", Toast.LENGTH_SHORT)

//        mIatDialog = RecognizerDialog(this.activity, { code ->
//            run {
//                Log.d(Constants.TAG_LOG, "SpeechRecognizer init() code = " + code)
//                if (code != ErrorCode.SUCCESS) {
//                    showTip("初始化失败，错误码：" + code)
//                }
//            }
//        })
//
//        mIatDialog.setParameter("asr_sch", "1");
//        mIatDialog.setParameter("nlp_version", "3.0");
//        mIatDialog.setListener(object : RecognizerDialogListener {
//            override fun onResult(result: RecognizerResult, p1: Boolean) {
//
//                val txt = JsonParser.parseIatResult(result.resultString)
//
//                Log.d(Constants.TAG_LOG, "text result:$txt from json [[${result.resultString}]]")
//                msgAdapter.addMessage(txt)
//            }
//
//            override fun onError(p0: SpeechError?) {
//            }
//
//        })

        aiuiAgent = AIUIAgent.createAgent(this.activity, getAIUIParams(), { aiuiEvent ->
            run {
                when (aiuiEvent.eventType) {
                    AIUIConstant.EVENT_RESULT -> {
                        Log.d(Constants.TAG_LOG, "got result:${aiuiEvent.info}")
                    }
                    AIUIConstant.EVENT_STATE -> {
                        Log.d(Constants.TAG_LOG, "state event: ${aiuiEvent.arg1}")
                        this.aiuiState = aiuiEvent.arg1
                    }
                    AIUIConstant.EVENT_SLEEP -> Log.d(Constants.TAG_LOG, "sleep.")
                    AIUIConstant.EVENT_START_RECORD -> Log.d(Constants.TAG_LOG, "start record.")

                    AIUIConstant.EVENT_STOP_RECORD -> {
                        Log.d(Constants.TAG_LOG, "stop record.")
                    }

                    else -> {
                        Log.d(Constants.TAG_LOG, "got other event: ${aiuiEvent.eventType},code:${aiuiEvent.arg1},info: ${aiuiEvent.info}")
                    }
                }
            }
        })
        val startMsg = AIUIMessage(AIUIConstant.CMD_START, 0, 0, null, null)
        aiuiAgent.sendMessage(startMsg)

    }


    private fun getAIUIParams(): String {
        var params = ""

        val assetManager = resources.assets
        try {
            val ins = assetManager.open("cfg/aiui_phone.cfg")
            val buffer = ByteArray(ins.available())

            ins.read(buffer)
            ins.close()

            params = String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return params
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_new, container, false)

        val rvMessage: RecyclerView = view.findViewById(R.id.rv_message)

        rvMessage.layoutManager = LinearLayoutManager(view.context)
        msgAdapter = MessageViewAdapter()
        rvMessage.adapter = msgAdapter



        view.findViewById<Button>(R.id.btnSpeech).setOnClickListener {
            //            mIatDialog.show()

            if (this.aiuiState != AIUIConstant.STATE_WORKING) {
                val wakeUpMsg = AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, null, null)
                aiuiAgent.sendMessage(wakeUpMsg)

            }
            val params = "sample_rate=16000,data_type=audio"
            val writeMsg = AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null)
            aiuiAgent.sendMessage(writeMsg)
        }

        return view
    }


    private fun showTip(str: String) {
        mToast.setText(str)
        mToast.show()
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)


//        if (context is OnFragmentInteractionListener) {
//            mListener = context
//        } else {
//            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }


    private class MessageViewAdapter : RecyclerView.Adapter<MessageViewAdapter.MessageViewHolder>() {

        private var msgs: MutableList<String> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_message, parent, false)
            return MessageViewHolder(view)

        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

            holder.tvMsg.text = msgs[position]
        }

        fun addMessage(msg: String) {
            msgs.add(msg)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return msgs.size
        }

        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val tvMsg: TextView = itemView.findViewById(R.id.tvMsg)

        }
    }


}// Required empty public constructor



