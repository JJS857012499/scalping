package com.exp.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exp.demo.ST;
import com.exp.demo.utils.HttpClientUtil;
import com.exp.demo.utils.JsonUtil;
import com.exp.demo.utils.Md5Util;
import com.exp.demo.vo.ProductVo;
import com.exp.demo.vo.ShopProductVo;
import com.exp.demo.vo.loginVo;
import net.jhelp.mass.utils.CollectionKit;
import net.jhelp.mass.utils.StringKit;
import org.omg.CORBA.INTERNAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <ul></ul>
 *
 * @Author : kame
 * @Date: 17/12/29 下午5:39
 */
@Controller
public class OrderAction {

    private static final Logger log = LoggerFactory.getLogger(OrderAction.class);

    @RequestMapping(value = "order")
    public String order() {
        int sizelimit = 1;
        List<ProductVo> productIds = CollectionKit.newArrayList();
        for (String s : ST.cache.keySet()) {
            int size = 0;
            String userId = ST.cache.get(s).split("@")[0];
            ShopProductVo shopProductVo = new ShopProductVo();
            shopProductVo.setShopId(userId);
            String result = HttpClientUtil.sendHttpsPost(ST.p_shop_product, JSON.toJSONString(shopProductVo), ST.headers);
//            log.info("shop product:{}", result);
            JSONObject resultJson = JSON.parseObject(result);
            JSONObject data = resultJson.getJSONObject("data");
            JSONArray commodityList = data.getJSONArray("commodityList");

            for (int i = 0; i < commodityList.size(); i++) {
                JSONObject p = commodityList.getJSONObject(i);
                String pid = p.getString("productId");
                ProductVo sku = getSku(pid);
                if (sku != null) {
                    log.info("符合条件的商品" + JsonUtil.toJson(sku));
                    productIds.add(sku);
                    ++size;
                    if (sizelimit >= size) {
                        break;
                    }
                }
            }
        }
        addShopCar(productIds);

        return "index";
    }

