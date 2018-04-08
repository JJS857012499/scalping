package com.exp.demo;

import com.exp.demo.utils.Md5Util;
import net.jhelp.mass.utils.CollectionKit;

import java.util.List;
import java.util.Map;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午2:05
 */
public class ST {
    //验证码
    public static final String p_smsCode = "http://app.zhidianlife.com/life-sms/app/zdsh_message_test?";
    //注册
    public static final String p_regist = "https://app.zhidianlife.com/member/public/mobile/register/secondMobileMall";
    //登录
    public static final String p_login = "https://account.zhidianlife.com/passport/api/mobile/login";
    //搜商品
    public static final String p_shop_product = "https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop";
    //商品详情
    public static final String p_product_info = "https://s1.zhidianlife.com/commodityInfo/api/v1//mobile/detail";
    //验证是否达标
    public static final String veriy = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite?phone=13434828914";
    //加入白名单
    public static final String p_whiltelist = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/addUserToWhite?phone=13128156998";

    //商家订单列表
    public static final String p_Merchant_orderList = "https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/orderList";
    //用户订单列表
    public static final String p_User_orderList = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list";
    //发货
    public static final String p_deliver = "https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/ship";
    //确认收货
    public static final String p_finish = "https://s2.zhidianlife.com/life-h2h/order/apis/unity/v1/order/confirmReceipt";

    //加入购物车
    public static final String p_addCar = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar";

    //确认收货
    public static final String p_shouyi = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite";

    //查询可提余额
    public static final String tixian="https://app.zhidianlife.com/life-mobile-mall/apis/v2/user/index";
    //获取收货地址
    public static final String shdz="https://app.zhidianlife.com/life-mobile-mall/apis/v1/receive/selectReceiveList";
    //下单
    public static final String addOrder="https://app.zhidianlife.com/life-h2h/order/apis/unity/v3/order/add";

    public static Map<String, String> headers = CollectionKit.newHashMap();
    static {
        headers.put("version", "32");
        headers.put("app_key", "android");
    }

    public static Map<String, String> cache = CollectionKit.newHashMap();
    public static List<String> phones = CollectionKit.newArrayList();

    public static void setUserId(String p, String userid){
        cache.put(p, userid);
    }

    public static String getUserId(String p){
        return cache.get(p);
    }


    public static void main(String[] args){
//        long str = 18767010210L;
//        for(int i = 0; i < 200; i ++){
//            System.out.println(str + i);
//        }

        System.out.println(Md5Util.encoderByMd5("123456"));
        System.out.println(Md5Util.encoderByMd5(Md5Util.encoderByMd5("456789").concat("031102fb8cbe4943807ff448c0f5f340")));
        System.out.println(Md5Util.encoderByMd5("136498"));
        System.out.println(Md5Util.encoderByMd5(Md5Util.encoderByMd5("136498").concat("0f9f1469622e44e6aa126feebf03187e")));
    }
}
