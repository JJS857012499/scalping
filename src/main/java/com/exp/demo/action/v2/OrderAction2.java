package com.exp.demo.action.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exp.demo.ST;
import com.exp.demo.utils.HttpClientUtil;
import com.exp.demo.utils.JsonUtil;
import com.exp.demo.vo.ProductVo;
import com.exp.demo.vo.ShopProductVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class OrderAction2 {
    @Value("#{'${sd.zhidian.account}'.split(',')}")
    private List<String> account;
    @Value("#{'${sd.zhidian.zhifuaccount}'.split(',')}")
    private List<String> zhifuaccounts;
    @Value("${sd_zhiidan_password}")
    private String password;
    private static final Logger log = LoggerFactory.getLogger(OrderAction2.class);


    @ResponseBody
    @RequestMapping({"order2"})
    public String order() {
        log.info("=============== add car ==================");
        Map<String, List<ProductVo>> map = new HashMap();
        List<String> excludeProdutIds = new ArrayList<>();
        //record goods that being add car, prevent repetition.
        for (String phone : ST.cache.keySet()) {
            String userId = ST.cache.get(phone).split("@")[0];
            ShopProductVo shopProductVo = new ShopProductVo();
            shopProductVo.setShopId(userId);
            String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop", JSON.toJSONString(shopProductVo), ST.headers);
            JSONObject resultJson = JSON.parseObject(result);
            JSONObject data = resultJson.getJSONObject("data");
            JSONArray commodityList = data.getJSONArray("commodityList");
            List<JSONObject> jsonObjects = commodityList.toJavaList(JSONObject.class);
            List<String> productIdz = jsonObjects.stream().map((t) -> t.getString("productId")).collect(Collectors.toList());
            List<ProductVo> ProductVos = new ArrayList();
            map.put(userId, ProductVos);
            for (String zhifuaccount : zhifuaccounts) {
                ProductVo sku = this.getSku(productIdz, excludeProdutIds);
                if (sku == null) {
                    log.info("----------sku is null------------");
                }

                ProductVos.add(sku);
            }
        }
        log.info("符合条件的商品" + JsonUtil.toJson(map));
        this.addShopCar(map);
        return "购物车添加完毕。。。。。。";
    }

    //map <userId,List<ProductVo>> 十个店铺用户id,每个店铺五个商品
    private void addShopCar(Map<String, List<ProductVo>> map) {
        int i = 0;
        for (String phone : ST.cache.keySet()) {
            for (String zhifuaccount : zhifuaccounts) {
                if (zhifuaccount.equals(phone)) {
                    Map<String, String> header = ST.headers;
                    header.put("sessionId", ST.cache.get(phone));
                    for (String userId : map.keySet()) {
                        try {
                            Thread.sleep(200L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar", JsonUtil.toJson((map.get(userId)).get(i)), ST.headers);
                        log.info("加入购物车：{}", result);
                    }
                    ++i;
                    break;
                }
            }
        }
    }

    private ProductVo getSku(List<String> productIdz, List<String> excludeProdutIds) {
        ProductVo sku1 = new ProductVo();
        Random rand = new Random();
        int myRand = rand.nextInt(productIdz.size());
        String pid = productIdz.get(myRand);
        if (excludeProdutIds.contains(pid)) {
            //contain the product, go next.
            return getSku(productIdz, excludeProdutIds);
        }
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
        if (dprice.doubleValue() >= 200.0D && dprice.doubleValue() <= 230.0D) {
            excludeProdutIds.add(pid);
            sku1.setProductId(pid);
            sku1.setPrice(new BigDecimal(price));
            sku1.setSkuId(skuId);
            sku1.setSaleType(saleType);
            sku1.setShopId(shopId);
            sku1.setQuantity(1);
            return sku1;
        } else {
            if (dprice.doubleValue() > 230.0D) {
                sku1 = this.getSku(productIdz, excludeProdutIds);
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
                    excludeProdutIds.add(pid);
                    sku1.setProductId(pid);
                    sku1.setPrice(new BigDecimal(price));
                    sku1.setSkuId(skuId);
                    sku1.setSaleType(saleType);
                    sku1.setShopId(shopId);
                    sku1.setQuantity(Integer.valueOf(k));
                    return sku1;
                }
                sku1 = this.getSku(productIdz, excludeProdutIds);
            }

            return sku1;
        }
    }


}
