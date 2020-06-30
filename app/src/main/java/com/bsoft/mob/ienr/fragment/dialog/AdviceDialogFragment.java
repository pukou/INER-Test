package com.bsoft.mob.ienr.fragment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.AdviceListActivity;
import com.bsoft.mob.ienr.activity.user.TransfusionListActivity;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.advice.AdviceUtils;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;
import com.bsoft.mob.ienr.model.advice.execut.REModel;
import com.bsoft.mob.ienr.model.advice.execut.SJModel;
import com.bsoft.mob.ienr.model.advice.execut.SQModel;
import com.bsoft.mob.ienr.model.advice.execut.SYZTModel;
import com.bsoft.mob.ienr.util.StringUtil;


/**
 * Created by classichu on 2018/3/9.
 */

public class AdviceDialogFragment extends DialogFragment {

    public static final String TAG = "AdviceDialogFragment";

    protected static ExecutVo result;
    private static final byte QRT_RESULT_DIALG = 1;
    /**
     * 更多对话框
     */
    public static final byte QRT_MENU_DIALG = 0;

    /**
     * 拒绝列表对话框
     */
    public static final byte QRT_REFUSE_DIALG = 2;

    /**
     * 手动执行对话框
     */
    public static final byte QRT_ACTION_DIALG = 3;

    private static byte mType = QRT_MENU_DIALG;
    private static String[] reson_more;
    private static OpearListener mOpearListener;


    public interface  OpearListener{
        void performExcuteEnd(String QRDH, String s);
        void performExcuteContinue(String QRDH, String s);
        void performExcuteRefuse(String itemStr);
        void actionDoHandleExecueEx(boolean flag);
        void actionDoScanExecueEx(boolean flag);
        void refreshData();

    }
    public static AdviceDialogFragment newInstance(ExecutVo resultExecutVo,String[] resonMore,OpearListener opearListener) {
        AdviceDialogFragment fragment = new AdviceDialogFragment();
        result = resultExecutVo;
        mType = QRT_RESULT_DIALG;
        mOpearListener = opearListener;
        reson_more = resonMore;
        return fragment;
    }

