/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package cn.apier.ytask.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.aip.chatkit.ImageLoader;
import com.baidu.aip.chatkit.message.MessageInput;
import com.baidu.aip.chatkit.message.MessagesList;
import com.baidu.aip.chatkit.message.MessagesListAdapter;
import com.baidu.aip.chatkit.model.Message;
import com.baidu.aip.chatkit.model.User;
import com.baidu.aip.unit.APIService;
import com.baidu.aip.unit.HintAdapter;
import com.baidu.aip.unit.exception.UnitError;
import com.baidu.aip.unit.listener.OnResultListener;
import com.baidu.aip.unit.listener.VoiceRecognizeCallback;
import com.baidu.aip.unit.model.AccessToken;
import com.baidu.aip.unit.model.CommunicateResponse;
import com.baidu.aip.unit.model.Scene;
import com.baidu.aip.unit.voice.VoiceRecognizer;
import com.baidu.aip.unit.widget.BasePopupWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.apier.ytask.R;

public class MainActivityDemo extends AppCompatActivity
        implements MessagesListAdapter.OnLoadMoreListener, MessageInput.InputListener,
        MessageInput.VoiceInputListener {

    private static final int SCENE_SWEEPER_ROBOT = 3087;
    private static final int SCENE_NAVIGATE = 3109;

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    protected HintAdapter hintAdapter;

    private TextView titleTv;
    private RelativeLayout titleRl;
    private RelativeLayout rootRl;
    private BasePopupWindow popupWindow;
    private MessagesList messagesList;
    private MessageInput input;

    private User sender;
    private User cs;
    private String sessionId = "";
    private VoiceRecognizer voiceRecognizer;
    private Scene curScene;
    private int id = 0;
    private String accessToken;
    private List<Message> waitList = new ArrayList<>();
    private Map<Integer, String> sceneMap = new HashMap<Integer, String>();

    private ListView dataListview;
    private List<Scene> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        findView();
        initAdapter();

        voiceRecognizer = new VoiceRecognizer();
        voiceRecognizer.init(this, input.getVoiceInputButton());
        voiceRecognizer.setVoiceRecognizerCallback(new VoiceRecognizeCallback() {
            @Override
            public void callback(String text) {
                Message message = new Message(String.valueOf(id++), sender, text);
                messagesAdapter.addToStart(message, true);

                sendMessage(message);
            }
        });
        input.getInputEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    onSubmit(v.getEditableText());
                    v.setText("");
                }
                return true;
            }
        });


        hintAdapter = new HintAdapter(this);

        changeScene(dataList.get(0));
        initPopupWindow();

        initAccessToken();
    }

    // 演示数据，开发者可到ai.baidu.com 官网理解与交互技术(UNIT)板块申请并训练自己的场景机器人
    private void initData() {
        sender = new User("0", "kf", "", true);
        cs = new User("1", "客服", "", true);

        sceneMap.put(SCENE_SWEEPER_ROBOT, "扫地机器人");
        sceneMap.put(SCENE_NAVIGATE, "车载导航");

        dataList = new ArrayList<Scene>();
        dataList.add(new Scene(SCENE_SWEEPER_ROBOT , "扫地机器人"));
        dataList.add(new Scene(SCENE_NAVIGATE , "车载导航"));

    }

    private void findView() {
        this.rootRl = (RelativeLayout) findViewById(R.id.root);
        this.titleRl = (RelativeLayout) findViewById(R.id.title_rl);
        this.titleTv = (TextView) findViewById(R.id.title_tv);
        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        input = (MessageInput) findViewById(R.id.input);

        input.setInputListener(this);
        input.setAudioInputListener(this);
//        hintRV = (RecyclerView) findViewById(R.id.hint_rv);
        titleRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.getPopupWindow().setAnimationStyle(R.style.animTranslate);
                popupWindow.showAtLocation(rootRl, Gravity.BOTTOM, 0, 0);
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
            }
        });

    }

    /**
     * 为了防止破解app获取ak，sk，建议您把ak，sk放在服务器端。
     */
    private void initAccessToken() {
        APIService.getInstance().init(getApplicationContext());
        APIService.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                accessToken = result.getAccessToken();
                Log.i("MainActivity", "AccessToken->" + result.getAccessToken());
                if (!TextUtils.isEmpty(accessToken)) {
                    resendWaitList();
                }

            }

            @Override
            public void onError(UnitError error) {
                Log.i("wtf", "AccessToken->" + error.getErrorMessage());
            }
        }, "jMWIPDmK6zaxiK9KMCbb0hQl", "D4d5DD51gElVGkoHuoOZZfSHFDDT5K5k");
    }

    /**
     * 重发未发送成功的消息
     */
    private void resendWaitList() {
        for (Message message : waitList) {
            sendMessage(message);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Message message = new Message(String.valueOf(id++), cs, "主人，很高兴为你服务！", new Date());
        // messagesAdapter.addToStart(message, true);
    }


    @Override
    public boolean onSubmit(CharSequence input) {

        Message message = new Message(String.valueOf(id++), sender, input.toString(), new Date());
        messagesAdapter.addToStart(message, true);

        sendMessage(message);
        return true;
    }

    @Override
    public void onVoiceInputClick() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 100);
            return;
        }

        voiceRecognizer.onClick();
    }

    private void initAdapter() {
        messagesAdapter = new MessagesListAdapter<>(sender.getId(), imageLoader);
        // messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.setOnHintClickListener(new MessagesListAdapter.OnHintClickListener<Message>() {
            @Override
            public void onHintClick(String hint) {
                Message message = new Message(String.valueOf(id++), sender, hint, new Date());
                sendMessage(message);
                messagesAdapter.addToStart(message, true);
            }
        });
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        //                        AppUtils.showToast(MainActivity.this,
                        //                                message.getUser().getName() + " avatar click",
                        //                                false);
                    }
                });
        this.messagesList.setAdapter(messagesAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        voiceRecognizer.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        voiceRecognizer.onDestroy();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {

    }

    /**
     * 切换场景
     *
     * @param scene
     */
    private void changeScene(Scene scene) {

        curScene = scene;
        titleTv.setText(scene.getName());

        sessionId = "";
        Message message = new Message(String.valueOf(id++), sender, "你好", new Date());
        sendMessage(message);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) {
                    text = "[attachment]";
                }

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }

    private void sendMessage(Message message) {

        if (TextUtils.isEmpty(accessToken)) {
            waitList.add(message);
            return;
        }

        APIService.getInstance().communicate(new OnResultListener<CommunicateResponse>() {
            @Override
            public void onResult(CommunicateResponse result) {

                handleResponse(result);
            }

            @Override
            public void onError(UnitError error) {

            }
        }, curScene.getId(), message.getText(), sessionId);

    }

    private void handleResponse(CommunicateResponse response) {
        if (response.result != null) {
            sessionId = response.result.sessionId;

            //  如果有对于的动作action，请执行相应的逻辑
            List<CommunicateResponse.Action> actionList = response.result.actionList;
            for (CommunicateResponse.Action action : actionList) {

                if (!TextUtils.isEmpty(action.say)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(action.say);

                    Message message = new Message(String.valueOf(id++), cs, sb.toString(), new Date());
                    messagesAdapter.addToStart(message, true);
                    if (action.hintList.size() > 0) {
                        message.setHintList(action.hintList);
                    }

                }

                // 执行自己的业务逻辑
                if ("start_work_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "开始扫地");
                } else if ("stop_work_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "停止工作");
                } else if ("move_action_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "移动");
                } else if ("timed_charge_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "定时充电");
                } else if ("timed_task_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "定时扫地");
                } else if ("sing_song_satisfy".equals(action.actionId)) {
                    Log.i("wtf", "唱歌");
                }
            }
        }
    }

    private void initPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48 * 2 + 3, metrics);
        // create popup window
        popupWindow = new BasePopupWindow(this, R.layout.scene_list_view, ViewGroup.LayoutParams.MATCH_PARENT,
                height) {
            @Override
            protected void initView() {
                View view=getContentView();
                dataListview = (ListView) view.findViewById(R.id.data_list);
                dataListview.setAdapter(new ArrayAdapter<Scene>(MainActivityDemo.this, R.layout.item_popup_view, dataList));
            }

            @Override
            protected void initEvent() {
                dataListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        popupWindow.getPopupWindow().dismiss();
                        Scene scene = dataList.get(position);
                        changeScene(scene);
                    }
                });
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);
                    }
                });
            }
        };
    }

}
