package com.bsoft.mob.ienr.fragment.dialog;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.listener.DialogContentClickListener;

/**
 * Created by Classichu on 2018/1/8.
 */

public class TryAltDialogFragment extends DialogFragment

{

    private boolean tryAgain;
    private static DialogContentClickListener listener;

    public static TryAltDialogFragment newInstance(boolean tryAgain,DialogContentClickListener list) {
        TryAltDialogFragment fragment = new TryAltDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("tryAgain", tryAgain);
        fragment.setArguments(args);
        //
        listener = list;
        return fragment;
    }

    public TryAltDialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tryAgain = getArguments().getBoolean("tryAgain");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (tryAgain) {
            builder.setMessage("网络请求失败,是否重新执行？");
        } else {
            builder.setMessage("数据已更改，是否保存当前数据？");
        }

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        if (listener != null) {
                            listener.contentClick();
                        }

                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.cancelClick();
                }

            }
        })
                //.setTitle(getString(R.string.project_tips))
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getActivity(), getString(R.string.project_tips)));

        AlertDialog dialog = builder.create();
        return dialog;

    }
}