    public static AdviceDialogFragment newInstance(byte type, String title,String[] resonMore,OpearListener opearListener, String... items) {
        AdviceDialogFragment fragment = new AdviceDialogFragment();
        mType = type;
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putStringArray("items", items);
        fragment.setArguments(bundle);
        mOpearListener = opearListener;
        reson_more = resonMore;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        switch (mType) {
            case QRT_MENU_DIALG:

                String title = getArguments().getString("title");
                String[] items = getArguments().getStringArray("items");

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getActivity());
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), title));

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Intent intent2 = new Intent(getActivity(),
                                    AdviceListActivity.class);
                            startActivity(intent2);
                        } else if (item == 1) {
                            Intent intent1 = new Intent(getActivity(),
                                    TransfusionListActivity.class);
                            startActivity(intent1);
                        }
                    }
                });
                return builder.create();

            case QRT_REFUSE_DIALG:

                title = getArguments().getString("title");
                items = getArguments().getStringArray("items");

                builder = new AlertDialog.Builder(getActivity());
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), title));
                builder.setItems(reson_more,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                final int item) {

                                if (mOpearListener!=null){
                                    mOpearListener.performExcuteRefuse(String.valueOf(item));
                                }
                               /* performExcute(DrugsAdviceFragment.ExecutTask.REFUSE_EXCUTE,
                                        String.valueOf(item));*/

                            }
                        });

                return builder.create();
            case QRT_ACTION_DIALG:

                items = getArguments().getStringArray("items");
                if (items == null || items.length < 1) {
                    return null;
                }
                String tag = items[0];
                title = getArguments().getString("title");
                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确定要" + tag + "吗?");
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), title));
                builder.setPositiveButton(android.R.string.ok,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                        /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
                                if (mOpearListener!=null){
                                    mOpearListener.actionDoHandleExecueEx(false);
                                }
                                //actionDoHandleExecue(false);
                                    /* =============================================================== end */
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                return builder.create();
            case QRT_RESULT_DIALG:
                return getResultDialog(result);
            default:
        }

        return super.onCreateDialog(savedInstanceState);
    }

    private Dialog getResultDialog(final ExecutVo result) {

        //boolean vib = mAppApplication.getSettingConfig().vib;

        if (null == result) {
            //VibratorUtil.vibrator(getActivity(), vib);
            return show(getActivity(), getString(R.string.project_tips), "失败");
        }

        if (!result.isOK()) {
            //VibratorUtil.vibrator(getActivity(), vib);
            return showAlert(getActivity());
        }

        // 成功

        AlertDialog.Builder builder = null;
        StringBuffer buf = null;
        switch (result.executType) {
            case RE:
                buf = new StringBuffer();
                for (int i = 0; i < result.size(); i++) {
                    REModel vo = (REModel) result.get(i);
                    System.out.println("vo.YCLX  :  " + vo.YCLX);
                    if (vo.YCLX != 0) {
                        buf.append(
                                StringUtil.getStringLength(vo.YZMC == null ? ""
                                        : vo.YZMC, 11))
                                .append("\n提示：")
                                .append(StringUtil
                                        .getUnEmptText(vo.YCXX == null ? AdviceUtils
                                                .getYCXXString(vo.YCLX)
                                                : vo.YCXX)).append("\n");
                    }
                }
                if (buf.length() > 0) {
                    android.content.DialogInterface.OnClickListener listener = new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // 确认之后再刷新
                           // toRefreshData();
                            if (mOpearListener!=null){
                                mOpearListener.refreshData();
                            }
                        }
                    };
                    return new AlertDialog.Builder(getActivity())
                            //  .setTitle(getString(R.string.project_tips))
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), getString(R.string.project_tips)))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(buf.toString())
                            .setPositiveButton(android.R.string.ok, listener)
                            .create();

                }

                return null;
            case SJ:
                buf = new StringBuffer();
                for (int i = 0; i < result.list.size(); i++) {
                    SJModel vo = (SJModel) result.get(i);
                    buf.append(vo.MES).append("\n");
                }
                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(buf.toString());
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), getString(R.string.project_tips)));
                builder.setPositiveButton(android.R.string.ok,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                                            /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
                                    if (mOpearListener!=null){
                                        mOpearListener.actionDoScanExecueEx(false);
                                    }
                               /* if (null != barinfo) {
                                    actionDoScanExecue(barinfo, false);
                                }*/
                                        /* =============================================================== end */

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            // break;
            case SYZT:
                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("输液处于暂停状态，需要执行继续还是结束?");
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getContext(), getString(R.string.project_tips)));
                builder.setPositiveButton("继续",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (mOpearListener!=null){
                                    if (!result.list.isEmpty()) {
                                        mOpearListener.performExcuteContinue(((SYZTModel) result.get(0)).QRDH,
                                                Boolean.toString(false));
                                    }
                                }
                              /*  performExcute(
                                        DrugsAdviceFragment.ExecutTask.Transfuse_Continue_EXCUTE,
                                        ((SYZTModel) result.get(0)).QRDH,
                                        Boolean.toString(false));*/
                            }
                        });
                builder.setNegativeButton("结束",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (mOpearListener!=null){
                                    if (!result.list.isEmpty()) {
                                        mOpearListener.performExcuteEnd(((SYZTModel) result.get(0)).QRDH,
                                                Boolean.toString(true));
                                    }
                                }
                              /*  performExcute(
                                        DrugsAdviceFragment.ExecutTask.Transfuse_Continue_EXCUTE,
                                        ((SYZTModel) result.get(0)).QRDH,
                                        Boolean.toString(true));*/
                            }
                        });
                return builder.create();
            case SQ:

                buf = new StringBuffer();
                for (int i = 0; i < result.list.size(); i++) {
                    SQModel vo = (SQModel) result.get(i);
                    buf.append(vo.MES).append("\n");
                }
                return show(getActivity(), getString(R.string.project_tips), buf.toString());
            // break;
            default:
                break;
        }

        return null;
    }

    public Dialog showAlert(Context context) {

        switch (result.statue) {
            case Statue.NET_ERROR:
                return showError(context, "网络加载失败");
            // break;
            case Statue.ERROR:
                return showError(
                        context,
                        null != result.ExceptionMessage
                                && result.ExceptionMessage.length() > 0 ? result.ExceptionMessage
                                : "请求失败");

            case Statue.PARSER_ERROR:
                return showError(context, "解析失败");

            case Statue.NO_Chose:
                return showError(context, "没有选择");
            default:
                return showError(context, "失败");

        }
    }

    public Dialog showError(Context context, String msg) {

      //  boolean vib = mAppApplication.getSettingConfig().vib;
      //  VibratorUtil.vibrator(getActivity(), vib);
        return show(context, getString(R.string.project_tips), msg);
    }

    public Dialog show(Context context, String title, String msg) {
        View txt = ViewBuildHelper.buildDialogTitleTextView(getContext(), title);
        return new AlertDialog.Builder(context)
                // .setTitle(title)
                .setCustomTitle(txt)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null).create();
    }

}