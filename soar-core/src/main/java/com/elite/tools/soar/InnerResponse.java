package com.elite.tools.soar;

import com.elite.tools.soar.exception.SoarError;

/**
 * Created by wjc133
 * Date: 2016/5/20
 * Time: 0:07
 * Description:
 */
public class InnerResponse<T> {

    /**
     * Parsed response, or null in the case of error.
     */
    public final T result;
    /**
     * Cache metadata for this response, or null in the case of error.
     */
    public final Cache.Entry cacheEntry;
    /**
     * Detailed error information if <code>errorCode != OK</code>.
     */
    public final SoarError error;
    /**
     * True if this response was a soft-expired one and a second one MAY be coming.
     */
    public boolean intermediate = false;

    private InnerResponse(T result, Cache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
    }

    private InnerResponse(SoarError error) {
        this.result = null;
        this.cacheEntry = null;
        this.error = error;
    }

    /** Returns a successful response containing the parsed result. */
    public static <T> InnerResponse<T> success(T result, Cache.Entry cacheEntry) {
        return new InnerResponse<T>(result, cacheEntry);
    }

    /**
     * Returns a failed response containing the given error code and an optional
     * localized message displayed to the user.
     */
    public static <T> InnerResponse<T> error(SoarError error) {
        return new InnerResponse<T>(error);
    }

    /**
     * Returns whether this response is considered successful.
     */
    public boolean isSuccess() {
        return error == null;
    }


    /**
     * Callback interface for delivering parsed responses.
     */
    public interface Listener<T> {
        /** Called when a response is received. */
        public void onResponse(T response);
    }

    /**
     * Callback interface for delivering error responses.
     */
    public static interface ErrorListener {
        /**
         * Callback method that an error has been occurred with the
         * provided error code and optional user-readable message.
         */
        public void onErrorResponse(SoarError error);
    }
}