package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by tushar on 26/02/16.
 */
public class VerifMailResonse implements Serializable {
    public boolean is_mail_sent() {
        return is_mail_sent;
    }

    public void setIs_mail_sent(boolean is_mail_sent) {
        this.is_mail_sent = is_mail_sent;
    }

    private boolean is_mail_sent;
}
