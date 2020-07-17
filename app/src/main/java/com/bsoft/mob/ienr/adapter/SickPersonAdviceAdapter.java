package com.bsoft.mob.ienr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.model.advice.AdvicePlanVo;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.NumberCharParser;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:25:40
 * @医嘱计划适配器
 */
public class SickPersonAdviceAdapter extends BaseAdapter {
    private static final String TAG_START = "tag_start";
    private static final String TAG_CENTER = "tag_center";
    private static final String TAG_END = "tag_end";
    private static final String TAG_EMPTY = "tag_empty";
    private ArrayList<AdvicePlanVo> list;
    private LayoutInflater inflater;
    // 记录Item是否选中
    SparseBooleanArray map = new SparseBooleanArray();
    // 记录医嘱组号标示
    SparseIntArray zhmap = new SparseIntArray();
    // 记录checkbox的选中情况
    SparseBooleanArray checkmap = new SparseBooleanArray();
    //
    SparseArray<String> yzTableMap = new SparseArray();


    public interface SYThingListener {
        void longClick(AdvicePlanVo planVo);
    }

    private SYThingListener mSYThingListener;

    public void setSYThingListener(SYThingListener mThing) {
        this.mSYThingListener = mThing;
    }

    public interface KFThingListener {
        void longClick(AdvicePlanVo planVo);
    }

    private KFThingListener mKFThingListener;

    public void setKFThingListener(KFThingListener mThing) {
        this.mKFThingListener = mThing;
    }

    private ZSThingListener mZSThingListener;

    public interface ZSThingListener {
        void longClick(AdvicePlanVo planVo);
    }

    public void setZSThingListener(ZSThingListener mThing) {
        this.mZSThingListener = mThing;
    }

    public SickPersonAdviceAdapter(Context context, ArrayList<AdvicePlanVo> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
        init();

    }

