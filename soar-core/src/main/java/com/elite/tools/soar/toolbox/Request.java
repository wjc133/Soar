package com.elite.tools.soar.toolbox;

import com.elite.tools.soar.InnerRequest;
import com.elite.tools.soar.InnerResponse;
import com.elite.tools.soar.NetworkResponse;
import com.elite.tools.soar.RequestQueue;
import com.elite.tools.soar.exception.AuthFailureError;
import com.elite.tools.soar.exception.SoarError;
import com.elite.tools.soar.utils.ParamUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wjc133
 * Date: 2017/3/10
 * Time: 16:01
 */
public class Request {
    private static final Logger LOGGER = LoggerFactory.getLogger(Request.class);
    private final RequestQueue queue;
    private static final long DEFAULT_TIMEOUT = 5000;

    public Request(RequestQueue queue) {
        this.queue = queue;
    }

    public interface Callback {
        void onResponse(Response response);
    }

    public Response get(String url) throws SoarError {
        return this.get(url, (Map<String, String>) null);
    }

    public Response get(String url, Map<String, String> params) throws SoarError {
        return this.get(url, params, (Map<String, String>) null);
    }

    public Response get(String url, Map<String, String> params, final Map<String, String> headers) throws SoarError {
        if (params != null && params.size() > 0) {
            url = url + ParamUtils.encodeParameters(params, "UTF-8");
        }
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(InnerRequest.Method.GET, url, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (headers == null || headers.size() == 0) {
                    return super.getHeaders();
                }
                return headers;
            }
        };
        queue.add(request);
        try {
            String content = future.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            Response resp = new Response();
            resp.setContent(content);
            return resp;
        } catch (Exception e) {
            LOGGER.error("get response from future timeout", e);
            throw new SoarError("get response from future timeout", e);
        }
    }

    public void get(String url, Callback callback) {
        this.get(url, null, callback);
    }

    public void get(String url, Map<String, String> params, Callback callback) {
        this.get(url, null, null, callback);
    }

    public void get(String url, Map<String, String> params, Map<String, String> headers, Callback callback) {
        if (params != null && params.size() > 0) {
            url = url + ParamUtils.encodeParameters(params, "UTF-8");
        }
        StringRequest request = new StringRequest(url, new InnerResponse.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Response resp = new Response();
                resp.setContent(response);
            }
        }, new InnerResponse.ErrorListener() {
            @Override
            public void onErrorResponse(SoarError error) {

            }
        });
        this.queue.add(request);
    }

    public Response post(String url) {
        return null;
    }

    public Response post(String url, Map<String, String> params) {
        return null;
    }

    public Response post(String url, Map<String, String> params, Map<String, String> headers) {
        return null;
    }

    public void post(String url, Callback callback) {

    }

    public void post(String url, Map<String, String> params, Callback callback) {

    }

    public void post(String url, Map<String, String> params, Map<String, String> headers, Callback callback) {

    }
}
