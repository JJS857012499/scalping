package com.exp.demo.pojo;

import java.io.*;

public class SdSg implements Serializable
{
    private String sgId;
    private String userId;
    private String mainAccount;
    private String ziZccount;
    private static final long serialVersionUID = 1L;
    
    public String getSgId() {
        return this.sgId;
    }
    
    public void setSgId(final String sgId) {
        this.sgId = ((sgId == null) ? null : sgId.trim());
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
    
    public String getZiZccount() {
        return this.ziZccount;
    }
    
    public void setZiZccount(final String ziZccount) {
        this.ziZccount = ((ziZccount == null) ? null : ziZccount.trim());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(this.hashCode());
        sb.append(", sgId=").append(this.sgId);
        sb.append(", userId=").append(this.userId);
        sb.append(", mainAccount=").append(this.mainAccount);
        sb.append(", ziZccount=").append(this.ziZccount);
        sb.append(", serialVersionUID=").append(1L);
        sb.append("]");
        return sb.toString();
    }
}
