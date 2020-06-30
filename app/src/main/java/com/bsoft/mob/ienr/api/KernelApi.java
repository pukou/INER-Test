package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.SignData;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.reflect.ReflectVo;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;

import org.json.JSONObject;

import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午2:47:35
 * @类说明
 */
public class KernelApi extends BaseApi {

    public String url;

    public KernelApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public KernelApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public static KernelApi getInstance(Context localContext) {
        // Context localContext = AppContext.getContext();
        KernelApi api = (KernelApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.kernel");
        if (api == null)
            api = (KernelApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.kernel");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 保存手写签名图片
     *
     * @param data
     * @return
     */
    public ParserModel SaveSignImage(String data) {

        String xml = null;
        if (!Constant.DEBUG_LOCAL) {
            String uri = new StringBuffer(url).append("SignImageSave")
                    .append("/").append(Constant.sysType).toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
            if (!isRequestSuccess(httpString.first)) {
                ParserModel parserModel = new ParserModel(Statue.ERROR);
                parserModel.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return parserModel;
            }
             xml = httpString.second;

        }
        if (null != xml && xml.length() > 0) {
            ParserModel model = new ParserModel();
            xml = xml.replace("\\", "");
            xml = xml.substring(1, xml.length() - 1);
            try {
                //JJSONObject ob = new JJSONObject(xml);
                JSONObject object=new JSONObject(xml);
                if (!object.isNull("Status")) {
                    if (object.getInt("Status") == 1) {
                        model.statue = Statue.SUCCESS;
                    } else {
                        model.statue = Statue.ERROR;
                        model.ExceptionMessage = object.getString("Msg");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                model.statue = Statue.PARSER_ERROR;
                return model;
            }
            return model;
        } else {
            // 网络失败
            return new ParserModel(Statue.NET_ERROR);
        }

    }

    /**
     * 获取手写签名图片
     *
     * @param hsgh
     * @param zyh
     * @param bqdm
     * @param type
     * @param gslx
     * @param qmdh
     * @return
     */
    public ParserModel GetSignImage(String hsgh, String zyh, String bqdm, String type, String gslx, String qmdh, String jgid) {

        String xml = null;
        if (!Constant.DEBUG_LOCAL) {
            String uri = new StringBuffer(url)
                    .append("GetSignImage?hsgh=").append(hsgh)
                    .append("&zyh=").append(zyh)
                    .append("&bqdm=").append(bqdm)
                    .append("&type=").append(type)
                    .append("&gslx=").append(gslx)
                    .append("&qmdh=").append(qmdh)
                    .append("&jgid=").append(jgid)
                    .toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
            if (!isRequestSuccess(httpString.first)) {
                ParserModel parserModel = new ParserModel(Statue.ERROR);
                parserModel.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return parserModel;
            }
             xml = httpString.second;
        }
        if (null != xml && xml.length() > 0) {
            return parser.parserTable(xml, new ReflectVo(SignData.class,
                    "Table1"));
        } else {
            // 网络失败
            return new ParserModel(Statue.NET_ERROR);
        }
    }

}