    public void initTag() {
        if (list == null || list.size() <= 0) {
            return;
        }
        yzTableMap.clear();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                //第一个 开始标记
                yzTableMap.put(i, TAG_START);
            } else {
                if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)
                        && list.get(i).JHSJ.equals(list.get(i - 1).JHSJ)) {
                    //如果和上一个同一组  中间标记
                    yzTableMap.put(i, TAG_CENTER);
                } else {
                    //如果和上一个不是同一组  
                    if (TAG_START.equals(yzTableMap.get(i - 1))) {
                        //上一个是开始的话
                        yzTableMap.put(i - 1, TAG_EMPTY); //上一个
                        yzTableMap.put(i, TAG_START); //当前
                    } else if (TAG_CENTER.equals(yzTableMap.get(i - 1))) {
                        //上一个是中间的话
                        yzTableMap.put(i - 1, TAG_END); //上一个
                        yzTableMap.put(i, TAG_START);//当前
                    } else if (TAG_END.equals(yzTableMap.get(i - 1))) {
                        yzTableMap.put(i, TAG_END);//当前
                    }
                }
            }
        }
        //Fix last
        String lastTag = yzTableMap.get(yzTableMap.size() - 1);
        if (lastTag != null) {
            if (TAG_EMPTY.equals(lastTag)) {
                lastTag = TAG_EMPTY;
            } else if (TAG_START.equals(lastTag)) {
                lastTag = TAG_EMPTY;
            } else if (TAG_CENTER.equals(lastTag)) {
                lastTag = TAG_END;
            } else if (TAG_END.equals(lastTag)) {
                lastTag = TAG_END;
            }
            yzTableMap.put(yzTableMap.size() - 1, lastTag);
        }
    }

    public void init() {

        for (int i = 0; i < list.size(); i++) {
            map.put(i, false);
            checkmap.put(i, false);
            if (i == 0) {
                zhmap.put(i, 0);
            } else {
                if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)
                        && list.get(i).JHSJ.equals(list.get(i - 1).JHSJ)) {
                    zhmap.put(i, zhmap.get(i - 1));
                } else {
                    if (zhmap.get(i - 1) == 1) {
                        zhmap.put(i, 0);
                    } else {
                        zhmap.put(i, 1);
                    }
                }
            }
        }

        initTag();
    }

    // 判断是否是同样类型的,前提choseList不为空及size>0
    public boolean isOneType(ArrayList<AdvicePlanVo> choseList) {

        if (choseList == null) {
            return true;
        }
        boolean flage = true;
        int GSLX = choseList.get(0).GSLX;
        for (int i = 1; i < choseList.size(); i++) {
            if (GSLX != choseList.get(i).GSLX) {
                flage = false;
                break;
            }
        }
        return flage;
    }

    public int getNowDataPosInAdapter(String JHSJ, String YZZH, String JHH) {
        int nowDataPos = getNowDataPosInAdapterInner(JHSJ, YZZH, JHH);
        if (nowDataPos < 0) {
            nowDataPos = getNowDataPosInAdapterInner(JHSJ, YZZH);
        }
        return nowDataPos;
    }

    //按组项
    private int getNowDataPosInAdapterInner(String JHSJ, String YZZH, String JHH) {
        if (list==null||list.isEmpty()||JHSJ==null||YZZH==null||JHH==null){
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (JHSJ.equals(list.get(i).JHSJ) && YZZH.equals(list.get(i).YZZH) && JHH.equals(list.get(i).JHH)) {
                return i;
            }
        }
        return -1;
    }

    //按组
    private int getNowDataPosInAdapterInner(String JHSJ, String YZZH) {
        if (list==null||list.isEmpty()||JHSJ==null||YZZH==null){
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (JHSJ.equals(list.get(i).JHSJ) && YZZH.equals(list.get(i).YZZH)) {
                return i;
            }
        }
        return -1;
    }

    // 选中的
    public ArrayList<AdvicePlanVo> getValue() {

        if (null != list && list.size() > 0) {
            ArrayList<AdvicePlanVo> cList = new ArrayList<AdvicePlanVo>();
            for (int i = 0; i < list.size(); i++) {
                if (checkmap.get(i)) {
                    cList.add(list.get(i));
                }
            }
            return cList;
        }
        return null;
    }

    public void changeBackgroundColor(ListView listView, int position) {
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //修改指定pos itemView的内容
                int firstVisiblePosition = listView.getFirstVisiblePosition();
                View itemView = listView.getChildAt(position - firstVisiblePosition);
                if (itemView == null) {
                    return;
                }
                LinearLayout view = itemView.findViewById(R.id.view);
                if (view == null) {
                    return;
                }
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.classicItemBgHighlight));
                //
            }
        }, 600);

    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public AdvicePlanVo getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_drugs_advice, parent, false);
            vHolder = new ViewHolder();

            vHolder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);
            vHolder.name = (TextView) convertView.findViewById(R.id.name);
            vHolder.time = (TextView) convertView.findViewById(R.id.time);
            vHolder.view = convertView.findViewById(R.id.view);
            vHolder.boxView = convertView.findViewById(R.id.boxView);
            vHolder.allName = (TextView) convertView.findViewById(R.id.allName);
            vHolder.JLXX = (TextView) convertView.findViewById(R.id.JLXX);
            vHolder.SLXX = (TextView) convertView.findViewById(R.id.SLXX);
            vHolder.ZXZT = (TextView) convertView.findViewById(R.id.ZXZT);
            vHolder.KSXX = (TextView) convertView.findViewById(R.id.KSXX);
            vHolder.JSXX = (TextView) convertView.findViewById(R.id.JSXX);

            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }

        final AdvicePlanVo vo = list.get(position);

        StringBuilder YZMCsb = new StringBuilder();

        //
        if (TAG_START.equals(yzTableMap.get(position))) {
            YZMCsb.append("┌ ");
        } else if (TAG_CENTER.equals(yzTableMap.get(position))) {
            YZMCsb.append("├ ");
        } else if (TAG_END.equals(yzTableMap.get(position))) {
            YZMCsb.append("└ ");
        } else if (TAG_EMPTY.equals(yzTableMap.get(position))) {
            YZMCsb.append(" — ");
        }


//       String YZMCstr = YZMC.toString();
        String ls = "";
        if (vo.LSBS != 0) {
            ls = NumberCharParser.parserNumWithCircle(vo.LSBS);
        }
        String yf = "  ";//如果没有用法 可以空格
