package com.elite.tools.soar;

public class SoarError extends Exception {
    public final NetworkResponse networkResponse;
    private long networkTimeMs;

    public SoarError() {
        networkResponse = null;
    }

    public SoarError(NetworkResponse response) {
        networkResponse = response;
    }

    public SoarError(String exceptionMessage) {
       super(exceptionMessage);
       networkResponse = null;
    }

    public SoarError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
        networkResponse = null;
    }

    public SoarError(Throwable cause) {
        super(cause);
        networkResponse = null;
    }

    /* package */ void setNetworkTimeMs(long networkTimeMs) {
       this.networkTimeMs = networkTimeMs;
    }

    public long getNetworkTimeMs() {
       return networkTimeMs;
    }
}