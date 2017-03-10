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

/**
 * Default retry policy for requests.
 */
public class DefaultRetryPolicy implements RetryPolicy {
    /** 当前超时时间 */
    private int mCurrentTimeoutMs;

    /** 当前重试次数 */
    private int mCurrentRetryCount;

    /** 最大重试次数 */
    private final int mMaxNumRetries;

    /** 策略的退避因数，用于控制重试的时间间隔 */
    private final float mBackoffMultiplier;

    /** 默认的Socket超时时间 */
    public static final int DEFAULT_TIMEOUT_MS = 2500;

    /** 默认最大重试次数 */
    public static final int DEFAULT_MAX_RETRIES = 0;

    /** 默认退避因数 */
    public static final float DEFAULT_BACKOFF_MULT = 1f;


    /**
     * 全部使用默认timeout构造
     */
    public DefaultRetryPolicy() {
        this(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
    }

    /**
     * 构造一个新的retry policy实例。
     * @param initialTimeoutMs 初始超时时间
     * @param maxNumRetries 最大重试次数
     * @param backoffMultiplier 退避因数
     */
    public DefaultRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
        mCurrentTimeoutMs = initialTimeoutMs;
        mMaxNumRetries = maxNumRetries;
        mBackoffMultiplier = backoffMultiplier;
    }

    /**
     * 返回当前超时时间
     */
    @Override
    public int getCurrentTimeout() {
        return mCurrentTimeoutMs;
    }

    /**
     * 返回当前重试次数
     */
    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    /**
     * 返回退避因数
     */
    public float getBackoffMultiplier() {
        return mBackoffMultiplier;
    }

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     * @param error 上次尝试抛出的异常
     */
    @Override
    public void retry(SoarError error) throws SoarError {
        mCurrentRetryCount++;
        mCurrentTimeoutMs += (mCurrentTimeoutMs * mBackoffMultiplier);
        if (!hasAttemptRemaining()) {
            throw error;
        }
    }

    /**
     * 返回是否还有剩余尝试次数
     */
    protected boolean hasAttemptRemaining() {
        return mCurrentRetryCount <= mMaxNumRetries;
    }
}
