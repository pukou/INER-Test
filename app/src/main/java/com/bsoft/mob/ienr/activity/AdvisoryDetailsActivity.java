package com.bsoft.mob.ienr.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.BaseApi;
import com.bsoft.mob.ienr.helper.PhotoDialogHelp;
import com.bsoft.mob.ienr.photo.photopicker.PhotoPicker;
import com.bsoft.mob.ienr.photo.photopicker.utils.ImageCaptureManager;
import com.bsoft.mob.ienr.the_recording.AudioRecordButton;
import com.bsoft.mob.ienr.the_recording.BitmapUtils;
import com.bsoft.mob.ienr.the_recording.ChatConst;
import com.bsoft.mob.ienr.the_recording.ChatMessageBean;
import com.bsoft.mob.ienr.the_recording.ChatMessageListBean;
import com.bsoft.mob.ienr.the_recording.ChatRecyclerAdapter;
import com.bsoft.mob.ienr.the_recording.MediaManager;
import com.bsoft.mob.ienr.the_recording.MyLinearLayoutManager;
import com.bsoft.mob.ienr.the_recording.PicturePreviewActivity;
import com.bsoft.mob.ienr.util.GlideUtils;
import com.bsoft.mob.ienr.util.NetException;
import com.bsoft.mob.ienr.util.OkhttpUtils;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.view.AlertBox;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;
//import com.zxy.tiny.Tiny;
//import com.zxy.tiny.callback.FileCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//import me.iwf.photopicker.PhotoPicker;


public class AdvisoryDetailsActivity extends BaseBarcodeActivity implements View.OnClickListener {

    private PullLoadMoreRecyclerView recycle_message;
    private ImageView iv_voice_cut;
    private EditText et_message_send;
    private ImageView iv_add_photo;
    private LinearLayout ll_overall;
    private boolean isOverall;//是否是录音的标记
    private AudioRecordButton tv_voice_type;
    private ImageCaptureManager captureManager;
    private PhotoDialogHelp photoDialogHelp;
    private List<ChatMessageBean> tblist;
    private ChatRecyclerAdapter chatRecyclerAdapter;
    private int number = 10;
    private ArrayList<String> imageList;
    private BitmapUtils bitmapUtils;
    private Gson gson;
    private String consultId;
    private Map mapdata;
    private boolean isRead;
    private Map<Integer, Integer> imagePosition;
        private Tiny.FileCompressOptions options;

    @Override
    public void initBarBroadcast() {

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_advisory_details;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        actionBar.setTitle("咨询详情");
        recycle_message = (PullLoadMoreRecyclerView) findViewById(R.id.recycle_message);
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(mContext);
        linearLayoutManager.setStackFromEnd(true);
        recycle_message.getRecyclerView().setLayoutManager(linearLayoutManager);
        recycle_message.setPushRefreshEnable(false);
        iv_voice_cut = (ImageView) findViewById(R.id.iv_voice_cut);
        iv_voice_cut.setOnClickListener(this);
        et_message_send = (EditText) findViewById(R.id.et_message_send);
        et_message_send.setOnClickListener(this);
        iv_add_photo = (ImageView) findViewById(R.id.iv_add_photo);
        iv_add_photo.setOnClickListener(this);
        ll_overall = (LinearLayout) findViewById(R.id.ll_overall);
        ll_overall.setOnClickListener(this);
        tv_voice_type = findViewById(R.id.tv_voice_type);
        consultId = getIntent().getStringExtra("consultId");
        isRead = getIntent().getBooleanExtra("isRead", false);
        mapdata = new HashMap<>();
        imagePosition = new HashMap<>();
        initData();
        initClick();
    }

    private void initData() {
        options = new Tiny.FileCompressOptions();
        tv_voice_type.setThis(this);
        bitmapUtils = new BitmapUtils();

        if (captureManager == null) {
            captureManager = new ImageCaptureManager(getApplicationContext());
        }
        photoDialogHelp = new PhotoDialogHelp(this, captureManager);
        tblist = new ArrayList<>();
        //adapter图片数据
        imageList = new ArrayList<String>();
        //图片下标位置
        chatRecyclerAdapter = new ChatRecyclerAdapter(this, tblist);
        //加载本地聊天记录
        recycle_message.setAdapter(chatRecyclerAdapter);
        if (gson == null) {
            gson = new Gson();
        }
        //请求接口
        requestData();
        if (isRead) {
            //有未读信息的时候才去请求阅读接口
            requestRead();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_voice_cut://语音切换
//                if (!isOverall){
//                    //如果此时是打字状态，点击切换语音按钮
//
//                }
                isOverall = !isOverall;
                if (isOverall) {
                    tv_voice_type.setVisibility(View.VISIBLE);
                    et_message_send.setVisibility(View.GONE);
                    iv_voice_cut.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                    if (isSoftShowing()) {
                        hideKeyboard();
                    }

                } else {
                    tv_voice_type.setVisibility(View.GONE);
                    et_message_send.setVisibility(View.VISIBLE);
                    iv_voice_cut.setBackgroundResource(R.mipmap.voice_btn_normal);
                }
                break;
            case R.id.iv_add_photo://显示拍照
                photoDialogHelp.showDialog();
                break;

        }
    }

