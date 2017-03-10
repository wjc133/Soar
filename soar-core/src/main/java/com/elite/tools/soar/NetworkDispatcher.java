/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elite.tools.soar;

import com.elite.tools.soar.exception.SoarError;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 网络调度器
 * 提供一个从请求队列执行网络请求的线程
 * 被添加到指定队列中的请求会被特定的{@link Network}处理。如果需要，响应值会通过指定的{@link Cache}
 * 提交给缓存。有效的响应和错误都会通过{@link ResponseDelivery}返回给调用方。
 */
public class NetworkDispatcher extends Thread {
    /**
     * 需要被处理请求的队列
     */
    private final BlockingQueue<InnerRequest<?>> mQueue;
    /**
     * 用来处理请求的Network实例
     */
    private final Network mNetwork;
    /**
     * 用来写入缓存的实例
     */
    private final Cache mCache;
    /**
     * 用于分发响应值和错误
     */
    private final ResponseDelivery mDelivery;
    /**
     * 用于告知对象已停止运行
     */
    private volatile boolean mQuit = false;

    private static final Logger LOG = LoggerFactory.getLogger(NetworkDispatcher.class);

    /**
     * 创建一个新的网络调度器线程。你必须调用{@link #start()}以启动线程
     *
     * @param queue    用于分流的请求对象队列
     * @param network  用于执行请求的Network对象
     * @param cache    用于写入响应值的缓存
     * @param delivery 用于返回响应值或错误
     */
    public NetworkDispatcher(BlockingQueue<InnerRequest<?>> queue,
                             Network network, Cache cache,
                             ResponseDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     * 用于强制定制调度器。如果请求队列中仍有请求，这些请求将不能被
     * 继续处理。
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        InnerRequest<?> request;
        while (true) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            // release previous request object to avoid leaking request object when mQueue is drained.
            // 释放前一个请求，以避免在queue崩溃时泄漏。
            request = null;
            try {
                // 从队列中取出一个请求
                request = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                // 如果请求已经被取消，则不再执行该请求
                if (request.isCanceled()) {
                    request.finish("network-discard-cancelled");
                    continue;
                }

                // 执行网络请求
                NetworkResponse networkResponse = mNetwork.performRequest(request);

                // 如果服务器返回的是304，说明当前请求是一个条件验证请求。
                // 如果我们已经分发过响应值，就不应该再次分发重复的数据。
                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("not-modified");
                    continue;
                }

                // 解析响应值
                InnerResponse<?> response = request.parseNetworkResponse(networkResponse);

                // 写入缓存
                // TODO: 304时，应该只缓存元数据metadata，而不是整个返回值数据
                if (request.shouldCache() && response.cacheEntry != null) {
                    mCache.put(request.getCacheKey(), response.cacheEntry);
                }

                // 标记已分发，并返回响应
                request.markDelivered();
                mDelivery.postResponse(request, response);
            } catch (SoarError soarError) {
                //异常时，解析异常并分发错误
                soarError.setNetworkTimeMs(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
                parseAndDeliverNetworkError(request, soarError);
            } catch (Exception e) {
                //非框架定义的错误无法解析，所以包装成SoarError直接返回
                LOG.error("Unhandled exception {}", e);
                SoarError soarError = new SoarError(e);
                soarError.setNetworkTimeMs(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
                mDelivery.postError(request, soarError);
            } finally {
                if (stopwatch.isRunning()) {
                    stopwatch.stop();
                }
            }
        }
    }

    private void parseAndDeliverNetworkError(InnerRequest<?> request, SoarError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
