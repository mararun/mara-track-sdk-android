package com.mararun.runservice.sample.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mararun.runservice.sample.model.RunDetail;
import com.mararun.runservice.util.MaraLogger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mararun
 */

public class FsRunDetailAccessHelper extends RunDetailAccessHelper {
    @Override
    public long save(Context context, String rawJson) {
        RunDetail runDetail = fromJson(rawJson);
        if (runDetail == null) {
            return INVALID_RUN_DETAIL_ID;
        }

        long runId = runDetail.getStartTime();

        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(String.valueOf(runId), Context.MODE_PRIVATE);
            outputStream.write(rawJson.getBytes());
            outputStream.close();
        } catch (IOException e) {
            MaraLogger.e(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException e) {
                    MaraLogger.e(e);
                }
            }
        }

        return runId;
    }

    @Override
    public RunDetail load(Context context, long id) {
        String data = "";
        FileInputStream inputStream = null;
        try {
            inputStream = context.openFileInput(String.valueOf(id));
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            data = new String(buffer, "UTF-8");
        } catch (IOException e) {
            MaraLogger.e(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {
                    MaraLogger.e(e);
                }
            }
        }

        return fromJson(data);
    }
}
