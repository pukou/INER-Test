/**
 * @Title: LifeSymptomViewFactory.java
 * @Package com.bsoft.mob.ienr.dynamicui.riskevaluate
 * @Description: 风险评估动态生成界面
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-9 下午1:43:29
 * @version V1.0
 */
package com.bsoft.mob.ienr.dynamicui.riskevaluate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateActivity;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.helper.SpannableStringBuilderHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.risk.FactorGoal;
import com.bsoft.mob.ienr.model.risk.QualityControl;
import com.bsoft.mob.ienr.model.risk.RiskFactor;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: LifeSymptomViewFactory
 * @Description: 风险评估动态生成界面
 * @date 2015-12-9 下午1:43:29
 */
public class RiskEvaluateViewFactory {
    Context context;
    LinearLayout root;
    List<QualityControl> qcList;
    List<RiskFactor> mList;

    public RiskEvaluateViewFactory(Context context, LinearLayout root,
                                   List<QualityControl> qclist, List<RiskFactor> list) {
        super();
        this.context = context;
        this.root = root;
        this.qcList = qclist;
        this.mList = list;
    }

    public void build() {

        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).DXBZ.equals("1")) {
                buildRadio(mList.get(i), i);
            } else {
                buildCheck(mList.get(i), i);
            }
        }
    }

    private void buildRadio(final RiskFactor factor, int i) {
        final LinearLayout card = LayoutParamsHelper.buildLinearMatchWrap_V(context);
        card.setBackgroundColor(ContextCompat.getColor(context, R.color.classicViewBg));
        SizeHelper.setPadding(card, 10);
        final TextView title = ViewBuildHelper.buildTextView(context, factor.YZMS);
        //title.getPaint().setFakeBoldText(true);
        if ("1".equals(factor.BXBZ)) {
//            title.setTextColor(Color.RED);
            title.append(" *");
            CharSequence charSequence = SpannableStringBuilderHelper.getTextColoredCharSequence(title.getText().toString(), "*", Color.RED);
            title.setText(charSequence);
//            title.append(HtmlCompatHelper.fromHtml("<font color=\"#ff0000\">*</font>"));
        }
        Drawable drawable = ContextCompatHelper.getDrawable(context, R.drawable.ic_info_outline_black_24dp);
        title.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        title.setTag(i);
        card.addView(title);

        if (factor.YZPF != null) {
            for (int j = 0; j < factor.YZPF.size(); j++) {
                FactorGoal goal = factor.YZPF.get(j);
                final CheckBox cb = new AppCompatCheckBox(context);
                cb.setText(goal.FZMS + "(" + goal.PFFZ + "')");
                cb.setId(goal.FZXH);
                /*cb.setButtonDrawable(context.getResources().getDrawable(
                        R.drawable.ck_tmp));*/
                ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                cb.setLayoutParams(vlp);
                cb.setTag(j);
                cb.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (cb.isChecked()) {
                            int old = 0;
                            int goal = ((RiskEvaluateActivity) context)
                                    .getGoal();
                            for (int k = 0; k < factor.YZPF.size(); k++) {
                                CheckBox c = (CheckBox) card
                                        .findViewById(factor.YZPF.get(k).FZXH);
                                if (c != null) {
                                    int p = (Integer) c.getTag();
                                    if (c.getId() != cb.getId()) {
                                        if (c.isChecked()) {
                                            old -= Integer.parseInt(factor.YZPF
                                                    .get(p).PFFZ);
                                            c.setChecked(false);
                                            factor.YZPF.get(p).SELECT = false;
                                        }
                                    } else {
                                        old += Integer.parseInt(factor.YZPF
                                                .get(p).PFFZ);
                                        factor.YZPF.get(p).SELECT = true;
                                    }
                                }
                            }
                            goal += old;
                            ((RiskEvaluateActivity) context)
                                    .setGoalAndLevel(String.valueOf(goal));
                        } else {
                            int p = (Integer) cb.getTag();
                            int goal = ((RiskEvaluateActivity) context)
                                    .getGoal();
                            goal -= Integer.parseInt(factor.YZPF.get(p).PFFZ);
                            ((RiskEvaluateActivity) context)
                                    .setGoalAndLevel(String.valueOf(goal));
                            factor.YZPF.get(p).SELECT = false;
                        }
                    }
                });
                cb.setChecked(!EmptyTool.isBlank(factor.YZPF.get(j).MXXH));
                card.addView(cb);
            }
        }
        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StringBuffer info = new StringBuffer();
                int pos = (Integer) title.getTag();
                for (FactorGoal goal : mList.get(pos).YZPF) {
                    String bzxx = goal.BZXX;

                    if (bzxx == null) {
                        bzxx = " ";
                    } else {
                        bzxx = bzxx + "\n";
                    }
                    info.append(goal.FZMS).append(":").append(bzxx).append("\n");
                }
                ((RiskEvaluateActivity) context).showInfo(info.toString());
            }
        });

        root.addView(card);
    }

    private void buildCheck(final RiskFactor factor, int i) {
        final LinearLayout card = LayoutParamsHelper.buildLinearMatchWrap_V(context);
        card.setBackgroundColor(ContextCompat.getColor(context, R.color.classicViewBg));

        final TextView title = ViewBuildHelper.buildTextView(context, factor.YZMS);
        //title.getPaint().setFakeBoldText(true);
        if ("1".equals(factor.BXBZ)) {
//            title.setTextColor(Color.RED);
            title.append(" *");
            CharSequence charSequence = SpannableStringBuilderHelper.getTextColoredCharSequence(title.getText().toString(), "*", Color.RED);
            title.setText(charSequence);
//            title.append(HtmlCompatHelper.fromHtml("<font color=\"#ff0000\"> *</font>"));
        }
        title.setTag(i);
        Drawable drawable = ContextCompatHelper.getDrawable(context, R.drawable.ic_info_outline_black_24dp);
        title.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        card.addView(title);

        if (factor.YZPF != null) {

            for (int j = 0; j < factor.YZPF.size(); j++) {
                FactorGoal goal = factor.YZPF.get(j);
                final CheckBox cb = new AppCompatCheckBox(context);
                cb.setText(goal.FZMS + "(" + goal.PFFZ + "')");
                cb.setId(goal.FZXH);
                /*cb.setButtonDrawable(context.getResources().getDrawable(
                        R.drawable.ck_mulit_slt));*/
                ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                cb.setLayoutParams(vlp);
                cb.setTag(j);
                cb.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (cb.isChecked()) {
                            int p = (Integer) cb.getTag();
                            int goal = ((RiskEvaluateActivity) context)
                                    .getGoal();

                            //福建协和客户化：IENR_FXYZPF.FZXH为288,195,290,291,289的选项，选中多个时只计算一个的分数
                            FactorGoal factorGoal = factor.YZPF.get(p);
                            String fxzh=String.valueOf(factorGoal.FZXH);
                            String onlyJsOnce = "288,195,290,291,289";//194 0
                            //包含当前选中
                            if (onlyJsOnce.contains(fxzh)) {
                                Boolean isHave = false;
                                for (FactorGoal item : factor.YZPF) {
                                    if (item.SELECT && onlyJsOnce.contains(String.valueOf(item.FZXH))) {
                                        isHave = true;
                                        break;
                                    }
                                }
                                if (!isHave) {
                                    goal += Integer.parseInt(factorGoal.PFFZ);
                                }
                            } else {
                                if (factor.YZSX != null && factor.YZSX.length() != 0) {
                                    int csPFFZ = 0;
                                    for (FactorGoal item : factor.YZPF) {
                                        if (item.SELECT) {
                                            csPFFZ += Integer.parseInt(item.PFFZ);
                                        }
                                    }
                                    if (Integer.parseInt(factor.YZSX) >= csPFFZ) {
                                        goal = goal - csPFFZ;
                                    } else {
                                        goal = goal - Integer.parseInt(factor.YZSX);
                                    }

                                    int zzPFFZ = 0;
                                    factorGoal.SELECT = true;
                                    for (FactorGoal item : factor.YZPF) {
                                        if (item.SELECT) {
                                            zzPFFZ += Integer.parseInt(item.PFFZ);
                                        }
                                    }
                                    if (Integer.parseInt(factor.YZSX) >= zzPFFZ){
                                        goal += zzPFFZ;
                                    } else {
                                        zzPFFZ = Integer.parseInt(factor.YZSX);
                                        goal += zzPFFZ;
                                    }
                                } else {
                                    goal += Integer.parseInt(factorGoal.PFFZ);
                                }

                            }

                            ((RiskEvaluateActivity) context)
                                    .setGoalAndLevel(String.valueOf(goal));
//                            factorGoal.SELECT = true;
                        } else {
                            int p = (Integer) cb.getTag();
                            int goal = ((RiskEvaluateActivity) context)
                                    .getGoal();

                            //福建协和客户化：IENR_FXYZPF.FZXH为288,195,290,291,289的选项，选中多个时只计算一个的分数
                            //思路：实现先减后加
                            FactorGoal factorGoal = factor.YZPF.get(p);
                            if (factor.YZSX != null && factor.YZSX.length() != 0) {
                                int csPFFZ = 0;
                                for (FactorGoal item : factor.YZPF) {
                                    if (item.SELECT) {
                                        csPFFZ += Integer.parseInt(item.PFFZ);
                                    }
                                }
                                if (Integer.parseInt(factor.YZSX) >= csPFFZ) {
                                    goal -= csPFFZ;
                                } else {
                                    csPFFZ = Integer.parseInt(factor.YZSX);
                                    goal -= csPFFZ;
                                }
//                                goal = goal - csPFFZ;
                                int zzPFFZ = 0;
                                factorGoal.SELECT = false;
                                for (FactorGoal item : factor.YZPF) {
                                    if (item.SELECT) {
                                        zzPFFZ += Integer.parseInt(item.PFFZ);
                                    }
                                }
                                if (Integer.parseInt(factor.YZSX) >= zzPFFZ) {
                                    goal += zzPFFZ;
                                } else {
                                    zzPFFZ = Integer.parseInt(factor.YZSX);
                                    goal += zzPFFZ;
                                }
//                                    zzPFFZ += Integer.parseInt(factorGoal.PFFZ);

                            }else{
                                    goal -= Integer.parseInt(factorGoal.PFFZ);
                                }
//                            factorGoal.SELECT = false;
                            String fxzh=String.valueOf(factorGoal.FZXH);
                            String onlyJsOnce = "288,195,290,291,289";//194 0
                            if (onlyJsOnce.contains(fxzh)) {
                                Boolean isHave = false;
                                for (FactorGoal item : factor.YZPF) {
                                    if (item.SELECT && onlyJsOnce.contains(String.valueOf(item.FZXH))) {
                                        isHave = true;
                                        break;
                                    }
                                }
                                if (isHave) {
                                    goal += Integer.parseInt(factorGoal.PFFZ);
                                }
                            }

                            ((RiskEvaluateActivity) context)
                                    .setGoalAndLevel(String.valueOf(goal));

                        }
                    }
                });
                cb.setChecked(!EmptyTool.isBlank(factor.YZPF.get(j).MXXH));
                card.addView(cb);
            }
        }

        title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StringBuffer info = new StringBuffer();
                int pos = (Integer) title.getTag();
                for (FactorGoal goal : mList.get(pos).YZPF) {
                    String bzxx = goal.BZXX;

                    if (bzxx == null) {
                        bzxx = " ";
                    } else {
                        bzxx = bzxx + "\n";
                    }
                    info.append(goal.FZMS).append(":    ").append(bzxx).append("\n");
                }

                ((RiskEvaluateActivity) context).showInfo(info.toString());
            }
        });
        root.addView(card);
    }
}
