package com.bsoft.mob.ienr.api;

import android.content.Context;
import com.bsoft.mob.ienr.http.AppHttpClient;

public class OffLineApi extends BaseApi {
    public String url;

    public OffLineApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public OffLineApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static OffLineApi getInstance(Context mContext) {
        OffLineApi api = (OffLineApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.OffLineApi");
        if (api == null)
            api = (OffLineApi) mContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.OffLineApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

}
