package com.mararun.runservice.sample.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mararun.runservice.sample.model.RunDetail;
import com.mararun.runservice.util.MaraLogger;

/**
 * Created by mararun
 */

public abstract class RunDetailAccessHelper {
    public static final long INVALID_RUN_DETAIL_ID = -1;

    /**
     * 保存跑步信息
     * @param context context
     * @param runDetailJson 详细信息json
     * @return 本地id
     */
    public abstract long save(Context context, String runDetailJson);

    /**
     * 获取跑步信息
     * @param context context
     * @param id 本地id
     * @return 跑步信息
     */
    public abstract RunDetail load(Context context, long id);

    protected RunDetail fromJson(String rawJson) {
        RunDetail runDetail = null;
        try {
            runDetail = new Gson().fromJson(rawJson, RunDetail.class);
        }
        catch (JsonSyntaxException e) {
            MaraLogger.e("deserialize run detail ex:" + e.getMessage());
        }

        return runDetail;
    }
}