    @SuppressLint("NewApi")
    private void initClick() {
        //录制按钮的监听
        tv_voice_type.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onStart() {
                chatRecyclerAdapter.stopPlayVoice();
            }

            @Override
            public void onFinished(float seconds, String filePath) {
                //刷新适配器
                sendVoice(seconds, filePath);
            }
        });
        chatRecyclerAdapter.setSendErrorListener(new ChatRecyclerAdapter.SendErrorListener() {
            @Override
            public void onClick(int position) {
                ChatMessageBean tbub = tblist.get(position);
                if (tbub.getSessionType() == ChatRecyclerAdapter.TO_USER_VOICE) {
                    tblist.remove(position);
                } else if (tbub.getSessionType() == ChatRecyclerAdapter.TO_USER_IMG) {
                    tblist.remove(position);
                }
                chatRecyclerAdapter.notifyItemRemoved(position);
            }
        });
        chatRecyclerAdapter.setVoiceIsReadListener(new ChatRecyclerAdapter.VoiceIsRead() {

            @Override
            public void voiceOnClick(int position) {
                for (int i = 0; i < chatRecyclerAdapter.unReadPosition.size(); i++) {
                    if (chatRecyclerAdapter.unReadPosition.get(i).equals(position + "")) {
                        chatRecyclerAdapter.unReadPosition.remove(i);
                        break;
                    }
                }
            }

        });
        recycle_message.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                int size = tblist.size();
                tblist.clear();
                imageList.clear();
                imagePosition.clear();
//                chatRecyclerAdapter.notifyItemInserted(tblist.size() - 1);
                chatRecyclerAdapter.notifyItemRangeRemoved(0, size);
                requestData();

            }

            @Override
            public void onLoadMore() {
                recycle_message.setPullLoadMoreCompleted();
            }
        });
        chatRecyclerAdapter.setOnItemClickLitener(new ChatRecyclerAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), PicturePreviewActivity.class);
                intent.putExtra("url", imageList.get(imagePosition.get(position)));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                AlertBox.Show(AdvisoryDetailsActivity.this, "是否撤回消息", null, "撤回", "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //执行删除操作
                                dialog.dismiss();
                                mapdata.clear();
                                mapdata.put("sessionId", tblist.get(position).getSessionId());
                                String baseUrl = BaseApi.advisoryUrl;
                                String url = new StringBuffer(baseUrl)
                                        .append(APIUrlConfig.BaseConsultUrl)
                                        .append("session/cancel").toString();
                                try {
                                    OkhttpUtils.getInstance().doPost(url, mapdata, new OkhttpUtils.MyCallback() {
                                        @Override
                                        public void onSuccess(String body) {
                                            tblist.remove(position);
                                            chatRecyclerAdapter.notifyItemRangeRemoved(position, 1);
                                        }

                                        @Override
                                        public void onFailture(String e) {
                                            StringUtil.showToast("撤回失败");
                                        }

                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onFinish() {

                                        }
                                    });
                                } catch (NetException e) {
                                    e.printStackTrace();
                                } catch (SocketTimeoutException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        });
        et_message_send.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //发送文字
                    senMeg();
                    return true;
                }
                return false;
            }
        });
        recycle_message.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int state) {
                super.onScrollStateChanged(recyclerView, state);
                if (state == RecyclerView.SCROLL_STATE_DRAGGING || state == RecyclerView.SCROLL_STATE_SETTLING) {//滚动中和惯性滑动
                    sIsScrolling = true;
                    Glide.with(AppApplication.getContext()).pauseRequests();
                } else if (state == RecyclerView.SCROLL_STATE_IDLE) {//停止滚动
                    if (sIsScrolling == true) {
                        Glide.with(AppApplication.getContext()).resumeRequests();

                    }
                    sIsScrolling = false;
                }
            }
        });
    }
    private boolean sIsScrolling;

    /**
     * 发送语音
     */

    protected void sendVoice(final float seconds, final String filePath) {
        Observable.create(new ObservableOnSubscribe<List<ChatMessageBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatMessageBean>> emitter) throws Exception {
                tblist.add(getTbubImage(ChatRecyclerAdapter.TO_USER_VOICE,null,filePath,ChatConst.COMPLETED,seconds,0,0));
                emitter.onNext(tblist);
                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ChatMessageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ChatMessageBean> chatMessageBeans) {
                        chatRecyclerAdapter.isPicRefresh=true;
                        chatRecyclerAdapter.notifyItemInserted(chatMessageBeans.size() - 1);
                        recycle_message.getRecyclerView().smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        upFile(1,filePath,seconds,"mp3");
                    }
                });
    }
    /**
     * 发送图片
     */
