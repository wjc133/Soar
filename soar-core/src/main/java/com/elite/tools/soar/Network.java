package com.elite.tools.soar;

/**
 * Created by wjc133
 * Date: 2016/5/20
 * Time: 0:10
 * Description: 请求处理中心接口.
 */
public interface Network {
    /**
     * 处理执行请求.
     * @param request 请求实例
     * @return 一个带有响应数据和缓存元数据的 {@link NetworkResponse}实例；永不为null
     * @throws SoarError 网络异常
     */
    NetworkResponse performRequest(Request<?> request) throws SoarError;
}
