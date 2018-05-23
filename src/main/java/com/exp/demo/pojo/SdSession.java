package com.exp.demo.pojo;

import java.io.*;

public class SdSession implements Serializable
{
    private String userId;
    private String sessionId;
    private String phone;
    private String loginPassword;
    private static final long serialVersionUID = 1L;
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = ((userId == null) ? null : userId.trim());
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final String sessionId) {
        this.sessionId = ((sessionId == null) ? null : sessionId.trim());
    }
    
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(final String phone) {
        this.phone = ((phone == null) ? null : phone.trim());
    }
    
    public String getLoginPassword() {
        return this.loginPassword;
    }
    
    public void setLoginPassword(final String loginPassword) {
        this.loginPassword = ((loginPassword == null) ? null : loginPassword.trim());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(this.hashCode());
        sb.append(", userId=").append(this.userId);
        sb.append(", sessionId=").append(this.sessionId);
        sb.append(", phone=").append(this.phone);
        sb.append(", loginPassword=").append(this.loginPassword);
        sb.append(", serialVersionUID=").append(1L);
        sb.append("]");
        return sb.toString();
    }
}
