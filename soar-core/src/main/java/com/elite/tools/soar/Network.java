package com.elite.tools.soar;

/**
 * Created by wjc133
 * Date: 2016/5/20
 * Time: 0:10
 * Description: An interface for performing requests.
 */
public interface Network {
    /**
     * Performs the specified request.
     * @param request Request to process
     * @return A {@link NetworkResponse} with data and caching metadata; will never be null
     * @throws SoarError on errors
     */
    NetworkResponse performRequest(Request<?> request) throws SoarError;
}
