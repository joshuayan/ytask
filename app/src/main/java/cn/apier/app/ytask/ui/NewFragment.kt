package cn.apier.app.ytask.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import cn.apier.app.ytask.R
import cn.apier.app.ytask.common.Constants
import com.baidu.aip.chatkit.message.MessageInput
import com.baidu.aip.chatkit.model.Message
import com.baidu.aip.chatkit.model.User
import com.baidu.aip.unit.APIService
import com.baidu.aip.unit.exception.UnitError
import com.baidu.aip.unit.listener.OnResultListener
import com.baidu.aip.unit.listener.VoiceRecognizeCallback
import com.baidu.aip.unit.model.CommunicateResponse
import com.baidu.aip.unit.voice.VoiceRecognizer
import java.util.*

class NewFragment : Fragment(), MessageInput.InputListener,
        MessageInput.VoiceInputListener {


    private lateinit var voiceRecognizer: VoiceRecognizer
    private var mid: Int = 0


    private lateinit var messageInput: MessageInput
    private lateinit var sender: User

//    private var messageInput: MessageInput? = null

    private var sessionId: String = ""
    private lateinit var msgAdapter: MessageViewAdapter

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sender = User("0", "kf", "", true)


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_new, container, false)

        val rvMessage: RecyclerView = view.findViewById(R.id.rv_message)

        rvMessage.layoutManager = LinearLayoutManager(view.context)
        msgAdapter = MessageViewAdapter()
        rvMessage.adapter = msgAdapter

        messageInput = view.findViewById(R.id.input)


        voiceRecognizer = VoiceRecognizer()
        voiceRecognizer.init(this.activity, messageInput.getVoiceInputButton())
        voiceRecognizer.setVoiceRecognizerCallback(VoiceRecognizeCallback { text ->
            Log.d("ytask", "voice text:$text")
//            messageInput.inputEditText.setText(text)
            msgAdapter.addMessage(text)

            val msg = Message(mid++.toString(), sender, text)

            sendMessage(msg)


        })
        messageInput.inputEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSubmit(v.editableText)
                v.text = ""
            }
            true
        })

        messageInput.setInputListener(this)
        messageInput.setAudioInputListener(this)

        return view
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
        voiceRecognizer.onActivityResult(requestCode, resultCode, data)
    }


    private fun sendMessage(message: Message) {

        APIService.getInstance().communicate(object : OnResultListener<CommunicateResponse> {
            override fun onResult(result: CommunicateResponse) {

                handleResponse(result)
            }

            override fun onError(error: UnitError) {

            }
        }, Constants.SCENE_ID, message.text, sessionId)

    }

    private fun handleResponse(response: CommunicateResponse?) {
        if (response != null) {
            sessionId = response.result.sessionId

            //  如果有对于的动作action，请执行相应的逻辑
            val actionList = response.result.actionList
            for (action in actionList) {

                if (!TextUtils.isEmpty(action.say)) {
                    val sb = StringBuilder()
                    sb.append(action.say)

                    val message = Message(mid++.toString(), sender, sb.toString(), Date())
//                    messagesAdapter.addToStart(message, true)
//                    if (action.hintList.size > 0) {
//                        message.hintList = action.hintList
//                    }

                }

                when (action.actionId) {
                    "add_task_satisfy" -> {

                        if (response.result.schema != null) {
                            val schema=response.result.schema!!
                            val items=schema.getSlotsByType(Constants.SLOT_TIME)
                            val todos=schema.getSlotsByType(Constants.SLOT_TODO)
                            val msg="time:${items[0].normalizedWord},task:${todos.map { it.normalizedWord }.joinToString()}"
                            msgAdapter.addMessage(msg)

                        }

//                        val msg = response.result.schema?.botMergedSlots?.joinToString { "$it," } ?: ""
//                        Log.d(Constants.TAG_LOG, "Add Task. $msg")
//
//                        msgAdapter.addMessage(msg)
                    }
                    else -> Log.e(Constants.TAG_LOG, "Unknown ActionId ${action.actionId}")
                }

//                // 执行自己的业务逻辑
//                if ("start_work_satisfy" == action.actionId) {
//                    Log.i("wtf", "开始扫地")
//                } else if ("stop_work_satisfy" == action.actionId) {
//                    Log.i("wtf", "停止工作")
//                } else if ("move_action_satisfy" == action.actionId) {
//                    Log.i("wtf", "移动")
//                } else if ("timed_charge_satisfy" == action.actionId) {
//                    Log.i("wtf", "定时充电")
//                } else if ("timed_task_satisfy" == action.actionId) {
//                    Log.i("wtf", "定时扫地")
//                } else if ("sing_song_satisfy" == action.actionId) {
//                    Log.i("wtf", "唱歌")
//                }
            }
        }
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


    override fun onSubmit(input: CharSequence?): Boolean {
        return true
    }

    override fun onVoiceInputClick() {
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.RECORD_AUDIO) != PackageManager
                .PERMISSION_GRANTED) {
            this.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 100)
            return
        }
        voiceRecognizer.onClick()
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



