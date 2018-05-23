package com.exp.demo.xc;

import com.exp.demo.utils.Md5Util;
import net.jhelp.mass.utils.CollectionKit;

import java.util.List;
import java.util.Map;

public class XC
{
    public static final String p_smsCode = "http://app.zhidianlife.com/life-sms/app/zdsh_message_test?";
    public static final String p_regist = "https://app.zhidianlife.com/member/public/mobile/register/secondMobileMall";
    public static final String p_login = "https://account.zhidianlife.com/passport/api/mobile/login";
    public static final String p_shop_product = "https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop";
    public static final String p_product_info = "https://s1.zhidianlife.com/commodityInfo/api/v1//mobile/detail";
    public static final String veriy = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite?phone=13434828914";
    public static final String p_whiltelist = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/addUserToWhite?phone=13128156998";
    public static final String p_Merchant_orderList = "https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/orderList";
    public static final String p_User_orderList = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list";
    public static final String p_deliver = "https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/ship";
    public static final String p_finish = "https://s2.zhidianlife.com/life-h2h/order/apis/unity/v1/order/confirmReceipt";
    public static final String p_addCar = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar";
    public static final String p_shouyi = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite";
    public static final String tixian = "https://app.zhidianlife.com/life-mobile-mall/apis/v2/user/index";
    public static final String shdz = "https://app.zhidianlife.com/life-mobile-mall/apis/v1/receive/selectReceiveList";
    public static final String addOrder = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v3/order/add";
    public static Map<String, String> headers;
    public static String receive_api_url;
    public static final String order_create_api_url = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v3/order/add";
    public static final String app_order_list_api_url = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list";
    public static final String cancel_orderl_order = "https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/cancel";
    public static Map<String, String> cache;
    public static List<String> phones;
    
    public static void setUserId(final String p, final String userid) {
        XC.cache.put(p, userid);
    }
    
    public static String getUserId(final String p) {
        return XC.cache.get(p);
    }
    
    public static void main(final String[] args) {
        System.out.println(Md5Util.encoderByMd5("123456"));
        System.out.println(Md5Util.encoderByMd5(Md5Util.encoderByMd5("456789").concat("031102fb8cbe4943807ff448c0f5f340")));
        System.out.println(Md5Util.encoderByMd5("136498"));
        System.out.println(Md5Util.encoderByMd5(Md5Util.encoderByMd5("136498").concat("0f9f1469622e44e6aa126feebf03187e")));
    }
    
    static {
        XC.headers = CollectionKit.newHashMap();
        XC.receive_api_url = "https://app.zhidianlife.com/life-mobile-mall/apis/v1/receive/selectReceiveList";
        XC.headers.put("version", "32");
        XC.headers.put("app_key", "android");
        XC.headers.put("terminalId", "006");
        XC.cache = CollectionKit.newHashMap();
        XC.phones = CollectionKit.newArrayList();
    }
}
