package com.exp.demo.vo;

import lombok.Data;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午6:12
 */
@Data
public class ShopProductVo {
    private String shopId;
    private String priceFrom = "198";
    private String priceTo = "210";
    private Integer pageSize = 40;
    private Integer startPage = 1;


}
