package com.exp.demo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午6:32
 */
@Data
public class ProductVo {
    private String productId;
    private String skuId;
    private BigDecimal price;

    private String saleType;

    private String shopId;

    private Integer quantity = 1;


}
