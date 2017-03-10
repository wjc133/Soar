package com.elite.tools.soar.exception;

import com.elite.tools.soar.NetworkResponse;

/**
 * Indicates that there was a redirection.
 */
public class RedirectError extends SoarError {

    public RedirectError() {
    }

    public RedirectError(final Throwable cause) {
        super(cause);
    }

    public RedirectError(final NetworkResponse response) {
        super(response);
    }
}
