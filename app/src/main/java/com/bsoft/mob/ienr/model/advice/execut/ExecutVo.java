package com.bsoft.mob.ienr.model.advice.execut;

import android.content.Context;
import android.text.TextUtils;

import com.bsoft.mob.ienr.model.BaseVo;
import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.view.BSToast;
import com.bsoft.mob.ienr.util.VibratorUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:42:42
 * @类说明 执行返回结果
 */
public class ExecutVo extends BaseVo {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // 状态
    public int statue = Statue.ERROR;
    public String ExceptionMessage;
    //
    public String LogicMsg;

    /**
     * 返回类型状态
     */
    public enum ExecutType {
        /**
         * 显示消息
         */
        SHOW,
        /**
         * 错误
         */
        ERROR,
        CORE,
        /**
         * 只要显示就好了
         */
        RE,
        /**
         * 时间限制
         */
        SJ,
        /**
         * 带表多包药执行
         */
        KF,
        KFQX,
        /**
         * 输液暂停
         */
        SYZT,
        SYQX,
        SYJS,
        /**
         * 多瓶输液
         */
        SY,
        ZSQX,
        /**
         * 需要双签名
         */
        SQ;
    }

    public ExecutType executType;

    @SuppressWarnings("rawtypes")
    public ArrayList list = new ArrayList();

    public ExecutVo() {

    }

    public ExecutVo(int statue) {
        this.statue = statue;
    }

    public void add(Object vo) {
        list.add(vo);
    }

    public int size() {
        return list.size();
    }

    public Object get(int index) {
        return list.get(index);
    }
    public ArrayList get() {
        return list;
    }
    public void setIsFalse(String IsFalse) {
        if (null == IsFalse || "false".equals(IsFalse)) {
            this.statue = Statue.SUCCESS;
        } else {
            //"true".equals(IsFalse)
            this.statue = Statue.ERROR;
        }
    }

    public boolean isOK() {
        return this.statue == Statue.SUCCESS;
    }

    public void showToast(Context context) {

        boolean vib = SettingUtils.isVib(context);
        VibratorUtil.vibrator(context,vib);
        switch (statue) {

            case Statue.NET_ERROR:
                BSToast.showToast(context, "网络加载失败", BSToast.LENGTH_SHORT);
                break;
            case Statue.ERROR:
                BSToast.showToast(context, null != ExceptionMessage
                        && ExceptionMessage.length() > 0 ? ExceptionMessage
                        : "请求失败", BSToast.LENGTH_SHORT);
                break;
            case Statue.PARSER_ERROR:
                BSToast.showToast(context, "解析失败", BSToast.LENGTH_SHORT);
                break;
            case Statue.NO_Chose:
                BSToast.showToast(context, "没有选择", BSToast.LENGTH_SHORT);
                break;
            case Statue.SHOW_MSG:
                if (!TextUtils.isEmpty(LogicMsg)) {
                    BSToast.showToast(context, LogicMsg, BSToast.LENGTH_SHORT);
                }
                break;
            default:
                BSToast.showToast(context, "失败", BSToast.LENGTH_SHORT);
                break;
        }
    }

    public InArgument inArgument;
    public SelectResult selectResult;
    public int selectResultCode;
}
