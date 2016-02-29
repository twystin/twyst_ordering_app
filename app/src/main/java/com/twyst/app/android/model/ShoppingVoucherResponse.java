package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by tushar on 26/02/16.
 */
public class ShoppingVoucherResponse implements Serializable {

    private boolean emailVerified;

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEmailVerifiedThresholdReached() {
        return emailVerifiedThresholdReached;
    }

    public void setEmailVerifiedThresholdReached(boolean emailVerifiedThresholdReached) {
        this.emailVerifiedThresholdReached = emailVerifiedThresholdReached;
    }

    private boolean emailVerifiedThresholdReached;
}
