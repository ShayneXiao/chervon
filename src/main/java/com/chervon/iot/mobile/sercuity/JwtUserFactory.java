package com.chervon.iot.mobile.sercuity;

import com.chervon.iot.mobile.model.Mobile_User;

/**
 * @author Jonsy
 *
 */
public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(Mobile_User user) {
        return new JwtUser(user);
    }
}