//        if ("6".equals(vo.YPYF)) {
        if (AppApplication.getInstance().userConfig.jingTui_YaoPinYongFa.equals(vo.YPYF)) {
//            yf = "【静推】";
            yf = "【" + vo.YPYFMC + "】";
//        } else if ("22".equals(vo.YPYF)) {
        } else if (AppApplication.getInstance().userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(vo.YPYF)) {
//            yf = "【微注泵推注】";
            yf = "【" + vo.YPYFMC + "】";
        } else if (!TextUtils.isEmpty(vo.YPYFMC)) {
            //add 2018-6-4 18:59:08
            yf = "【" + vo.YPYFMC + "】";
        }
        String rawYzmcs = vo.YZMC;
        String simpleYzmcs = rawYzmcs;
        if (!TextUtils.isEmpty(simpleYzmcs) && simpleYzmcs.contains("/")) {
            simpleYzmcs = simpleYzmcs.substring(0, simpleYzmcs.indexOf("/"));
        }
        String JL_DW = "";
        if (!TextUtils.isEmpty(vo.JLXX)) {
            JL_DW = "(" + vo.JLXX + ")";
        }
        String SL_DW = "";
        if (!TextUtils.isEmpty(vo.SLXX)) {
            SL_DW = "(" + vo.SLXX + ")";
        }
        //增加"医嘱执行"下的展示：使用频次
        String SY_PC = "";
        if (!TextUtils.isEmpty(vo.SYPC)) {
            SY_PC = vo.SYPC;
        }
        if (AppApplication.getInstance().userConfig.qiYong_SY_LSBS_Show) {
            YZMCsb.append(ls);
        }
        YZMCsb.append(yf).append(simpleYzmcs);
        //
        if (vo.GSLX != 1) {
            //护理治疗不显示JLXX
            YZMCsb.append(JL_DW).append(SL_DW);
        }
        //
        if (vo.ZXWZ == 1) {
            YZMCsb.append("||").append("手术医嘱");
        }

        Date date = DateUtil.getDateCompat(vo.JHSJ);
        String adviceTime = DateUtil.format_HHmm.format(date);
        String str = EmptyTool.isBlank(vo.SJMC) ? adviceTime : vo.SJMC;
        //增加使用频次(调整)
        vHolder.time.setText(SY_PC.concat("  ").concat(str));
        vHolder.name.setText(YZMCsb.toString());
        vHolder.allName.setText(rawYzmcs);
        /*if (!(EmptyTool.isBlank(vo.JSGH) && EmptyTool.isBlank(vo.KSSJ))) {
            if (vo.GSLX == 4) {
                String tempDate = "";
                if (DateUtil.getDateCompat(vo.KSSJ) != null) {
                    tempDate = DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(vo.KSSJ));
                }
                vHolder.JSXX.setText("执行：" + vo.JSGH + "于" + tempDate);
            } else {
                String tempDate = "";
                if (DateUtil.getDateCompat(vo.KSSJ) != null) {
                    tempDate = DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(vo.KSSJ));
                }
                vHolder.JSXX.setText("结束：" + vo.JSGH + "于" + tempDate);
            }
            vHolder.JSXX.setVisibility(View.VISIBLE);
        } else {
            vHolder.JSXX.setVisibility(View.GONE);
        }*/
        String tempKssj = "";
        if (DateUtil.getDateCompat(vo.KSSJ) != null) {
            tempKssj = DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(vo.KSSJ));
        }
        vHolder.KSXX.setText("开始:" + tempKssj + (vo.KSGH == null ? "" : " " + vo.KSGH));
        String tempJssj = "";
        if (DateUtil.getDateCompat(vo.JSSJ) != null) {
            tempJssj = DateUtil.format_MMdd_HHmm.format(DateUtil.getDateCompat(vo.JSSJ));
        }
        vHolder.JSXX.setText("结束:" + tempJssj + (vo.JSGH == null ? "" : " " + vo.JSGH));
        //
        vHolder.JLXX.setText(StringUtil.getText("剂量：", vo.JLXX));
        vHolder.SLXX.setText(StringUtil.getText("数量：", vo.SLXX));

        if (map.get(position)) {
            vHolder.boxView.setVisibility(View.VISIBLE);
        } else {
            vHolder.boxView.setVisibility(View.GONE);
        }
        if (zhmap.get(position) == 1) {
            vHolder.view.setBackgroundResource(R.color.classicViewBg);//item
            vHolder.boxView.setBackgroundResource(R.color.classicViewBgDark);//item 展开
        } else {
            vHolder.view.setBackgroundResource(R.color.classicViewBgLight);//item
            vHolder.boxView.setBackgroundResource(R.color.classicViewBgDark);//item 展开
        }

        vHolder.view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (vHolder.boxView.getVisibility() == View.GONE) {
                    map.put(position, true);
                    vHolder.boxView.setVisibility(View.VISIBLE);
                } else {
                    map.put(position, false);
                    vHolder.boxView.setVisibility(View.GONE);
                }
            }
        });

        // 解决listview 复用view 时，自动响应OnCheckedChangeListener事件
        vHolder.checkBox.setOnCheckedChangeListener(null);

        vHolder.checkBox.setChecked(checkmap.get(position));

        // vHolder.checkBox.setOnCheckedChangeListener(this);
        if (vo.ZXZT == 1) {
            vHolder.checkBox.setClickable(false);
        } else {
            vHolder.checkBox.setClickable(true);
            vHolder.checkBox
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton arg0,
                                                     boolean arg1) {

                            resetSelected(position, arg1, vo.GSLX);
                            notifyDataSetChanged();
                        }
                    });
        }
        //
        listenerClick(vHolder.view, vo);
        //
        String defaultColorStr = "#000000";
        // 1 已执行 2 正在执行 4 暂停 0 未执行 5 拒绝
        switch (vo.ZXZT) {
            case 0:
                vHolder.ZXZT.setText("默");
                vHolder.ZXZT.setVisibility(View.INVISIBLE);
                vHolder.name.setTextColor(Color.parseColor(defaultColorStr));
                break;
            case 1:
                vHolder.ZXZT.setText("已");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                vHolder.ZXZT.setTextColor(Color.parseColor("#006400"));
                vHolder.name.setTextColor(Color.parseColor("#006400"));
                break;
            case 2:
                // vHolder.ZXZT.setText("→");
                vHolder.ZXZT.setText("➔");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                vHolder.ZXZT.setTextColor(Color.parseColor("#00B2EE"));
                vHolder.name.setTextColor(Color.parseColor("#00B2EE"));
                break;
            case 4:
                vHolder.ZXZT.setText("||");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                vHolder.ZXZT.setTextColor(Color.parseColor("#FF8C00"));
                vHolder.name.setTextColor(Color.parseColor("#FF8C00"));
                break;
            case 5:
                vHolder.ZXZT.setText("拒");
                vHolder.ZXZT.setVisibility(View.VISIBLE);
                vHolder.ZXZT.setTextColor(Color.parseColor("#EE0000"));
                vHolder.name.setTextColor(Color.parseColor("#EE0000"));
                break;
            default:
                vHolder.ZXZT.setText("默");
                vHolder.ZXZT.setVisibility(View.INVISIBLE);
                vHolder.name.setTextColor(Color.parseColor(defaultColorStr));
                break;
        }
        return convertView;
    }

    private void listenerClick(View itemView, AdvicePlanVo vo) {
        if (vo.GSLX == 4) {
            //输液
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                        Toast.makeText(v.getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
                    if (mSYThingListener != null) {
                        mSYThingListener.longClick(vo);
                        return true;
                    }
                    return false;
                }
            });
        } else if (vo.GSLX == 3) {
            //输液
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                        Toast.makeText(v.getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
                    if (mKFThingListener != null) {
                        mKFThingListener.longClick(vo);
                        return true;
                    }
                    return false;
                }
            });
        } else if (vo.GSLX == 5) {
            //输液
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                        Toast.makeText(v.getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
                    if (mZSThingListener != null) {
                        mZSThingListener.longClick(vo);
                        return true;
                    }
                    return false;
                }
            });
        } else {
            itemView.setOnLongClickListener(null);
        }
    }

    class ViewHolder {
        public CheckBox checkBox;
        public View view, boxView;
        public TextView name, time, allName, JLXX, SLXX, ZXZT, KSXX, JSXX;
    }

    /**
     * 重置选择顶
     *
     * @param position
     * @param isChecked
     * @param gSLX
     */
    private void resetSelected(int position, boolean isChecked, int gSLX) {

        if (isChecked) {
            checkmap.clear();
        }

        // 选中之后连选
        checkmap.put(position, isChecked);

        // 非输液、注射不级联取消
        if (!isChecked && !(gSLX == 4 || gSLX == 5)) {
            return;
        }

        int zh = zhmap.get(position);
        int count = getCount();
        for (int i = position + 1; i < count; i++) {
            if (zh == zhmap.get(i)) {
                checkmap.put(i, isChecked);
            } else {
                break;
            }
        }
        if (position != 0) {
            for (int i = position - 1; i >= 0; i--) {
                if (zh == zhmap.get(i)) {
                    checkmap.put(i, isChecked);
                } else {
                    break;
                }
            }
        }
    }


    public void refreshData(ArrayList<AdvicePlanVo> advicePlanVoArrayList) {
        list.clear();
        list.addAll(advicePlanVoArrayList);
        init();
        this.notifyDataSetChanged();
    }

}
