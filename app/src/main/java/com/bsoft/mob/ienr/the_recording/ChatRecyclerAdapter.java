package com.bsoft.mob.ienr.the_recording;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.GlideUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mao Jiqing on 2016/9/29.
 */
public class ChatRecyclerAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ChatMessageBean> userList = new ArrayList<ChatMessageBean>();
    public static final int FROM_USER_MSG = 1;//接收消息类型
    public static final int TO_USER_MSG = 2;//发送消息类型
    public static final int TO_USER_IMG = 3;//发送消息类型
    public static final int TO_USER_VOICE = 4;//发送消息类型
    private int mMinItemWith;// 设置对话框的最大宽度和最小宽度
    private int mMaxItemWith;
    private Animation an;
    private SendErrorListener sendErrorListener;
    private VoiceIsRead voiceIsRead;
    public List<String> unReadPosition = new ArrayList<String>();
    private int voicePlayPosition = -1;
    private LayoutInflater mLayoutInflater;
    public boolean isPicRefresh = true;
    private String imageUrlSrc;

    public interface SendErrorListener {
        public void onClick(int position);
    }

    public void setSendErrorListener(SendErrorListener sendErrorListener) {
        this.sendErrorListener = sendErrorListener;
    }

    public interface VoiceIsRead {
        public void voiceOnClick(int position);
    }

    public void setVoiceIsReadListener(VoiceIsRead voiceIsRead) {
        this.voiceIsRead = voiceIsRead;
    }

    public ChatRecyclerAdapter(Context context, List<ChatMessageBean> userList) {
        this.context = context;
        this.userList = userList;
        mLayoutInflater = LayoutInflater.from(context);
        // 获取系统宽度
        WindowManager wManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWith = (int) (outMetrics.widthPixels * 0.5f);
        mMinItemWith = (int) (outMetrics.widthPixels * 0.15f);
    }

    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case FROM_USER_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msgfrom_list_item, parent, false);
                holder = new FromUserMsgViewHolder(view);
                break;
            case TO_USER_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_msgto_list_item, parent, false);
                holder = new ToUserMsgViewHolder(view);
                break;
            case TO_USER_IMG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imageto_list_item, parent, false);
                holder = new ToUserImgViewHolder(view);
                break;
            case TO_USER_VOICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_voiceto_list_item, parent, false);
                holder = new ToUserVoiceViewHolder(view);
                break;
        }
        return holder;
    }

    class FromUserMsgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private ImageView iv_from_1;
        private ImageView iv_from_2;
        private ImageView iv_from_3;
        private ImageView iv_from_4;
        private TextView chat_time;
        private TextView content;
        private TextView tv_name;
        private LinearLayout ll_list;

        public FromUserMsgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.tb_other_user_icon);
            chat_time = (TextView) view.findViewById(R.id.chat_time);
            tv_name =view.findViewById(R.id.tv_name);
            content = view.findViewById(R.id.content);
            iv_from_1 = view.findViewById(R.id.iv_from_1);
            iv_from_2 = view.findViewById(R.id.iv_from_2);
            iv_from_3 = view.findViewById(R.id.iv_from_3);
            iv_from_4 = view.findViewById(R.id.iv_from_4);
            ll_list = view.findViewById(R.id.ll_list);
        }
    }

    class ToUserMsgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private TextView content;
        private ImageView sendFailImg;

        public ToUserMsgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.mychat_time);
            content = (TextView) view
                    .findViewById(R.id.mycontent);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.mysend_fail_img);
        }
    }

    class ToUserImgViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private LinearLayout image_group;
        private ImageView image_Msg;
        private ImageView sendFailImg;

        public ToUserImgViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.mychat_time);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.mysend_fail_img);
            image_group = (LinearLayout) view
                    .findViewById(R.id.image_group);
            image_Msg = (ImageView) view
                    .findViewById(R.id.image_message);
        }
    }

    class ToUserVoiceViewHolder extends RecyclerView.ViewHolder {
        private ImageView headicon;
        private TextView chat_time;
        private LinearLayout voice_group;
        private TextView voice_time;
        private FrameLayout voice_image;
        private View receiver_voice_unread;
        private View voice_anim;
        private ImageView sendFailImg;

        public ToUserVoiceViewHolder(View view) {
            super(view);
            headicon = (ImageView) view
                    .findViewById(R.id.tb_my_user_icon);
            chat_time = (TextView) view
                    .findViewById(R.id.mychat_time);
            voice_group = (LinearLayout) view
                    .findViewById(R.id.voice_group);
            voice_time = (TextView) view
                    .findViewById(R.id.voice_time);
            voice_image = (FrameLayout) view
                    .findViewById(R.id.voice_image);
            voice_anim = (View) view
                    .findViewById(R.id.id_recorder_anim);
            sendFailImg = (ImageView) view
                    .findViewById(R.id.mysend_fail_img);
        }
    }

    /**
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageBean tbub = userList.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case FROM_USER_MSG:
                fromMsgUserLayout((FromUserMsgViewHolder) holder, tbub, position);
                break;
            case TO_USER_MSG:
                toMsgUserLayout((ToUserMsgViewHolder) holder, tbub, position);
                break;
            case TO_USER_IMG:
                toImgUserLayout((ToUserImgViewHolder) holder, tbub, position);
                break;
            case TO_USER_VOICE:
                toVoiceUserLayout((ToUserVoiceViewHolder) holder, tbub, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (userList.get(position).getPersonType()==1){
            return FROM_USER_MSG;
        }else{
            return userList.get(position).getSessionType();
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void fromMsgUserLayout(final FromUserMsgViewHolder holder, final ChatMessageBean tbub, final int position) {
        if (position != 0) {
            String showTime = tbub.getGroupTime();
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = tbub.getGroupTime();
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }
        if (!TextUtils.isEmpty(tbub.getContent())){
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(tbub.getContent());
        }else{
            holder.content.setVisibility(View.GONE);
        }

        holder.tv_name.setText(tbub.getPersonName());
        if (tbub.getPathList().size()>0){
            holder.ll_list.setVisibility(View.VISIBLE);

            for (int i=0;i<tbub.getPathList().size();i++){
               String imageUrl= GlideUtils.getImagePath(tbub.getPathList().get(i),"jpg");
                if (i==0){
                    GlideUtils.filletImage(imageUrl,holder.iv_from_1);
                }else if (i==1){
                    GlideUtils.filletImage(imageUrl,holder.iv_from_2);
                }else if (i==2){
                    GlideUtils.filletImage(imageUrl,holder.iv_from_3);
                }else if (i==3){
                    GlideUtils.filletImage(imageUrl,holder.iv_from_4);
                }
            }
        }else{
            holder.ll_list.setVisibility(View.GONE);
        }
    }



    private void toMsgUserLayout(final ToUserMsgViewHolder holder, final ChatMessageBean tbub, final int position) {
//        holder.headicon.setBackgroundResource(R.mipmap.grzx_tx_s);
//
        GlideUtils.getGlideUtils().roundnessLoadImageView(R.mipmap.doctor_icon,holder.headicon);
//        /* time */        holder.headicon.setImageDrawable(context.getResources()
//                .getDrawable(R.mipmap.doctor_icon));
        if (position != 0) {
            String showTime =tbub.getGroupTime();
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = tbub.getGroupTime();
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }

        holder.content.setVisibility(View.VISIBLE);
        holder.content.setText(tbub.getContent());
        holder.itemView.setOnLongClickListener(v -> {
            mOnItemClickLitener.onLongItemClick(v,position);
            return true;
        });
    }

    private void toImgUserLayout(final ToUserImgViewHolder holder, final ChatMessageBean tbub, final int position) {
        GlideUtils.getGlideUtils().roundnessLoadImageView(R.mipmap.doctor_icon,holder.headicon);
        switch (tbub.getSendType()) {
            case ChatConst.SENDING:
                an = AnimationUtils.loadAnimation(context,
                        R.anim.update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                break;

            case ChatConst.COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case ChatConst.SENDERROR:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        if (sendErrorListener != null) {
                            sendErrorListener.onClick(position);
                        }
                    }

                });
                break;
            default:
                break;
        }
