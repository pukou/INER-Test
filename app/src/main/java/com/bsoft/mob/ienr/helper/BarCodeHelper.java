package com.bsoft.mob.ienr.helper;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;

public class BarCodeHelper {
    public static void testBarcode(Context context) {
        final EditText editText = new EditText(context);
        String bar = context.getSharedPreferences("testbarcode", Context.MODE_PRIVATE).getString("bar", "");
        editText.setText(bar);
        new AlertDialog.Builder(context).setMessage("输入条码").setView(editText).setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        String barCodeStr = editText.getText().toString().trim();
                        context.getSharedPreferences("testbarcode", Context.MODE_PRIVATE).edit().putString("bar", barCodeStr).apply();
                        Intent intent = new Intent(context, AnalyseCodeService.class);
                        intent.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barCodeStr);
                        context.startService(intent);
                    }
                }).create().show();

    }
}
