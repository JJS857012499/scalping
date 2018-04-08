package com.exp.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author JianChow
 * @date 2018-04-04
 */
@Data
public class OrderVo {
    @ApiModelProperty(value = "推荐人用户id  h5专用")
    private String referrerId;

    @ApiModelProperty(value = "上级分销商")
    private String distributorId;
    @ApiModelProperty(value = "收货地址Id")
    private String receiveId;
    private String clientType="5";
    private String appKey="android";
    private String appVersion="32";
    @NotEmpty(message = "必须选择是否使用优惠券")
    @ApiModelProperty(value = "是否使用优惠卷 0使用 1不使用")
    private String useVoucher;
    @NotEmpty(message = "必须指定是否使用余额")
    @ApiModelProperty(value = "是否使用余额（0：是，1：否）", required = true)
    private String isUseBalance;

    @ApiModelProperty(value = "支付密码")
    private String payPassword;

    @NotNull(message = "来源不能为空")
    @ApiModelProperty(value = "来源 1是商品详情 2是购物车 3转盘", required = true)
    private Integer source;

    @Valid
    @NotEmpty(message = "店铺列表不能为空")
    @ApiModelProperty(value = "店铺list", required = true)
    private List<OrderShop> shopList;

    @ApiModelProperty(value = "是否使用E卡 1：YES，0：NO", notes = "22版本 2017/08/17需求新增")
    private String useECard;

    @ApiModelProperty(value = "使用的优惠券id列表")
    private List<String> vouchers;

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class OrderShop {

        @ApiModelProperty(value = "是否开发票（0：不开，1：开）")
        private String invoiceRequired;
        @ApiModelProperty(value = "发票单位（0：个人，1：单位）")
        private String invoiceUnit;
        @ApiModelProperty(value = "发票抬头")
        private String invoiceTitle;
        @ApiModelProperty(value = "发票邮寄地址")
        private String invoiceAddress;
        @ApiModelProperty(value = "发票联系电话")
        private String invoiceContactPhone;
        @ApiModelProperty(value = "发票类型（0：纸质发票，1：电子发票）")
        private String invoiceType;
        @ApiModelProperty(value = "发票纳税人识别号")
        private String invoiceTaxPayerNumber;
        @ApiModelProperty(value = "配送方式  1：物流配送，2：到仓自提 3：活动现场自提 默认是1")
        private String logisticsType = "1";
        @ApiModelProperty(value = "留言")
        private String message = "";
        @ApiModelProperty(value = "自提时间")
        private String drawTimes = "";

        @NotEmpty(message = "店铺ID不能为空")
        @ApiModelProperty(value = "店铺ID", required = true)
        private String shopId;

        @NotEmpty(message = "商品列表不能为空")
        @Valid
        @ApiModelProperty(value = "本类商品列表", required = true)
        private List<ProductVo> products;
    }
}
