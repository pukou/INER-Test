package com.bsoft.mob.ienr.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

public class DynamicUiUtil {

    /**
     * 设置最大值和最小值hint ，以及输入错误提示
     *
     * @param min
     * @param max
     * @param edit
     * @return
     */
    public static EditText setMaxMinValue(final String min, final String max,
                                          final EditText edit, final String extrMin, final String extrMax) {

        if (edit == null) {
            return edit;
        }

        final boolean notEmpty = !EmptyTool.isBlank(min)
                && !EmptyTool.isBlank(max);
        // 设置Hint和提示
        if (notEmpty && !min.equals(max)) {

            edit.setHint(min + "-" + max);

            edit.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!EmptyTool.isBlank(s.toString())) {
                        int result = compareValue(max, min, s.toString());
                        if (result > 0) {
                            boolean isExtrMax = isExtrMax(extrMax, s.toString(), extrMin);
                            String errorStr = "输入数值超出正常值上限";
                            if (isExtrMax) {
                                errorStr = "超出有效值上限,重新输入";
                            }
                            edit.setError(errorStr);
                        } else if (result < 0) {
                            boolean isExtrMin = isExtrMin(extrMin, s.toString(), extrMax);
                            String errorStr = "输入数值超出正常值下限";
                            if (isExtrMin) {
                                errorStr = "超出有效值下限,重新输入";
                            }
                            edit.setError(errorStr);
                        }
                    }
                }

            });


            edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        String str = ((EditText) v).getText().toString();
                        if (!EmptyTool.isBlank(str)) {
                            boolean isExtrMax = isExtrMax(extrMax, str, extrMin);
                            boolean isExtrMin = isExtrMin(extrMin, str, extrMax);
                            if (isExtrMax || isExtrMin) {
                                ((EditText) v).setText(null);
                            }
                        }
                    }
                }
            });
        }

        return edit;

    }

    public static boolean isExtrMin(String extrMin, String s, String extrMax) {

        if (!EmptyTool.isBlank(extrMin) && !extrMin.equals(extrMax)) {
            float min = Float.valueOf(extrMin);
            float valuef = Float.valueOf(s.toString());
            if (valuef < min) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExtrMax(String extrMax, String s, String extrMin) {

        if (!EmptyTool.isBlank(extrMax) && !extrMax.equals(extrMin)) {
            float max = Float.valueOf(extrMax);
            float valuef = Float.valueOf(s.toString());
            if (valuef > max) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较数值是否在范围内
     *
     * @param max
     * @param min
     * @param value
     * @return 小于最小值返回-1，在范围内返回0，大于最值返回1
     */
    public static int compareValue(String max, String min, String value) {

        try {
            float maxf = Float.valueOf(max);
            float minf = Float.valueOf(min);
            float valuef = Float.valueOf(value);

            if (valuef >= minf && valuef <= maxf) {
                return 0;
            } else if (valuef < minf) {
                return -1;
            } else {
                return 1;
            }
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }

        return 0;

    }
}