//        holder.headicon.setImageDrawable(context.getResources()
//                .getDrawable(R.mipmap.grzx_tx_s));

        /* time */
        if (position != 0) {
            String showTime = tbub.getGroupTime();
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            String showTime = tbub.getGroupTime();
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(showTime);
        }

//        if (isPicRefresh) {
            holder.image_group.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(tbub.getPicPath())) {
                imageUrlSrc = GlideUtils.getImagePath(tbub.getPaths(),"jpg");
            }else{
                imageUrlSrc=tbub.getPicPath();
            }

        GlideUtils.ratioImage(imageUrlSrc,holder.image_Msg,tbub.getWidth(),tbub.getHeight());
        holder.image_Msg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // TODO Auto-generated method stub
                    stopPlayVoice();
                    mOnItemClickLitener.onItemClick(view,position);
                }

            });
        holder.image_Msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickLitener.onLongItemClick(v,position);
                return true;
            }
        });
//        }
    }

    private void toVoiceUserLayout(final ToUserVoiceViewHolder holder, final ChatMessageBean tbub, final int position) {
        GlideUtils.getGlideUtils().roundnessLoadImageView(R.mipmap.doctor_icon,holder.headicon);
        switch (tbub.getSendType()) {
            case ChatConst.SENDING:
                an = AnimationUtils.loadAnimation(context,
                        R.anim.update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                break;

            case ChatConst.COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case ChatConst.SENDERROR:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg
                        .setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Auto-generated method stub
                        if (sendErrorListener != null) {
                            sendErrorListener.onClick(position);
                        }
                    }

                });
                break;
            default:
                break;
        }

        /* time */
        if (position != 0) {
            String showTime =tbub.getGroupTime();
            if (showTime != null) {
                holder.chat_time.setVisibility(View.VISIBLE);
                holder.chat_time.setText(showTime);
            } else {
                holder.chat_time.setVisibility(View.GONE);
            }
        } else {
            holder.chat_time.setVisibility(View.VISIBLE);
            holder.chat_time.setText(tbub.getGroupTime());
        }
        holder.voice_group.setVisibility(View.VISIBLE);
        if (holder.receiver_voice_unread != null)
            holder.receiver_voice_unread.setVisibility(View.GONE);
        if (holder.receiver_voice_unread != null && unReadPosition != null) {
            for (String unRead : unReadPosition) {
                if (unRead.equals(position + "")) {
                    holder.receiver_voice_unread
                            .setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
        AnimationDrawable drawable;
        holder.voice_anim.setId(position);
        if (position == voicePlayPosition) {
            holder.voice_anim.setBackgroundResource(R.mipmap.adj);
            holder.voice_anim
                    .setBackgroundResource(R.drawable.voice_play_send);
            drawable = (AnimationDrawable) holder.voice_anim
                    .getBackground();
            drawable.start();
        } else {
            holder.voice_anim.setBackgroundResource(R.mipmap.adj);
        }
        holder.voice_group.setOnClickListener(new View.OnClickListener() {

            private String voicePath;

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.receiver_voice_unread != null)
                    holder.receiver_voice_unread.setVisibility(View.GONE);
                holder.voice_anim.setBackgroundResource(R.mipmap.adj);
                stopPlayVoice();
                voicePlayPosition = holder.voice_anim.getId();
                AnimationDrawable drawable;
                holder.voice_anim
                        .setBackgroundResource(R.drawable.voice_play_send);
                drawable = (AnimationDrawable) holder.voice_anim
                        .getBackground();
                drawable.start();
                if (!TextUtils.isEmpty(tbub.getVedioPath())){
                    voicePath = tbub.getVedioPath();
                    File file = new File(voicePath);
                    if (!(!voicePath.equals("") && FileSaveUtil
                            .isFileExists(file))) {
                        voicePath = tbub.getVedioPath() == null ? ""
                                : tbub.getPaths();
                    }
                }else{
                    voicePath = GlideUtils.getImagePath(tbub.getPaths(),"mp3");
                }

                if (voiceIsRead != null) {
                    voiceIsRead.voiceOnClick(position);
                }
                MediaManager.playSound(voicePath, new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                voicePlayPosition = -1;
                                holder.voice_anim
                                        .setBackgroundResource(R.mipmap.adj);
                            }
                        });
            }

        });
        holder.voice_group.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickLitener.onLongItemClick(v,position);
                return true;
            }
        });
        float voiceTime = tbub.getVoiceDuration();
        BigDecimal b = new BigDecimal(voiceTime);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        holder.voice_time.setText(f1 + "\"");
        ViewGroup.LayoutParams lParams = holder.voice_image
                .getLayoutParams();
        lParams.width = (int) (mMinItemWith + mMaxItemWith / 60f
                * tbub.getVoiceDuration());
        holder.voice_image.setLayoutParams(lParams);
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(String time) {
        String showDay = null;
        String nowTime = returnTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(nowTime);
            java.util.Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            if (day >= 365) {
                showDay = time.substring(0, 10);
            } else if (day >= 1 && day < 365) {
                showDay = time.substring(5, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showDay;
    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            ByteArrayOutputStream out;
            FileInputStream fis = new FileInputStream(url);
            BufferedInputStream bis = new BufferedInputStream(fis);
            out = new ByteArrayOutputStream();
            @SuppressWarnings("unused")
            int hasRead = 0;
            byte[] buffer = new byte[1024 * 2];
            while ((hasRead = bis.read(buffer)) > 0) {
                // 读出多少数据，向输出流中写入多少
                out.write(buffer);
                out.flush();
            }
            out.close();
            fis.close();
            bis.close();
            byte[] data = out.toByteArray();
            // 长宽减半
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 3;
            return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public void stopPlayVoice() {
        if (voicePlayPosition != -1) {
            View voicePlay = (View) ((Activity) context)
                    .findViewById(voicePlayPosition);
            if (voicePlay != null) {
              voicePlay.setBackgroundResource(R.mipmap.adj);
            }
            MediaManager.pause();
            voicePlayPosition = -1;
        }
    }
    // 设置点击事件的接口，利用接口回调，来完成点击事件
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onLongItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
