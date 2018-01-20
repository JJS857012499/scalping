package com.exp.demo.vo;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午2:34
 */
public class RegisterReqVo {
    private String phone;
    private String password = "e99a18c428cb38d5f260853678922e03";
    private String code = "111111";

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
