package com.bsoft.mob.ienr.dynamicui.nurserecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.CareRecordActivity;
import com.bsoft.mob.ienr.activity.user.LifeSymptomActivity;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateListActivity;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.PopupWindowHelper;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.Association;
import com.bsoft.mob.ienr.model.nurserecord.RefrenceValue;
import com.bsoft.mob.ienr.util.DynamicUiUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.adapter.recyclerview.ClassicRVHeaderFooterAdapter;
import com.classichu.adapter.recyclerview.ClassicRVHeaderFooterViewHolder;
import com.classichu.popupwindow.util.ScreenUtil;

import java.util.List;

public class NurseRecordViewFactory {

    private Context mContext;
    private OnDateTimeClickListener mListener;
    private OnListUpDownClickListener mlistener_list;
    private AppApplication mApp;

    public interface OnListUpDownClickListener {
        void listUp();

        void listDown();
    }

    public interface OnDateTimeClickListener {

        void showPickerDialog(Integer id, String value, String format);
    }

    public NurseRecordViewFactory(Context mContext,
                                  OnDateTimeClickListener mListener, AppApplication app) {
        this.mContext = mContext;
        this.mListener = mListener;
        this.mApp = app;
    }

    public LinearLayout builderUi(List<UIView> list) {
        if (list == null || mContext == null) {
            return null;
        }

        LinearLayout root = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
        // 遍历大类
        for (UIView uiDate : list) {

            if ("0".equals(uiDate.LBH)) {// 必填类型，不显示UI，但记录保存数据
                root.setTag(uiDate.NRControllist);
                continue;
            }

            root.addView(ViewBuildHelper.buildClassTextViewLayout(mContext, uiDate.LBMC).first);

            if ("2".equals(uiDate.LBH)) {
                initDaymicUI(uiDate.NRControllist, root);
                continue;
            }

            // 静态控件，默认显示所有UI
            // 遍历大类里的所有UI控件
            for (PlugIn plugIn : uiDate.NRControllist) {
                View view = initPlugView(plugIn);
                if (view != null) {
                    view.setPadding(SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingTertiary(),
                            SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingTertiary());
                    root.addView(view);
                }
            }

        }

        return root;
    }

    /**
     * 初始化动态控件
     *
     * @param nRControllist
     * @param root
     */
    private void initDaymicUI(final List<PlugIn> nRControllist,
                              final LinearLayout root) {

        if (nRControllist == null || root == null) {
            return;
        }

        SpinnerLayout spinnerLayout = new SpinnerLayout(mContext);
        spinnerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Spinner spinner = spinnerLayout.getSpinner();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item);