    private void addShopCar(List<ProductVo> productIds) {
        for (String s : ST.cache.keySet()) {
            if ("18578638326".equals(s)
                    || "18825146607".equals(s)
                    || "18825146600".equals(s)
                    || "13640328644".equals(s)
                    || "18318326959".equals(s)
                    ) {
                for (ProductVo vo : productIds) {
                    Map<String, String> header = ST.headers;
                    header.put("sessionId", ST.cache.get(s));
                    String result = HttpClientUtil.sendHttpsPost(ST.p_addCar, JsonUtil.toJson(vo), ST.headers);
                    log.info("加入购物车：{}", result);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private ProductVo getSku(String pid) {
        String result = HttpClientUtil.sendHttpsPost(ST.p_product_info, "{\"productId\":\"" + pid + "\"}", ST.headers);
        //log.info("product detail : {}", result);
        JSONObject resultJson = JSON.parseObject(result);
        JSONObject data = resultJson.getJSONObject("data");
        JSONObject product = data.getJSONObject("product");
        JSONObject sku = product.getJSONArray("skus").getJSONObject(0);
        JSONObject value = sku.getJSONObject("value");
        String price = value.getString("price");
        String skuId = value.getString("skuId");
        String saleType = product.getString("saleType");
        String shopId = product.getString("shopId");
        log.info("pid : {}, skuId:{}, price:{}, saleType:{},shopId:{}", pid, skuId, price, saleType, shopId);

        Double dprice = Double.valueOf(price);
        if (dprice > 199 && dprice < 205) {
            ProductVo productVo = new ProductVo();
            productVo.setProductId(pid);
            productVo.setPrice(new BigDecimal(price));
            productVo.setSkuId(skuId);

            productVo.setSaleType(saleType);
            productVo.setShopId(shopId);
            return productVo;
        }
        return null;
    }

    @RequestMapping(value = "delivery", method = {RequestMethod.POST})
    @ResponseBody
    public String delivery(String accounts, String password) {
        log.info("accounts : {}", accounts);
        if (StringKit.isNotBlank(accounts)) {
            accounts = accounts.replaceAll("\r\n", "").replaceAll("\n", "");
            String[] tmp = accounts.split(",");
            for (String s : tmp) {
                //登录
                loginVo loginVo = new loginVo();
                if (StringKit.isNotBlank(password)) {
                    loginVo.setPassword(Md5Util.encoderByMd5(password));
                }
                loginVo.setPhone(s);
                String result = HttpClientUtil.sendHttpsPost(ST.p_login, JSON.toJSONString(loginVo), ST.headers);
                log.info("login result : {}", result);
                JSONObject jsonObject1 = JSON.parseObject(result);
                JSONObject data = jsonObject1.getJSONObject("data");
                log.info("sessionId : {}", data.getString("sessionId"));
                ST.cache.put(s, data.getString("sessionId"));
                //ST.phones.add(s);
                //发货
                Map<String, Object> orderParam = CollectionKit.newHashMap();
                orderParam.put("searchOrderStatus", 50);
                orderParam.put("startPage", 1);
                orderParam.put("pageSize", 50);
                orderParam.put("role", 1);
                Map<String, String> header = ST.headers;
                header.put("sessionId", ST.cache.get(s));
                result = HttpClientUtil.sendHttpsPost(ST.p_Merchant_orderList, JSON.toJSONString(orderParam), ST.headers);
                log.info("order result : {}", result);
                JSONObject orderResult = JSON.parseObject(result);
                JSONObject orderData = orderResult.getJSONObject("data");
                JSONArray orderList = orderData.getJSONArray("orderPage");
                for (int i = 0; i < orderList.size(); i++) {
                    JSONObject order = orderList.getJSONObject(i);
                    Map<String, Object> deliverParam = CollectionKit.newHashMap();
                    deliverParam.put("orderId", order.getString("orderId"));
                    deliverParam.put("isLogistics", 1);
                    deliverParam.put("role", 1);
                    result = HttpClientUtil.sendHttpsPost(ST.p_deliver, JSON.toJSONString(deliverParam), ST.headers);
                    log.info("deliver result : {}", result);
                }
            }
        }

        return "ok";
    }


    @RequestMapping(value = "finish", method = {RequestMethod.POST})
    @ResponseBody
    public String finish(String accounts, String password) {
        log.info("accounts : {}", accounts);
        if (StringKit.isNotBlank(accounts)) {
            accounts = accounts.replaceAll("\r\n", "").replaceAll("\n", "");
            String[] tmp = accounts.split(",");
            for (String s : tmp) {
                //登录
                loginVo loginVo = new loginVo();
                if (StringKit.isNotBlank(password)) {
                    loginVo.setPassword(Md5Util.encoderByMd5(password));
                }
                loginVo.setPhone(s);
                String result = HttpClientUtil.sendHttpsPost(ST.p_login, JSON.toJSONString(loginVo), ST.headers);
                log.info("login result : {}", result);
                JSONObject jsonObject1 = JSON.parseObject(result);
                JSONObject data = jsonObject1.getJSONObject("data");
                log.info("sessionId : {}", data.getString("sessionId"));
                ST.cache.put(s, data.getString("sessionId"));
                //ST.phones.add(s);
                //发货
                Map<String, Object> orderParam = CollectionKit.newHashMap();
                orderParam.put("status", 100);
                orderParam.put("startPage", 1);
                orderParam.put("pageSize", 50);
                Map<String, String> header = ST.headers;
                header.put("sessionId", ST.cache.get(s));
                result = HttpClientUtil.sendHttpsPost(ST.p_User_orderList, JSON.toJSONString(orderParam), ST.headers);
                log.info("order result : {}", result);
                JSONObject orderResult = JSON.parseObject(result);
                JSONArray orderList = orderResult.getJSONArray("data");
                for (int i = 0; i < orderList.size(); i++) {
                    JSONObject order = orderList.getJSONObject(i);
                    Map<String, Object> deliverParam = CollectionKit.newHashMap();
                    deliverParam.put("orderId", order.getString("orderId"));
                    result = HttpClientUtil.sendHttpsPost(ST.p_finish, JSON.toJSONString(deliverParam), ST.headers);
                    log.info("finish result : {}", result);
                }
            }
        }

        return "ok";
    }
}
