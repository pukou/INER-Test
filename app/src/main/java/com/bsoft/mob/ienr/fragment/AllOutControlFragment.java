package com.bsoft.mob.ienr.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.api.OutControlApi;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.RecyclerViewHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.classichu.adapter.recyclerview.ClassicRVHeaderFooterAdapter;
import com.classichu.adapter.recyclerview.ClassicRVHeaderFooterViewHolder;

import java.util.ArrayList;
import java.util.List;

/*升级编号【56010038】============================================= start
                外出管理PDA上只有登记功能，查询需要找到具体的人再查询，不太方便，最好能有一个查询整个病区外出病人的列表
            ================= classichu 2018/3/7 19:49
            */
public class AllOutControlFragment extends LeftMenuItemFragment {
    private RecyclerView mRecyclerView;
    private OutControlRVAdapter mOutControlRVAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public AllOutControlFragment() {
        // Required empty public constructor
    }


    public static AllOutControlFragment newInstance(String param1, String param2) {
        AllOutControlFragment fragment = new AllOutControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_all_out_control;
    }

    private String nowZyh;

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {

        actionBar.setTitle("外出管理");
        //
        mRecyclerView = (RecyclerView) rootLayout.findViewById(R.id.id_rv);
        RecyclerViewHelper.init(mRecyclerView);
        mOutControlRVAdapter = new OutControlRVAdapter(mContext, R.layout.item_list_text_three_secondary);
        mOutControlRVAdapter.setOnItemClickListener(new ClassicRVHeaderFooterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                OutControl outControl = mOutControlRVAdapter.getData(position);
                nowZyh = outControl.ZYH;
                //
                actionGetDetailData();
            }
        });
        //
        mRecyclerView.setAdapter(mOutControlRVAdapter);
        mOutControlRVAdapter.setEmptyView(EmptyViewHelper.buildEmptyView(mContext));
        //
        toRefreshData();

    }

    @Override
    protected void toRefreshData() {
        super.toRefreshData();
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }

    class GetDataTask extends
            AsyncTask<Void, Void, Response<List<OutControl>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<OutControl>> doInBackground(Void... params) {
            int sysType = Constant.sysType;
            return OutControlApi
                    .getInstance(mContext)
                    .GetAllOutPatients(application.getAreaId(), application.jgId, sysType);
        }


        @Override
        protected void onPostExecute(Response<List<OutControl>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(mContext, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    toRefreshData();
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {
                    List<OutControl> mList = result.Data;
                    if (mList == null) {
                        mList = new ArrayList<>();
                        //
                        TestDataHelper.buidTestData(OutControl.class, mList);
                    }
                    mOutControlRVAdapter.setEmptyViewVisibility();
                    mOutControlRVAdapter.refreshDataList(mList);
                } else {
                    showMsgAndVoiceAndVibrator("请求失败：" + result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    protected void actionGetDetailData() {
        GetDetialDataTask task = new GetDetialDataTask();
        tasks.add(task);
        task.execute();
    }

    class GetDetialDataTask extends
            AsyncTask<String, Void, Response<SickPersonVo>> {


        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.doing);
        }

        @Override
        protected Response<SickPersonVo> doInBackground(String... params) {

            String zyh = nowZyh;
            return PatientApi
                    .getInstance(mContext)
                    .getPatientForHand(zyh, application.jgId);
        }


        @Override
        protected void onPostExecute(Response<SickPersonVo> result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(mContext, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    actionGetDetailData();
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {
                    SickPersonVo sickPersonVo = result.Data;
                    if (sickPersonVo != null) {
                        //赋值新的病人
                        mAppApplication.sickPersonVo = sickPersonVo;
                        //跳转用户界面
                        Intent intent = new Intent(getActivity(), UserModelActivity.class);
                        intent.putExtra("outcontrol", true);
                        getActivity().startActivity(intent);
                    }
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    public class OutControlRVAdapter extends ClassicRVHeaderFooterAdapter<OutControl> {


        public OutControlRVAdapter(Context mContext, int mItemLayoutId) {
            super(mContext, mItemLayoutId);
        }

        @Override
        public RVHeaderFooterAdapterDelegate setupDelegate() {
            return null;
        }

        @Override
        public void findBindView(int pos, ClassicRVHeaderFooterViewHolder viewHolder) {

            TextView name = viewHolder.findBindItemView(R.id.id_tv_one);
            TextView brch = viewHolder.findBindItemView(R.id.id_tv_two);
            TextView time = viewHolder.findBindItemView(R.id.id_tv_three);

            name.setText(mDataList.get(pos).BRCH);
            brch.setText(mDataList.get(pos).BRXM);
            time.setText("外出登记时间:" + mDataList.get(pos).WCDJSJ);

        }
    }

}
/* =============================================================== end */
