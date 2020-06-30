package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NurseFocusActivity;
import com.bsoft.mob.ienr.activity.user.NursePlanActivity;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.evaluate.PGLX_JD_GL_FXPG_Bean;
import com.bsoft.mob.ienr.model.nurseplan.CSMS_DataWrapper;
import com.bsoft.mob.ienr.model.nurseplan.FXPG_InfoDataWrapper;
import com.bsoft.mob.ienr.model.nurseplan.FocusRelevanceGroupBean;
import com.bsoft.mob.ienr.model.nurseplan.ZDMS_DataWrapper;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Classichu on 2017/5/4.
 */
public class NurseFocusRelevanceFragment extends BaseUserFragment {
    // 眉栏工具条

    // 下拉刷新的分组列表
    private ExpandableListView refreshView;
    private List<FocusRelevanceGroupBean> mList;
    private FocusRelevanceGroupChildBeanListAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCast();
    }

    private static final int[] ITEM_DRAWABLES = {R.drawable.menu_reset, R.drawable.menu_fresh};
    private static final int[][] itemStringDrawables = {
            {R.drawable.menu_reset, R.string.comm_menu_reset},
            {R.drawable.menu_fresh, R.string.comm_menu_refresh}};
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_nurse_focus_relevance;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initView(rootLayout);
        initActionBar();
        initRefreshView();

        toRefreshData();
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : ITEM_DRAWABLES) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onItemSelected(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId=itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid,text) {
                @Override
                public void actionClick(View view, int resid) {
                    onItemSelected(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }


    private void onItemSelected(int resid) {
        if (resid == R.drawable.menu_reset) {
            getEditedData();//add
        } else if (resid == R.drawable.menu_fresh) {
            toRefreshData();
        }
    }

    @Override
    protected void toRefreshData() {
        getData();
    }

    /**
     * @param @param view 设定文件
     * @return void 返回类型
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView(View view) {
        refreshView = (ExpandableListView) view
                .findViewById(R.id.id_elv);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("护理焦点关联");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.BRCH) ? "" : mAppApplication.sickPersonVo.BRCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initRefreshView
     * @Description: 初始化下拉刷新列表
     */
    private void initRefreshView() {

        refreshView.setOnChildClickListener(
                new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent,
                                                View v, int groupPosition, int childPosition,
                                                long id) {

                      /*  Intent intent = new Intent(getActivity(),
                                NurseFocusActivity.class);
                        intent.putExtra("WTXH",
                                mList.get(groupPosition).SimpleRecord
                                        .get(childPosition).XH);
                        intent.putExtra("GLLX", mList.get(groupPosition).GLLX);
                        intent.putExtra("GLXH", mList.get(groupPosition).XH);
                        if (mList.get(groupPosition).SimpleRecord
                                .get(childPosition).UMBER.equals("0"))
                            intent.putExtra("ISADD", true);
                        else
                            intent.putExtra("ISADD", false);
                        startActivityForResult(intent,
                                NurseFocusActivity.REQUEST_CODE);
*/
                        return true;
                    }
                });
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initBroadCast
     * @Description: 初始化条码处理广播接收器
     */
    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                }
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NursePlanActivity.REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            toRefreshData();
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getData
     * @Description: 获取计划问题列表
     */
    private void getData() {
        isQueryEdited = false;
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    private void getEditedData() {
        isQueryEdited = true;
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    private boolean isQueryEdited = true;

    /**
     * 网络请求并处理获取的数据
     *
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: getDataTast
     * @Description: 网络请求并处理获取的数据
     * @date 2015-11-19 上午11:35:46
     */
    class GetDataTast extends AsyncTask<Void, Void, Response<List<FocusRelevanceGroupBean>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<FocusRelevanceGroupBean>> doInBackground(Void... params) {
            return NursePlanApi.getInstance(getActivity())
                    .getFocusRelevanceGroupList(mAppApplication.sickPersonVo.ZYH,
                            mAppApplication.getAreaId(),
                            mAppApplication.jgId, isQueryEdited);
        }

        @Override
        protected void onPostExecute(Response<List<FocusRelevanceGroupBean>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    toRefreshData();
                                }
                            }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    mList = result.Data;

                    mAdapter = new FocusRelevanceGroupChildBeanListAdapter(getActivity(), mList);
                    refreshView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            TextView nursefrom_name = (TextView) v.findViewById(R.id.id_tv);
                            nursefrom_name.setSelected(!nursefrom_name.isSelected());
                            return false;
                        }
                    });
                    refreshView.setAdapter(mAdapter);
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    class FocusRelevanceGroupChildBeanListAdapter extends BaseExpandableListAdapter {
        Context mContext;
        List<FocusRelevanceGroupBean> mList;

        public FocusRelevanceGroupChildBeanListAdapter(Context mContext, List<FocusRelevanceGroupBean> list) {
            super();
            this.mContext = mContext;
            this.mList = list;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (mList.get(groupPosition).PZBH) {
                case "1":
                    return mList.get(groupPosition).FXPGJLBeanList.get(childPosition);
                case "2":
                    return mList.get(groupPosition).HLJHJLBeanList.get(childPosition);
                case "3":
                    return mList.get(groupPosition).JD_GL_SMTZ_BeanList.get(childPosition);
                case "4":
                    return mList.get(groupPosition).JYXM_PATIENTINFO_BeanList.get(childPosition);
            }
            return null;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            switch (mList.get(groupPosition).PZBH) {
                case "1":
                    return Long.parseLong(mList.get(groupPosition).FXPGJLBeanList
                            .get(childPosition).PGXH);
                case "2":
                    return Long.parseLong(mList.get(groupPosition).HLJHJLBeanList
                            .get(childPosition).JLWT);
                case "3":
                    return mList.get(groupPosition).JD_GL_SMTZ_BeanList
                            .get(childPosition).CJH;
                case "4":
                    return Long.parseLong(mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                            .get(childPosition).XMID);

            }
            return 0;
        }

        @Override
        public View getChildView(final int groupPosition,
                                 final int childPosition,
                                 boolean isLastChild, View convertView,
                                 ViewGroup parent) {
            ChildHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_one_secondary_icon, parent, false);
                vHolder = new ChildHolder();
                vHolder.tv_itemname = (TextView) convertView
                        .findViewById(R.id.id_tv);
                vHolder.id_tv_more = (TextView) convertView
                        .findViewById(R.id.id_tv_more);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ChildHolder) convertView.getTag();
            }
            switch (mList.get(groupPosition).PZBH) {
                case "1":
                    vHolder.tv_itemname.setText(mList.get(groupPosition).FXPGJLBeanList
                            .get(childPosition).PGDMC);
                    String pgsj = mList.get(groupPosition).FXPGJLBeanList
                            .get(childPosition).PGSJ;
                    vHolder.id_tv_more.setText(DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(pgsj)));
                    break;
                case "2":
                    vHolder.tv_itemname.setText(mList.get(groupPosition).HLJHJLBeanList
                            .get(childPosition).WTMS);
                    String cjsj = mList.get(groupPosition).HLJHJLBeanList
                            .get(childPosition).KSSJ;
                    vHolder.id_tv_more.setText(DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(cjsj)));
                    break;
                case "3":
                    vHolder.tv_itemname.setText(mList.get(groupPosition).JD_GL_SMTZ_BeanList
                            .get(childPosition).XMMC + mList.get(groupPosition).JD_GL_SMTZ_BeanList
                            .get(childPosition).TZNR);
                    String caijishijian = mList.get(groupPosition).JD_GL_SMTZ_BeanList
                            .get(childPosition).CJSJ;

                    vHolder.id_tv_more.setText(DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(caijishijian)));

                    break;
                case "4":
                    vHolder.tv_itemname.setText(mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                            .get(childPosition).XMMC);
                    String shsj = mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                            .get(childPosition).SHSJ;

                    vHolder.id_tv_more.setText(DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(shsj)));

                    break;
            }
            if (isQueryEdited) {
                vHolder.id_tv_more.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                //
                convertView.setOnClickListener(new MyViewClicker(groupPosition, childPosition));
            } else {
                Drawable drawable = ContextCompatHelper.getDrawable(getContext(), R.drawable.ic_add_black_24dp);
                vHolder.id_tv_more.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                //
                vHolder.id_tv_more.setOnClickListener(new MyViewClicker(groupPosition, childPosition));
            }

            return convertView;
        }

        private class MyViewClicker implements View.OnClickListener {
            public MyViewClicker(int groupPosition, int childPosition) {
                this.groupPosition = groupPosition;
                this.childPosition = childPosition;
            }

            private int groupPosition;
            private int childPosition;

            @Override
            public void onClick(View v) {

        /*            Intent intent = new Intent(getActivity(),
                            NurseFocusActivity.class);
                    intent.putExtra("WTXH",
                            mList.get(groupPosition).SimpleRecord
                                    .get(childPosition).XH);
                    intent.putExtra("GLLX", mList.get(groupPosition).GLLX);
                    intent.putExtra("GLXH", mList.get(groupPosition).XH);
                    if (mList.get(groupPosition).SimpleRecord
                            .get(childPosition).UMBER.equals("0"))
                        intent.putExtra("ISADD", true);
                    else
                        intent.putExtra("ISADD", false);
                    startActivityForResult(intent,
                            NurseFocusActivity.REQUEST_CODE);*/
                Intent intent = new Intent(getActivity(),
                        NurseFocusActivity.class);
                switch (mList.get(groupPosition).PZBH) {
                    case "1":
                        List<PGLX_JD_GL_FXPG_Bean> JD_GL_FXPG_BEANS = mList.get(groupPosition).FXPGJLBeanList
                                .get(childPosition).JD_GL_FXPG_BEANS;
                        String pgzf = mList.get(groupPosition).FXPGJLBeanList
                                .get(childPosition).PGZF;
                        String pgxh = mList.get(groupPosition).FXPGJLBeanList
                                .get(childPosition).PGXH;
                        if (JD_GL_FXPG_BEANS != null && JD_GL_FXPG_BEANS.size() > 0) {


                             /*   Map<String,String> selectMap=new HashMap<>();
                                for (PGLX_JD_GL_FXPG_Bean pglx_jd_gl_fxpg_bean:
                                        JD_GL_FXPG_BEANS) {
                                    selectMap.put(pglx_jd_gl_fxpg_bean.WTXH,pglx_jd_gl_fxpg_bean.WTMS);
                                }*/
                            List<FXPG_InfoDataWrapper.FXPG_InfoData> fxpg_infoDataList = new ArrayList<>();
                            for (PGLX_JD_GL_FXPG_Bean pglx_jd_gl_fxpg_bean :
                                    JD_GL_FXPG_BEANS) {
                                FXPG_InfoDataWrapper.FXPG_InfoData infoData = new FXPG_InfoDataWrapper.FXPG_InfoData();
                                infoData.WTXH = pglx_jd_gl_fxpg_bean.WTXH;
                                infoData.WTMS = pglx_jd_gl_fxpg_bean.WTMS;
                                infoData.GLLX = pglx_jd_gl_fxpg_bean.GLLX;
                                infoData.GLXH = pglx_jd_gl_fxpg_bean.GLXH;
                                fxpg_infoDataList.add(infoData);
                            }
                            FXPG_InfoDataWrapper fXPG_InfoDataWrapper = new FXPG_InfoDataWrapper(fxpg_infoDataList);

                              /*  intent.putExtra("WTXH", wtxh);
                                intent.putExtra("GLLX",gllx);
                                intent.putExtra("GLXH",glxh);*/
                            intent.putExtra("fXPG_InfoDataWrapper", fXPG_InfoDataWrapper);
                            intent.putExtra("need_replace", pgzf);
                            intent.putExtra("JLGLLX", "1");//1 风险评估
                            intent.putExtra("GLJL", pgxh); //pgxh
                            //
                            intent.putExtra("ISADD", !isQueryEdited);
                            startActivityForResult(intent,
                                    NurseFocusActivity.REQUEST_CODE);
                        }
                        break;
                    case "2":
                        intent.putExtra("WTXH", mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).WTXH);
                        intent.putExtra("GLLX", mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).GLLX);
                        intent.putExtra("GLXH", mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).GLXH);
                        intent.putExtra("JLGLLX", "2");//2 护理计划
                        intent.putExtra("GLJL", mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).JLWT);// jlwt
                        //
                        intent.putExtra("ZDMS_DataWrapper", new ZDMS_DataWrapper(mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).ZDMS_List));//
                        intent.putExtra("CSMS_DataWrapper", new CSMS_DataWrapper(mList.get(groupPosition).HLJHJLBeanList
                                .get(childPosition).CSMS_List));//
                        //
                        intent.putExtra("ISADD", !isQueryEdited);
                        startActivityForResult(intent,
                                NurseFocusActivity.REQUEST_CODE);
                        break;
                    case "3":
                        intent.putExtra("WTXH", mList.get(groupPosition).JD_GL_SMTZ_BeanList
                                .get(childPosition).wtxh);
                        intent.putExtra("GLLX", mList.get(groupPosition).JD_GL_SMTZ_BeanList
                                .get(childPosition).gllx);
                        intent.putExtra("GLXH", mList.get(groupPosition).JD_GL_SMTZ_BeanList
                                .get(childPosition).glxh);
                        String tznr = mList.get(groupPosition).JD_GL_SMTZ_BeanList
                                .get(childPosition).TZNR;
                        intent.putExtra("need_replace", tznr);
                        //
                        intent.putExtra("JLGLLX", "3");//3 生命体征
                        intent.putExtra("GLJL", String.valueOf(mList.get(groupPosition).JD_GL_SMTZ_BeanList
                                .get(childPosition).CJH));// cjh  int -->string
                        //
                        intent.putExtra("ISADD", !isQueryEdited);
                        startActivityForResult(intent,
                                NurseFocusActivity.REQUEST_CODE);
                        break;
                    case "4":
                        intent.putExtra("WTXH", mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).wtxh);
                        intent.putExtra("GLLX", mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).gllx);
                        intent.putExtra("GLXH", mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).glxh);
                        intent.putExtra("JLGLLX", "4");//4  检验项目

                        String ybhm = mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).YBHM;
                        String xmid = mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).XMID;
                        String jgid = mList.get(groupPosition).JYXM_PATIENTINFO_BeanList
                                .get(childPosition).JGID;
                        intent.putExtra("GLJL", ybhm + '|' + xmid + '|' + jgid);// ybhm + '|' + xmid + '|' + jgid
                        //
                        intent.putExtra("ISADD", !isQueryEdited);
                        startActivityForResult(intent,
                                NurseFocusActivity.REQUEST_CODE);
                        break;
                }


            }
        }

        //=========
        @Override
        public int getChildrenCount(int groupPosition) {

            switch (mList.get(groupPosition).PZBH) {
                case "1":
                    return mList.get(groupPosition).FXPGJLBeanList == null ? 0 : mList
                            .get(groupPosition).FXPGJLBeanList.size();
                case "2":
                    return mList.get(groupPosition).HLJHJLBeanList == null ? 0 : mList
                            .get(groupPosition).HLJHJLBeanList.size();
                case "3":
                    return mList.get(groupPosition).JD_GL_SMTZ_BeanList == null ? 0 : mList
                            .get(groupPosition).JD_GL_SMTZ_BeanList.size();
                case "4":
                    return mList.get(groupPosition).JYXM_PATIENTINFO_BeanList == null ? 0 : mList
                            .get(groupPosition).JYXM_PATIENTINFO_BeanList.size();
            }
            return 0;
        }


        @Override
        public Object getGroup(int groupPosition) {
            return mList.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return mList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return Long.parseLong(mList.get(groupPosition).PZBH);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            ParentHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_group_primary, parent, false);
                vHolder = new ParentHolder();
                vHolder.tv_planname = (TextView) convertView
                        .findViewById(R.id.id_tv);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ParentHolder) convertView.getTag();
            }
            if (isQueryEdited) {
                vHolder.tv_planname.setText(mList.get(groupPosition).DMMC + " (已写:" + getChildrenCount(groupPosition) + ")");
            } else {
                vHolder.tv_planname.setText(mList.get(groupPosition).DMMC + " (待写:" + getChildrenCount(groupPosition) + ")");
            }
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    class ParentHolder {
        TextView tv_planname;
    }

    class ChildHolder {
        TextView tv_itemname;
        TextView id_tv_more;
    }


}