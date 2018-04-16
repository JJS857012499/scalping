package com.exp.demo.wx;

import lombok.Data;

/**
 * 模板详细信息
 * Created by 江俊升 on 2018/4/16.
 */
@Data
public class TemplateData {
    private String value;
    private String color;
    public TemplateData(String value,String color){
        this.value = value;
        this.color = color;
    }
}
