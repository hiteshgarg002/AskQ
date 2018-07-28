package com.hrrock.askq.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp-u on 2/20/2018.
 */

public class UserDetailsModel {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("name")
    private String name;
    @SerializedName("mobile")
    private String mobile;
    @SerializedName("username")
    private String username;
    @SerializedName("profile")
    private String profile;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile() {
        return profile;
    }
}
