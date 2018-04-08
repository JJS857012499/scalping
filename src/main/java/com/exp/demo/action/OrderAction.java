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
import com.exp.demo.vo.ShouYiVo;
import com.exp.demo.vo.loginVo;
import net.jhelp.mass.utils.CollectionKit;
import net.jhelp.mass.utils.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class OrderAction {
    @Value("#{'${sd.zhidian.account}'.split(',')}")
    private List<String> account;
    @Value("#{'${sd.zhidian.zhifuaccount}'.split(',')}")
    private List<String> zhifuaccount;
    @Value("${sd_zhiidan_password}")
    private String password;
    private static final Logger log = LoggerFactory.getLogger(OrderAction.class);

    public OrderAction() {
    }

    @ResponseBody
    @RequestMapping({"order"})
    public String order() {
        log.info("================================================请求加入购物车==================================");
            Map<String, List<ProductVo>> map = new HashMap();
            Iterator var2 = ST.cache.keySet().iterator();

            while (var2.hasNext()) {
                String s = (String) var2.next();
                String userId = ((String) ST.cache.get(s)).split("@")[0];
                ShopProductVo shopProductVo = new ShopProductVo();
                shopProductVo.setShopId(userId);
                String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop", JSON.toJSONString(shopProductVo), ST.headers);
                JSONObject resultJson = JSON.parseObject(result);
                JSONObject data = resultJson.getJSONObject("data");
                JSONArray commodityList = data.getJSONArray("commodityList");
                List<JSONObject> jsonObjects = commodityList.toJavaList(JSONObject.class);
                List<String> productIdz = (List) jsonObjects.stream().map((t) -> {
                    return t.getString("productId");
                }).collect(Collectors.toList());
                List<ProductVo> ProductVos = new ArrayList();
                Iterator var13 = this.zhifuaccount.iterator();
                while (var13.hasNext()) {
                    String t = (String) var13.next();
                    ProductVo sku = this.getSku(productIdz);
                    if (sku == null) {
                        log.info("-------------------------------sku is null---------------------------------");
                    }

                    ProductVos.add(sku);
                    map.put(userId, ProductVos);
                }
            }

            log.info("符合条件的商品" + JsonUtil.toJson(map));
            this.addShopCar(map);
            return "购物车添加完毕。。。。。。";


    }

    private void addShopCar(Map<String, List<ProductVo>> map) {
        int i = 0;
        Iterator var3 = ST.cache.keySet().iterator();

        while (true) {
            while (var3.hasNext()) {
                String s = (String) var3.next();
                Iterator var5 = this.zhifuaccount.iterator();

                while (var5.hasNext()) {
                    String a = (String) var5.next();
                    if (a.equals(s)) {
                        Map<String, String> header = ST.headers;
                        header.put("sessionId", ST.cache.get(s));
                        Iterator var8 = map.keySet().iterator();

                        while (var8.hasNext()) {
                            String key = (String) var8.next();

                            try {
                                Thread.sleep(200L);
                            } catch (InterruptedException var11) {
                                var11.printStackTrace();
                            }

                            String result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar", JsonUtil.toJson(((List) map.get(key)).get(i)), ST.headers);
                            log.info("加入购物车：{}", result);
                        }

                        ++i;
                        break;
                    }
                }
            }

            return;
        }
    }

    private ProductVo getSku(List<String> productIdz) {
        ProductVo sku1 = new ProductVo();
        Random rand = new Random();
        int size = productIdz.size();
        int myRand = rand.nextInt(size);
        String pid = (String) productIdz.get(myRand);
        String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/commodityInfo/api/v1//mobile/detail", "{\"productId\":\"" + pid + "\"}", ST.headers);
        JSONObject resultJson = JSON.parseObject(result);
        JSONObject data = resultJson.getJSONObject("data");
        JSONObject product = data.getJSONObject("product");
        JSONObject sku = product.getJSONArray("skus").getJSONObject(0);
        JSONObject value = sku.getJSONObject("value");
        String price = value.getString("price");
        String skuId = value.getString("skuId");
        String saleType = product.getString("saleType");
        String shopId = product.getString("shopId");
        Double dprice = Double.valueOf(price);
        ProductVo productVo = new ProductVo();
        if (dprice.doubleValue() >= 200.0D && dprice.doubleValue() <= 230.0D) {
            productVo.setProductId(pid);
            productVo.setPrice(new BigDecimal(price));
            productVo.setSkuId(skuId);
            productVo.setSaleType(saleType);
            productVo.setShopId(shopId);
            productVo.setQuantity(1);
            return productVo;
        } else {
            if (dprice.doubleValue() > 230.0D) {
                sku1 = this.getSku(productIdz);
            } else {
                int i = dprice.intValue();
                int i1 = 200 % i;
                int k;
                if (i1 > 0) {
                    k = 200 / i + 1;
                } else {
                    k = 200 / i;
                }

                if ((double) k * dprice.doubleValue() <= 230.0D) {
                    sku1.setProductId(pid);
                    sku1.setPrice(new BigDecimal(price));
                    sku1.setSkuId(skuId);
                    sku1.setSaleType(saleType);
                    sku1.setShopId(shopId);
                    sku1.setQuantity(Integer.valueOf(k));
                    return sku1;

                }

                sku1 = this.getSku(productIdz);
            }

            return sku1;
        }
    }

    @RequestMapping(
            value = {"delivery"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public String delivery() {
        log.info("accounts : {}", this.account);
        Iterator var1 = this.account.iterator();
        while (var1.hasNext()) {
            String s = (String) var1.next();
            Map<String, Object> orderParam = CollectionKit.newHashMap();
            orderParam.put("searchOrderStatus", Integer.valueOf(50));
            orderParam.put("startPage", Integer.valueOf(1));
            orderParam.put("pageSize", Integer.valueOf(50));
            orderParam.put("role", Integer.valueOf(1));
            Map<String, String> header = ST.headers;
            header.put("sessionId", ST.cache.get(s));
            String result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/orderList", JSON.toJSONString(orderParam), ST.headers);
            log.info("order result : {}", result);
            JSONObject orderResult = JSON.parseObject(result);
            JSONObject orderData = orderResult.getJSONObject("data");
            JSONArray orderList = orderData.getJSONArray("orderPage");
            for (int i = 0; i < orderList.size(); ++i) {
                JSONObject order = orderList.getJSONObject(i);
                Map<String, Object> deliverParam = CollectionKit.newHashMap();
                deliverParam.put("orderId", order.getString("orderId"));
                deliverParam.put("isLogistics", Integer.valueOf(1));
                deliverParam.put("role", Integer.valueOf(1));

                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException var16) {
                    var16.printStackTrace();
                }

                result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/ship", JSON.toJSONString(deliverParam), ST.headers);
                log.info("deliver result : {}", result);
            }
        }

        return "批量发货完成";
    }

    @RequestMapping(
            value = {"finish"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public String finish() {
        log.info("account : {}", this.account);
        Iterator var1 = this.account.iterator();

        while (var1.hasNext()) {
            String s = (String) var1.next();
            loginVo loginVo = new loginVo();
            if (StringKit.isNotBlank(this.password)) {
                loginVo.setPassword(Md5Util.encoderByMd5(this.password));
            }

            loginVo.setPhone(s);
            String result = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString(loginVo), ST.headers);
            log.info("login result : {}", result);
            JSONObject jsonObject1 = JSON.parseObject(result);
            JSONObject data = jsonObject1.getJSONObject("data");
            log.info("sessionId : {}", data.getString("sessionId"));
            ST.cache.put(s, data.getString("sessionId"));
            Map<String, Object> orderParam = CollectionKit.newHashMap();
            orderParam.put("status", Integer.valueOf(100));
            orderParam.put("startPage", Integer.valueOf(1));
            orderParam.put("pageSize", Integer.valueOf(50));
            Map<String, String> header = ST.headers;
            header.put("sessionId", ST.cache.get(s));
            result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list", JSON.toJSONString(orderParam), ST.headers);
            log.info("order result : {}", result);
            JSONObject orderResult = JSON.parseObject(result);
            JSONArray orderList = orderResult.getJSONArray("data");

            for (int i = 0; i < orderList.size(); ++i) {
                JSONObject order = orderList.getJSONObject(i);
                Map<String, Object> deliverParam = CollectionKit.newHashMap();
                deliverParam.put("orderId", order.getString("orderId"));

                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException var15) {
                    var15.printStackTrace();
                }

                result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/life-h2h/order/apis/unity/v1/order/confirmReceipt", JSON.toJSONString(deliverParam), ST.headers);
                log.info("finish result : {}", result);
            }
        }

        return "批量收货完成";
    }

    @RequestMapping(
            value = {"shouyi"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public List<ShouYiVo> shouyi() {
        List<ShouYiVo> list = new ArrayList();
        Iterator var2 = this.account.iterator();
        while (var2.hasNext()) {
            String a = (String) var2.next();
            Map<String, String> map = new HashMap();
            map.put("phone", a);
            String url = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite?phone=" + a;
            String s = HttpClientUtil.get(url, ST.headers);
            try {
                Thread.sleep(200L);
            } catch (InterruptedException var8) {
                var8.printStackTrace();
            }

            ShouYiVo shouYiVo = (ShouYiVo) JSON.parseObject(s, ShouYiVo.class);
            shouYiVo.setAccount(a);
            list.add(shouYiVo);
        }

        return list;
    }


    @RequestMapping("loginall")
    @ResponseBody
    public String loginall(){
        for(String s:account){
            loginVo loginVo = new loginVo();
            if (StringKit.isNotBlank(this.password)) {
                loginVo.setPassword(Md5Util.encoderByMd5(this.password));
            }

            loginVo.setPhone(s);
            String result = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString(loginVo), ST.headers);
            log.info("login result : {}", result);
            JSONObject jsonObject1 = JSON.parseObject(result);
            if(!"000".equals(jsonObject1.getString("result"))){
                return "批量登录失败,账户："+s+",返回参数："+result;
            }
            JSONObject data = jsonObject1.getJSONObject("data");
            log.info("sessionId : {}", data.getString("sessionId"));
            ST.cache.put(s, data.getString("sessionId"));
        }
        return "批量登录完成";
    }


}
