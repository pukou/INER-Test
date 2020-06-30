package com.bsoft.mob.ienr.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advisory_list.PatientMessageBean;
import com.bsoft.mob.ienr.util.GlideUtils;

import java.util.List;

/**
 * 咨询列表
 */
public class AdvisoryListAdapter extends RecyclerView.Adapter<AdvisoryListAdapter.ViewHolder> {
    public AdvisoryListAdapter() {
    }

    private List<PatientMessageBean> patientMessageBeanList;

    public void setData(List<PatientMessageBean> patientMessageBeanList) {
        this.patientMessageBeanList = patientMessageBeanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(AppApplication.getContext()).inflate(R.layout.item_patient_message, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientMessageBean data=patientMessageBeanList.get(position);
//        GlideUtils.getGlideUtils().roundnessLoadImageView(data.getIamge(), holder.iv_head);
        holder.tv_advisory_time.setText(data.getConsultTime());
        holder.tv_name.setText(data.getPatientName());
        holder.tv_reply_content.setText(data.getConsultContent());
        //判断状态显示回复
        if (data.getMsgCount()>0){
            holder.tv_geshu.setText(String.valueOf(data.getMsgCount()));
            holder.tv_geshu.setVisibility(View.VISIBLE);
        }else{
            holder.tv_geshu.setVisibility(View.GONE);
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                mOnItemClickLitener.onItemClick(v,position);
                }
                }
        );
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickLitener.onLongClick(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return patientMessageBeanList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public ImageView iv_head;
        public TextView tv_name;
        public TextView tv_advisory_time;
        public TextView tv_reply_content;
        public TextView tv_geshu;
        public ViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.iv_head = (ImageView) rootView.findViewById(R.id.iv_head);
            this.tv_name = (TextView) rootView.findViewById(R.id.tv_name);
            this.tv_advisory_time = (TextView) rootView.findViewById(R.id.tv_advisory_time);
            this.tv_reply_content = (TextView) rootView.findViewById(R.id.tv_reply_content);
            this.tv_geshu = (TextView) rootView.findViewById(R.id.tv_geshu);
        }
    }
    // 设置点击事件的接口，利用接口回调，来完成点击事件
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onLongClick(int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
