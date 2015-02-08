package com.yksong.noomee.test.util;

import android.content.Context;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.io.IOUtils;
import org.mockito.internal.util.io.IOUtil;

/**
 * Created by esong on 2015-02-07.
 */
public class TestUtils {
    private static final MediaType sJSONMediaType =
            MediaType.parse("application/json; charset=utf-8");
    
    public static Response createResponse(Context context, int resId) {
        Response response = null;
        ResponseBody body = null;
        try {
            ResponseBody.create(
                    sJSONMediaType,
                    IOUtils.toByteArray(context.getResources().openRawResource(resId)));

            response = new Response.Builder()
                    .body(body)
                    .code(200)
                    .build();
        } catch (Exception e) {

        }

        return response;
    }
}
