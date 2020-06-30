package com.bsoft.mob.ienr.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.AdvisoryDetailsActivity;
import com.bsoft.mob.ienr.adapter.AdvisoryListAdapter;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.AdviceCheckApi;
import com.bsoft.mob.ienr.api.BaseApi;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advicecheck.CheckDetail;
import com.bsoft.mob.ienr.model.advisory_list.PatieMessageList;
import com.bsoft.mob.ienr.model.advisory_list.PatientMessageBean;
import com.bsoft.mob.ienr.the_recording.MyLinearLayoutManager;
import com.bsoft.mob.ienr.util.NetException;
import com.bsoft.mob.ienr.util.OkhttpUtils;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.view.AlertBox;
import com.google.gson.Gson;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 咨询列表
 */
public class AdvisoryListFragment extends BaseUserFragment {


    private PullLoadMoreRecyclerView recycleview_advisory;
    private AdvisoryListAdapter advisoryListAdapter;
    private List<PatientMessageBean> patientMessageBeanList;
    private Gson gson;
    private PatieMessageList patieMessageList;
    private Map mapdata;
    private TextView tv_not_data;

    public AdvisoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_advisory_list;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        recycleview_advisory=rootLayout.findViewById(R.id.recycleview_advisory);
        recycleview_advisory.getRecyclerView().setLayoutManager(new MyLinearLayoutManager(getActivity()));
        tv_not_data = rootLayout.findViewById(R.id.tv_not_data);
        actionBar.setTitle("资讯列表");
        initData();
        initClick();
        if (gson==null){
            gson = new Gson();
        }
    }
    private void initData(){
        mapdata = new HashMap();
        patientMessageBeanList = new ArrayList<>();
        //请求聊天列表
        advisoryListAdapter = new AdvisoryListAdapter();
        advisoryListAdapter.setData(patientMessageBeanList);
        recycleview_advisory.setAdapter(advisoryListAdapter);
        recycleview_advisory.setPushRefreshEnable(false);
        //请求接口
            requestData(mAppApplication.sickPersonVo.ZYH);
    }
    private void initClick(){
        advisoryListAdapter.setOnItemClickLitener(new AdvisoryListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if(patieMessageList!=null) {
                    Intent intent = new Intent(AppApplication.getContext(), AdvisoryDetailsActivity.class);
                    intent.putExtra("consultId", patieMessageList.getData().get(position).getConsultId());
                    if ( patieMessageList.getData().get(position).getMsgCount()>0){
                        intent.putExtra("isRead",true);
                        patieMessageList.getData().get(position).setMsgCount(0);
                        advisoryListAdapter.notifyItemRangeChanged(position,1);
                    }else{
                        intent.putExtra("isRead",false);
                    }

                    startActivity(intent);


                }
            }

            @Override
            public void onLongClick(int position) {
                AlertBox.Show(getActivity(), "是否删除会话", null, "删除", "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //执行删除操作
                                dialog.dismiss();
                                mapdata.clear();
                                mapdata.put("consultId",patieMessageList.getData().get(position).getConsultId());
                                String baseUrl= BaseApi.advisoryUrl;
                                String url=new StringBuffer(baseUrl)
                                        .append(APIUrlConfig.BaseConsultUrl)
                                        .append("delete").toString();
                                try {
                                    OkhttpUtils.getInstance().doPost(url, mapdata, new OkhttpUtils.MyCallback() {
                                        @Override
                                        public void onSuccess(String body) {
                                            patieMessageList.getData().remove(position);
                                            advisoryListAdapter.notifyItemRangeRemoved(position,1);
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
        recycleview_advisory.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                requestData("181");

            }

            @Override
            public void onLoadMore() {
                recycleview_advisory.setPullLoadMoreCompleted();
            }
        });
    }
    private void requestData(String patientId){
       String jgId= mAppApplication.jgId;
       String baseUrl= BaseApi.advisoryUrl;
       String url=new StringBuffer(baseUrl)
               .append(APIUrlConfig.BaseConsultUrl)
               .append("list/nurse?orgCode=")
               .append(jgId)
               .append("&patientId=")
               .append(patientId).toString();
        try {
            OkhttpUtils.getInstance().doGet(url, new OkhttpUtils.MyCallback() {
                @Override
                public void onSuccess(String response) {
                    patieMessageList = gson.fromJson(response,PatieMessageList.class);
                    if (patieMessageList.getData().size()>0) {
                        advisoryListAdapter.setData(patieMessageList.getData());
                        advisoryListAdapter.notifyDataSetChanged();
                        tv_not_data.setVisibility(View.GONE);
                    }else{
                        tv_not_data.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailture(String e) {
                    StringUtil.showToast(e);
                }

                @Override
                public void onStart() {
                    recycleview_advisory.setRefreshing(true);
                }

                @Override
                public void onFinish() {
                    recycleview_advisory.setPullLoadMoreCompleted();
                }
            });
        } catch (NetException e) {
            e.printStackTrace();
        }
    }
}
