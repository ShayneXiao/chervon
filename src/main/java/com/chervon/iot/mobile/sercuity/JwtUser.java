package com.chervon.iot.mobile.sercuity;

import com.chervon.iot.mobile.model.Mobile_User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * @author Jonsy
 *
 */
public class JwtUser{

	private final String id;
    private final String name;
    private final String password;
    private final String email;
    private final List<String> authorities;
    private final boolean enabled;
    private final Date lastPasswordResetDate;

    public JwtUser(Mobile_User user) {
        this.id = user.getSfdcId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        if(user.getRole() != null) {
            List<String> roles = new ArrayList(Arrays.asList(user.getRole().split(",")));
            this.authorities = roles;
        }else{
            this.authorities = null;
        }
        this.enabled = user.getEnabled();
        this.lastPasswordResetDate = user.getLastpasswordresetdate();
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public String geName() {
        return name;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }
}
