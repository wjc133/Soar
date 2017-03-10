package com.elite.tools.soar.toolbox;

import com.elite.tools.soar.InnerResponse;
import com.elite.tools.soar.NetworkResponse;
import com.elite.tools.soar.exception.ParseError;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

/**
 * Created by wjc133
 * DATE: 16/5/20
 * TIME: 下午3:27
 */
public class GsonRequest<T> extends JsonRequest<T> {
    private Class<T> tClz;
    private Type tType;

    public GsonRequest(int method, String url, Class<T> clz, InnerResponse.Listener<T> listener, InnerResponse.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        tClz = clz;
    }

    public GsonRequest(int method, String url, Type type, InnerResponse.Listener<T> listener, InnerResponse.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        tType = type;
    }

    @Override
    protected InnerResponse<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            T data = null;
            if (tType != null) {
                data = JsonUtils.fromJson(jsonString, tType);
            } else if (tClz != null) {
                data = JsonUtils.fromJson(jsonString, tClz);
            }
            if (data == null) {
                return null;
            }
            return InnerResponse.success(data,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return InnerResponse.error(new ParseError(e));
        }
    }

}