        for (PlugIn plugIn : nRControllist) {
            if (!EmptyTool.isBlank(plugIn.XSMC)) {
                adapter.add(plugIn.XSMC);
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        root.addView(spinnerLayout);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                PlugIn plugin = nRControllist.get(position);
                if (plugin != null) {
                    View child = root.findViewWithTag(plugin.XSMC);
                    if (child != null) {
                        child.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 遍历大类里的所有UI控件
        for (PlugIn plugIn : nRControllist) {
            View view = initPlugView(plugIn);
            if (view != null) {
                view.setTag(plugIn.XSMC);
                if (EmptyTool.isBlank(plugIn.KJNR)) {
                    view.setVisibility(View.GONE);
                }
                root.addView(view);
            }
        }

    }

    private View initPlugView(PlugIn plugIn) {
        if (plugIn == null) {
            return null;
        }
        // 控件类型 1 Lable 2 TextBox 3 动态 4 下拉 5特殊控件
        switch (plugIn.KJLX) {
            case 1:
                return ViewBuildHelper.buildTextView(mContext, plugIn.XSMC);
            case 2:
                return initEditText(plugIn);
            case 4:
                if (plugIn.SFDX.equals("1")) {
                    return initComboxForMultiselect(plugIn);
                } else {
                    return initCombox(plugIn);
                }
            case 3:
                // TODO
                break;
            case 5:
                // TODO
                break;
            default:
                break;

        }
        return null;
    }

    /**
     * 支持手动输入和导入
     *
     * @param plugIn
     * @return
     */
    private View initCombox(final PlugIn plugIn) {

        if (null == plugIn.DropdownItem) {
            return null;

        }

        final PouponEditView pullEditView = new PouponEditView(mContext);
        pullEditView.setDataList(plugIn.DropdownItem);
        pullEditView.setTitle(plugIn.XSMC);
        pullEditView.setEditText(plugIn.KJNR);
        for (int i = 0; i < plugIn.DropdownItem.size(); i++) {
            if (plugIn.DropdownItem.get(i).VALUE.equals(plugIn.KJNR)) {
                pullEditView.setEditText(plugIn.DropdownItem.get(i).XZNR);
                break;
            }
        }

        // 用于回收输入数据
        pullEditView.edit.setTag(R.id.tag_KJ, plugIn);

        if (plugIn.SJLX == 2) {// 数字
            pullEditView.edit.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (plugIn.SJLX == 3) {// 时间和日期
            pullEditView.edit.setInputType(InputType.TYPE_CLASS_DATETIME
                    | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        }

        /*
        升级编号【56010022】============================================= start
        护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
        ================= Classichu 2017/10/18 10:41
        */
        LinearLayout linearLayout = null;
        if ("1".equals(plugIn.FZYT)//等于1不显示
                ) {/*no op*/} else {
            linearLayout = new LinearLayout(mContext);
            LinearLayout.LayoutParams pullEditView_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            pullEditView_vlp.weight = 1;
            pullEditView.setLayoutParams(pullEditView_vlp);
            linearLayout.addView(pullEditView);
            //
            final int xmbh = plugIn.KJH;
            //
            ImageView last = new ImageView(mContext);
            LinearLayout.LayoutParams last_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            last_vlp.gravity = Gravity.CENTER_VERTICAL;
            last.setLayoutParams(last_vlp);
            last.setImageResource(R.drawable.img_his);
            last.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    if (mContext instanceof CareRecordActivity) {
                        CareRecordActivity careRecordActivity = (CareRecordActivity) mContext;
                        careRecordActivity.showLastXMValueCombox(xmbh, pullEditView.getEditText(), plugIn);
                    }
                }
            });
            linearLayout.addView(last);

        }
        return linearLayout == null ? pullEditView : linearLayout;
        /* =============================================================== end */
    }

    /**
     * 支持手动输入和导入
     *
     * @param plugIn
     * @return
     */
    private View initComboxForMultiselect(final PlugIn plugIn) {

        if (null == plugIn.DropdownItem) {
            return null;
        }

        String dxfg = TextUtils.isEmpty(plugIn.DXFG) ? " " : plugIn.DXFG;
        final PouponEditViewForMultiselect pullEditViewForMultiselect = new PouponEditViewForMultiselect(mContext, dxfg);
        pullEditViewForMultiselect.setDataList(plugIn.DropdownItem);
        pullEditViewForMultiselect.setTitle(plugIn.XSMC);
//		pullEditViewForMultiselect.setEditText(plugIn.KJNR);
        //处理文本内容
        String s = writeTextContent(plugIn, dxfg);
        pullEditViewForMultiselect.setEditText(s);

        // 用于回收输入数据
        pullEditViewForMultiselect.edit.setTag(R.id.tag_KJ, plugIn);

        if (plugIn.SJLX == 2) {// 数字
            pullEditViewForMultiselect.edit.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else if (plugIn.SJLX == 3) {// 时间和日期
            pullEditViewForMultiselect.edit.setInputType(InputType.TYPE_CLASS_DATETIME
                    | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        }
  /*
        升级编号【56010022】============================================= start
        护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
        ================= Classichu 2017/10/18 10:41
        */
        LinearLayout linearLayout = null;
        if ("1".equals(plugIn.FZYT)//等于1不显示
                ) {/*no op*/} else {
            linearLayout = new LinearLayout(mContext);
            LinearLayout.LayoutParams pullEditView_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            pullEditView_vlp.weight = 1;
            pullEditViewForMultiselect.setLayoutParams(pullEditView_vlp);
            linearLayout.addView(pullEditViewForMultiselect);
            //
            final int xmbh = plugIn.KJH;
            //
            ImageView last = new ImageView(mContext);
            LinearLayout.LayoutParams last_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            last_vlp.gravity = Gravity.CENTER_VERTICAL;
            last.setLayoutParams(last_vlp);
            last.setImageResource(R.drawable.img_his);
            last.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    if (mContext instanceof CareRecordActivity) {
                        CareRecordActivity careRecordActivity = (CareRecordActivity) mContext;
                        careRecordActivity.showLastXMValueMultCombox(xmbh, pullEditViewForMultiselect, plugIn);
                    }
                }
            });
            linearLayout.addView(last);

        }
        return linearLayout == null ? pullEditViewForMultiselect : linearLayout;
        /* =============================================================== end */
    }

    private String writeTextContent(PlugIn plugIn, String dxfg) {
        String value = "";
        if (!EmptyTool.isBlank(plugIn.KJNR)) {
//            String[] strings = plugIn.KJNR.split(plugIn.DXFG);//key
            String[] strings = plugIn.KJNR.split(dxfg);//key
            String s = null;
            StringBuffer sb = new StringBuffer();
            List<PouponItem> datas = plugIn.DropdownItem;
            for (int j = 0; j < strings.length; j++) {
                String s1 = strings[j].trim();
                boolean isOk = false;
                for (int k = 0; k < datas.size(); k++) {
                    String xznr = datas.get(k).XZNR;
                    String key = datas.get(k).VALUE;
                    if (key.equals(s1)) {
                        s = xznr + dxfg;
//                        s = xznr + plugIn.DXFG;
                        sb.append(s);
                        isOk = true;
                        break;
                    }
                }
                if (!isOk) {
//                    sb.append(s1).append(plugIn.DXFG);
                    sb.append(s1).append(dxfg);
                }
            }
            value = sb.toString();
//            if (value.endsWith(plugIn.DXFG)) {
            if (value.endsWith(dxfg)) {
                value = value.substring(0, value.length() - dxfg.length());
            }
            return value;
        }
        return null;
    }

    /**
     * 输入框
     *
     * @param plugIn
     * @return
     */
    private View initEditText(final PlugIn plugIn) {

        LinearLayout content = new LinearLayout(mContext);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView txt = ViewBuildHelper.buildTextView(mContext, plugIn.XSMC);
        if (plugIn.YSLX.equals("6")) {
            txt.setTextColor(mContext.getResources().getColor(R.color.pink));
        } else if (plugIn.YSLX.equals("453")) {
            txt.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            txt.setTextColor(ContextCompat.getColor(mContext, R.color.textColorSecondary));
        }


          /*
        升级编号【56010022】============================================= start
        护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
        ================= Classichu 2017/10/18 10:41
        */
        final EditText edit = ViewBuildHelper.buildEditTextAutoWrap(mContext, plugIn.KJNR);
        /* =============================================================== end */

        final String min = plugIn.ZCZXX;
        final String max = plugIn.ZCZSX;

        // 设置输入类型
        if (plugIn.SJLX == 2) {// 数字
            edit.setInputType(InputType.TYPE_CLASS_NUMBER
                    | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            DynamicUiUtil.setMaxMinValue(min, max, edit, plugIn.YSZXX,
                    plugIn.YSZSX);
        } else if (plugIn.SJLX == 3) {// 时间和日期
            edit.setInputType(InputType.TYPE_CLASS_DATETIME
                    | InputType.TYPE_DATETIME_VARIATION_NORMAL);
            edit.setHint(plugIn.SJGS);
            edit.setHintTextColor(ContextCompat.getColor(mContext,R.color.textColorTertiary));
            edit.setFocusable(false);
            edit.setId(plugIn.KJH);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        String value = ((EditText) v).getText().toString();
                        String hint = ((EditText) v).getHint().toString();
                        mListener.showPickerDialog(plugIn.KJH, value, hint);
                    }
                }
            });
        }

        if ((plugIn.SJLX == 1 || plugIn.SJLX == 2)
                && (plugIn.YSLX.equals("6") || plugIn.YSLX.equals("453"))) {
            txt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPopupWindow(edit, plugIn);
                }
            });
        }
        // 用于回收输入数据
        edit.setTag(R.id.tag_KJ, plugIn);

        content.addView(txt);
        content.addView(edit);

         /*
        升级编号【56010022】============================================= start
        护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
        ================= Classichu 2017/10/18 10:41
        */
        //add 2017-9-10 10:53:58
        if ("1".equals(plugIn.FZYT)//等于1不显示
                ) {/*no op*/} else {

            final int xmbh = plugIn.KJH;
            //
            ImageView last = new ImageView(mContext);
            LinearLayout.LayoutParams last_vlp = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            last_vlp.gravity = Gravity.CENTER_VERTICAL;
            last.setLayoutParams(last_vlp);
            last.setImageResource(R.drawable.img_his);
            last.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    if (mContext instanceof CareRecordActivity) {
                        CareRecordActivity careRecordActivity = (CareRecordActivity) mContext;
                        careRecordActivity.showLastXMValueEdit(xmbh, edit);
                    }
                }
            });
            content.addView(last);

        }
        /* =============================================================== end */
        return content;
    }

    private MyHeaderFooterAdapter myHeaderFooterAdapter;

    private class MyHeaderFooterAdapter extends ClassicRVHeaderFooterAdapter<RefrenceValue> {

        public MyHeaderFooterAdapter(Context mContext, int mItemLayoutId) {
            super(mContext, mItemLayoutId);
        }

        @Override
        public RVHeaderFooterAdapterDelegate setupDelegate() {
            return null;
        }

        @Override
        public void findBindView(int position, ClassicRVHeaderFooterViewHolder classicRVHeaderFooterViewHolder) {
            TextView tv_name = classicRVHeaderFooterViewHolder.findBindItemView(R.id.id_tv_three);
            TextView tv_value = classicRVHeaderFooterViewHolder.findBindItemView(R.id.id_tv_two);
            TextView tv_time = classicRVHeaderFooterViewHolder.findBindItemView(R.id.id_tv_one);
            tv_name.setText(EmptyTool.isBlank(mDataList.get(position).NAME) ? ""
                    : mDataList.get(position).NAME);
            tv_value.setText(mDataList.get(position).VALUE);
            tv_time.setText(mDataList.get(position).TIME);
        }
    }

    private void toRefreshData(final PlugIn plugin) {
        new AsyncTask<Void, Void, Response<Association>>() {
            @Override
            protected void onPreExecute() {
//                tv_refresh.setTextColor(Color.GRAY);
//                tv_refresh.setEnabled(false);
            }

            @Override
            protected Response<Association> doInBackground(
                    Void... params) {
                return NurseRecordApi.getInstance(mContext)
                        .getAssociation(mApp.sickPersonVo.ZYH,
                                plugin.YSLX, plugin.YSKZ, 0, mApp.jgId);
            }

            @Override
            protected void onPostExecute(Response<Association> result) {
//                tv_refresh.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
//                tv_refresh.setEnabled(true);
                if (result.ReType == 1) {
                    BSToast.showToast(mContext, result.Msg,
                            BSToast.LENGTH_LONG);
                    return;
                }
                plugin.RefrenceValue = result.Data.RefrenceValue;
                if (plugin.RefrenceValue != null) {
                    plugin.PageIndex = 0;
                    plugin.PageSize = result.Data.PageSize;
                    //
                    myHeaderFooterAdapter.refreshDataList(plugin.RefrenceValue);
                }
            }
        }.execute();
    }

    private OnClickListener mOnClickListener;
    private TextView tv;

    private void showPopupWindow(final View parent, final PlugIn plugin) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popwindow_carerecord, null, false);
        TextView tv_cancel = (TextView) view.findViewById(R.id.cancel);
        final TextView tv_refresh = (TextView) view.findViewById(R.id.refresh);
        TextView tv_create = (TextView) view.findViewById(R.id.create);

        final RecyclerView list = (RecyclerView) view
                .findViewById(R.id.pulltorefresh);
        list.setLayoutManager(new LinearLayoutManager(mContext));
        list.setHasFixedSize(true);
        list.setItemAnimator(new DefaultItemAnimator());

        myHeaderFooterAdapter = new MyHeaderFooterAdapter(mContext, R.layout.item_list_text_three_secondary);
        list.setAdapter(myHeaderFooterAdapter);
        tv = new TextView(mContext);
        tv.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 20, 10, 20);
        tv.setGravity(Gravity.CENTER);
        tv.setText("点击加载更多");
        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                int nextPageIndex = plugin.PageIndex + 1;
                if (nextPageIndex > plugin.PageSize - 1) {
                    tv.setText("加载完成");
                    tv.setOnClickListener(null);
                    return;
                }

                new AsyncTask<Void, Void, Response<Association>>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        tv.setEnabled(false);
                        tv.setText("加载中...");
                    }

                    @Override
                    protected Response<Association> doInBackground(
                            Void... params) {
                        return NurseRecordApi.getInstance(mContext)
                                .getAssociation(mApp.sickPersonVo.ZYH,
                                        plugin.YSLX, plugin.YSKZ,
                                        nextPageIndex, mApp.jgId);
                    }

                    @Override
                    protected void onPostExecute(Response<Association> result) {
                        tv.setEnabled(true);
                        tv.setText("点击加载更多");
                        if (result.ReType == 1) {
                            BSToast.showToast(mContext, result.Msg,
                                    BSToast.LENGTH_LONG);
                            return;
                        }

                        if (result.Data == null
                                || result.Data.RefrenceValue == null) {
                            return;
                        }
                        //成功后赋值
                        plugin.PageIndex = nextPageIndex;
                        for (RefrenceValue value : result.Data.RefrenceValue) {
                            plugin.RefrenceValue.add(value);
                        }
                        //
                        myHeaderFooterAdapter.refreshDataList(plugin.RefrenceValue);
                        // if (plugin.RefrenceValue != null
                        // && plugin.RefrenceValue.size() > 0)
                        // list.setAdapter(new HistoryAdapter(
                        // plugin.RefrenceValue));
                    }
                }.execute();
            }
        };
        tv.setOnClickListener(mOnClickListener);
        myHeaderFooterAdapter.addFooterView(tv);
        myHeaderFooterAdapter.setOnItemClickListener(new ClassicRVHeaderFooterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                super.onItemClick(view, position);
                EditText editText = (EditText) parent;
                String value = plugin.RefrenceValue.get(position).VALUE;
                editText.setText(value);
                editText.setSelection(value.length());
            }
        });
        if (plugin.RefrenceValue != null) {
            myHeaderFooterAdapter.refreshDataList(plugin.RefrenceValue);
        }
        final PopupWindow popupWindow = new PopupWindow(view,
                parent.getWidth(), ScreenUtil.getScreenHeight() / 2);
        // 加了下面这行，onItemClick才好用
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext
                .getResources(), (Bitmap) null));
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        tv_create.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (plugin.YSLX.equals("6")) {
                    Intent intent = new Intent(mContext,
                            LifeSymptomActivity.class);
                    mContext.startActivity(intent);
                } else if (plugin.YSLX.equals("453")) {
                    Intent intent = new Intent(mContext,
                            RiskEvaluateListActivity.class);
                    mContext.startActivity(intent);
                }

            }
        });
        tv_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        tv_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tv.setText("点击加载更多");
                tv.setOnClickListener(mOnClickListener);
                toRefreshData(plugin);
            }
        });
//        popupWindow.showAsDropDown(parent, 0, 4);
        PopupWindowHelper.show(popupWindow, parent, 4);
    }
}
