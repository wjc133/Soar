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

public interface ResponseDelivery {
    /**
     * 解析并分发一个网络请求或者缓存请求。
     */
    void postResponse(InnerRequest<?> request, InnerResponse<?> response);

    /**
     * 解析并分发一个网络请求或者缓存请求。并在分发完成后执行runnable内容。
     */
    void postResponse(InnerRequest<?> request, InnerResponse<?> response, Runnable runnable);

    /**
     * 向对应request分发一个网络错误。
     */
    void postError(InnerRequest<?> request, SoarError error);
}
