package com.exp.demo.wx;

import lombok.Data;

import java.io.Serializable;

/**
 * AccessToken 对象
 * Created by 江俊升 on 2018/4/16.
 */
@Data
public class AccessToken implements Serializable {
    //获取到的凭证
    private String accessToken;
    //凭证有效时间，单位：秒
    private int expiresin;
    //此处省略get/set 方法
}