//    int i = 1;

    protected void sendImage(final String filePath,int w,int h) {

        Observable.create(new ObservableOnSubscribe<List<ChatMessageBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ChatMessageBean>> emitter) throws Exception {
                tblist.add(getTbubImage(ChatRecyclerAdapter.TO_USER_IMG,null,filePath, ChatConst.SENDING,0f,w,h));
                imageList.add(filePath);
                imagePosition.put(tblist.size()-1,imageList.size()-1);
                emitter.onNext(tblist);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Observer<List<ChatMessageBean>>() {
              @Override
              public void onSubscribe(Disposable d) {

              }

              @Override
              public void onNext(List<ChatMessageBean> chatMessageBeans) {
                  chatRecyclerAdapter.isPicRefresh=true;
                  chatRecyclerAdapter.notifyItemInserted(chatMessageBeans.size() - 1);
                  recycle_message.getRecyclerView().smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
              }

              @Override
              public void onError(Throwable e) {

              }

              @Override
              public void onComplete() {
//                  options.isKeepSampling=false;
//                  options.width=100;
//                  options.height=200;
//                  options.quality=100;
                  Tiny.getInstance().source(filePath).asFile().withOptions(options).compress(new FileCallback() {

                      @Override
                      public void callback(boolean isSuccess, String outfile) {
                          if (isSuccess){
                              upFile(0,outfile,0f,"jpg");
                          }else{
                              upFile(0,filePath,0f,"jpg");
                          }
                      }
                  });

              }
          });
    }

    /**
     * 发送文字
     */
    private void senMeg(){
        String send = et_message_send.getText().toString().trim();
        mapdata.clear();
        mapdata.put("sessionType","2");

        senMessage(send,mapdata);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if(captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();
                        // 照片地址
                        String imagePaht = captureManager.getCurrentPhotoPath();
                        //请求接口进行发送图片
                        sendImage(imagePaht,bitmapUtils.getImage(imagePaht)[0],bitmapUtils.getImage(imagePaht)[1]);
                    }
                    break;
                case PhotoPicker.REQUEST_CODE:
                    if (data != null) {
                        List<String> imagePaht= data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        if (imagePaht.size()>0) {
                            sendImage(imagePaht.get(0),bitmapUtils.getImage(imagePaht.get(0))[0],bitmapUtils.getImage(imagePaht.get(0))[1]);
                        }
                    }
                    break;
            }
        }
    }

    protected void requestData() {
        String baseUrl= BaseApi.advisoryUrl;
        String url=new StringBuffer(baseUrl)
                .append(APIUrlConfig.BaseConsultUrl)
                .append("session/list/pda?consultId=")
                .append(consultId).toString();
        try {
            OkhttpUtils.getInstance().doGet(url, new OkhttpUtils.MyCallback() {
                @Override
                public void onSuccess(String body) {
                    ChatMessageListBean chatMessageListBean= gson.fromJson(body,ChatMessageListBean.class);
                    tblist.addAll(chatMessageListBean.getData());
                    for (int i=0;i<tblist.size();i++){
                        if (tblist.get(i).getSessionType()==3&&tblist.get(i).getPersonType()!=1) {
                            imageList.add(GlideUtils.getImagePath(tblist.get(i).getPaths(), "jpg"));
                            imagePosition.put(i,imageList.size()-1);
                        }
                    }
                    recycle_message.getRecyclerView().smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                    chatRecyclerAdapter.notifyItemInserted(tblist.size() - 1);
                }

                @Override
                public void onFailture(String e) {
                    StringUtil.showToast(e);
                }

                @Override
                public void onStart() {
                    recycle_message.setRefreshing(true);
                }

                @Override
                public void onFinish() {
                    recycle_message.setPullLoadMoreCompleted();
                }
            });
        } catch (NetException e) {
            e.printStackTrace();
        }
    }
    public ChatMessageBean getTbubImage(int sessionType,String content,String imageUrl,int sendType,
                                        Float userVoiceTime, int w,int h) {
        ChatMessageBean tbub = new ChatMessageBean();
        String time = returnTime();
        tbub.setGroupTime(time);
        tbub.setVoiceDuration(userVoiceTime);
        tbub.setPersonType(2);
        tbub.setSessionType(sessionType);
        tbub.setWidth(w);
        tbub.setHeight(h);
        tbub.setSendType(sendType);
        tbub.setContent(content);
        tbub.setPicPath(imageUrl);
        tbub.setVedioPath(imageUrl);
        return tbub;
    }
    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    /**
     * 发送内容
     */
    private void senMessage(String sendText, Map<String, String> map){
        map.put("sessionStatus","1");
        map.put("readTag","0");
        map.put("consultId",consultId);
        map.put("personType","2");
        map.put("personId",application.user.YHID);
        map.put("personName",application.user.YHXM);
        mapdata.put("content",sendText);
        //请求接口
        String baseUrl= BaseApi.advisoryUrl;
        String url=new StringBuffer(baseUrl)
                .append(APIUrlConfig.BaseConsultUrl)
                .append("session/add").toString();

        try {
            OkhttpUtils.getInstance().doPost(url, map, new OkhttpUtils.MyCallback() {
                @Override
                public void onSuccess(String body) {
                    if (!TextUtils.isEmpty(sendText)) {
                        //如果发送的是文字
                        et_message_send.setText("");
                        tblist.add(getTbubImage(ChatRecyclerAdapter.TO_USER_MSG,sendText,null,ChatConst.COMPLETED,0f,0,0));
                        recycle_message.getRecyclerView().smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                        chatRecyclerAdapter.notifyItemInserted(tblist.size() - 1);
                    }else{
                        tblist.get(tblist.size() - 1).setSendType(ChatConst.COMPLETED);
                        chatRecyclerAdapter.notifyItemRangeChanged(tblist.size()-1,1);
                    }

                }

                @Override
                public void onFailture(String e) {
                    tblist.get(tblist.size() - 1).setSendType(ChatConst.SENDERROR);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        } catch (NetException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
    }
    /**
     * 上传文件
     */
    private void upFile(int type,String filePath,float seconds,String extName){

        OkhttpUtils.getInstance().upLoad(filePath,extName, new OkhttpUtils.MyCallback() {

            private String path;

            @Override
            public void onSuccess(String body) {
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    path = jsonObject.optString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mapdata.clear();


                mapdata.put("paths",path);
                if (type==0) {
                    //上传图片完成
                    mapdata.put("sessionType","3");
                }else{
                    mapdata.put("sessionType","4");
                    mapdata.put("voiceDuration",seconds);
                    //上传语音完成
                }
                senMessage(null,mapdata);
            }

            @Override
            public void onFailture(String e) {
                //上传失败刷新
                tblist.get(tblist.size() - 1).setSendType(ChatConst.SENDERROR);
                chatRecyclerAdapter.notifyItemRangeChanged(tblist.size()-1,1);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }
        });
    }
    private void requestRead(){
        String baseUrl= BaseApi.advisoryUrl;
        String url=new StringBuffer(baseUrl)
                .append(APIUrlConfig.BaseConsultUrl)
                .append("nurse/read").toString();
        mapdata.clear();
        mapdata.put("consultId",consultId);
        try {
            OkhttpUtils.doPost(url, mapdata, new OkhttpUtils.MyCallback() {
                @Override
                public void onSuccess(String body) { }

                @Override
                public void onFailture(String e) { }

                @Override
                public void onStart() { }

                @Override
                public void onFinish() { }
            });
        } catch (NetException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
    }
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom- getSoftButtonsBarHeight() != 0;
    }
    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    @Override
    protected void onDestroy() {
        tblist.clear();
        chatRecyclerAdapter.notifyDataSetChanged();
        recycle_message.setAdapter(null);
        tblist = null;
        //adapter图片数据
        imageList = null;
        //图片下标位置
        chatRecyclerAdapter.unReadPosition=null;
        MediaManager.pause();
        MediaManager.release();
        chatRecyclerAdapter=null;
        recycle_message=null;
        super.onDestroy();
    }
}
