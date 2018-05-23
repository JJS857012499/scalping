package com.exp.demo.pojo;

import java.io.*;

public class SdGx implements Serializable
{
    private String gxId;
    private String userId;
    private String mainAccount;
    private String ziAccount;
    private static final long serialVersionUID = 1L;
    
    public String getGxId() {
        return this.gxId;
    }
    
    public void setGxId(final String gxId) {
        this.gxId = ((gxId == null) ? null : gxId.trim());
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = ((userId == null) ? null : userId.trim());
    }
    
    public String getMainAccount() {
        return this.mainAccount;
    }
    
    public void setMainAccount(final String mainAccount) {
        this.mainAccount = ((mainAccount == null) ? null : mainAccount.trim());
    }
    
    public String getZiAccount() {
        return this.ziAccount;
    }
    
    public void setZiAccount(final String ziAccount) {
        this.ziAccount = ((ziAccount == null) ? null : ziAccount.trim());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(this.hashCode());
        sb.append(", gxId=").append(this.gxId);
        sb.append(", userId=").append(this.userId);
        sb.append(", mainAccount=").append(this.mainAccount);
        sb.append(", ziAccount=").append(this.ziAccount);
        sb.append(", serialVersionUID=").append(1L);
        sb.append("]");
        return sb.toString();
    }
}
