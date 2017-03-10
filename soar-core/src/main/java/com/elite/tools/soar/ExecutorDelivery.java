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

import java.util.concurrent.Executor;

/**
 * 响应分发器，用于分发响应值和错误。
 */
public class ExecutorDelivery implements ResponseDelivery {
    /** 用于分发响应值 */
    private final Executor responsePoster;

    /**
     * 构造方法，创建一个新的ExecutorDelivery实例。
     */
    public ExecutorDelivery() {
        // 初始化一个Executor，仅用于简单执行command
        responsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }

    /**
     * 使用指定的Executor创建实例
     * @param executor 用于执行分发任务的Executor
     */
    public ExecutorDelivery(Executor executor) {
        responsePoster = executor;
    }

    @Override
    public void postResponse(InnerRequest<?> request, InnerResponse<?> response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(InnerRequest<?> request, InnerResponse<?> response, Runnable runnable) {
        request.markDelivered();
        responsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(InnerRequest<?> request, SoarError error) {
        InnerResponse<?> response = InnerResponse.error(error);
        responsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    @SuppressWarnings("rawtypes")
    private class ResponseDeliveryRunnable implements Runnable {
        private final InnerRequest request;
        private final InnerResponse response;
        private final Runnable runnable;

        public ResponseDeliveryRunnable(InnerRequest request, InnerResponse response, Runnable runnable) {
            this.request = request;
            this.response = response;
            this.runnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // 如果请求已经被取消了，打上个标记，不分发了。
            if (request.isCanceled()) {
                request.finish("canceled-at-delivery");
                return;
            }

            // 根据响应值是否成功决定分发响应值还是错误
            if (response.isSuccess()) {
                request.deliverResponse(response.result);
            } else {
                request.deliverError(response.error);
            }

            // 如果响应值是一个中间值，则应该做标记；否则就是正常返回
            if (response.intermediate) {
            } else {
                request.finish("done");
            }

            // 分发后执行的动作
            if (runnable != null) {
                runnable.run();
            }
       }
    }
}
