package com.chervon.iot.mobile.model;

import org.springframework.stereotype.Component;

/**
 * Created by 喷水君 on 2017/7/19.
 */
@Component
public class Sfdc_Request {
    private  String name;
    private  String firstname;
    private String lastname;
    private  String email;
    private  String password;
    private  String status;

    public Sfdc_Request() {
    }

    public Sfdc_Request(String name, String firstname, String lastname, String email, String password, String status) {

        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
