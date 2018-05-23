package com.exp.demo.pojo;

import java.io.*;

public class SdUser implements Serializable
{
    private String userId;
    private String userName;
    private String password;
    private static final long serialVersionUID = 1L;
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = ((userId == null) ? null : userId.trim());
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = ((userName == null) ? null : userName.trim());
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = ((password == null) ? null : password.trim());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(this.hashCode());
        sb.append(", userId=").append(this.userId);
        sb.append(", userName=").append(this.userName);
        sb.append(", password=").append(this.password);
        sb.append(", serialVersionUID=").append(1L);
        sb.append("]");
        return sb.toString();
    }
}
