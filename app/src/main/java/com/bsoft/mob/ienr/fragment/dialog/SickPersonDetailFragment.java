package com.bsoft.mob.ienr.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.expense.ExpenseTotal;
import com.bsoft.mob.ienr.model.kernel.SickPersonDetailVo;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.LinkedList;
@Deprecated
public class SickPersonDetailFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SickPersonDetailVo person;

    private ExpenseTotal total;

    public SickPersonDetailFragment() {
        // Required empty public constructor
    }
    public static SickPersonDetailFragment newInstance(SickPersonDetailVo person,
                                                       ExpenseTotal expenseTotal) {
        SickPersonDetailFragment fragment = new SickPersonDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, person);
        args.putSerializable(ARG_PARAM2, expenseTotal);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            person = (SickPersonDetailVo) getArguments().getSerializable(ARG_PARAM1);
            total = (ExpenseTotal) getArguments().getSerializable(ARG_PARAM2);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LinkedList<String> list = new LinkedList<String>();
        if (person != null) {

            String brxm = "病人姓名：" + person.BRXM;
            list.add(brxm);

            String zyh = "住院号：" + person.ZYH;
            list.add(zyh);

            String zyhm = "住院号码：" + person.ZYHM;
            list.add(zyhm);

            String brxb = "病人性别：" + (person.BRXB == 1 ? "男" : "女");
            list.add(brxb);

            String ryrqStr = person.RYRQ;
            if (!EmptyTool.isBlank(ryrqStr)) {
                String ryrq = "入院日期：" + ryrqStr.split("T")[0];
                list.add(ryrq);
            }

            String brch = "病人床号：" + person.BRCH;
            list.add(brch);

            if (!EmptyTool.isBlank(person.KSMC)) {
                String ksmc = "病人科室：" + person.KSMC;
                list.add(ksmc);
            }

            if (!EmptyTool.isBlank(person.YSMC)) {
                String ysmc = "主治医生：" + person.YSMC;
                list.add(ysmc);
            }

            if (!EmptyTool.isBlank(person.XZMC)) {
                String xzmc = "费用性质：" + person.XZMC;
                list.add(xzmc);
            }

            if (total != null) {
                String zjje = "总费用:" + total.ZJJE;
                list.add(zjje);

                String zfje = ("自负金额:" + total.ZFJE);
                list.add(zfje);

                String jkje = ("交款金额:" + total.JKJE);
                list.add(jkje);

                String fyye = ("费用余额:" + total.FYYE);
                list.add(fyye);
            }

        }
        String[] array = list.toArray(new String[list.size()]);
        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(),"病人详情")).setItems(array, null);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();

    }
}
