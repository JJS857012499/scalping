package com.exp.demo.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.exp.demo.dao.*;
import com.exp.demo.xc.*;
import org.springframework.util.*;
import java.util.stream.*;
import com.exp.demo.utils.*;
import java.util.*;
import com.alibaba.fastjson.*;
import com.exp.demo.vo.*;
import net.jhelp.mass.utils.*;
import java.text.*;
import java.math.*;
import org.slf4j.*;

@Service
public class SdService
{
    private static final Logger log;
    @Autowired
    private SdGxMapper sdGxMapper;
    @Autowired
    private SdSessionMapper sdSessionMapper;
    @Autowired
    private SdSgMapper sdSgMapper;
    @Autowired
    private SdUserMapper sdUserMapper;
    
    private Map<String, ProductVo> getSku(final Map<String, ProductVo> skuMap, final List<String> productIdz, final String keti, double dprices) {
        final Double k = Double.valueOf(keti);
        final Random rand = new Random();
        final int size = productIdz.size();
        final int myRand = rand.nextInt(size);
        final String pid = productIdz.get(myRand);
        final ProductVo productVo = this.getProductVo(pid);
        final Double dprice = productVo.getPrice().doubleValue();
        dprices += dprice;
        final ProductVo productVo2 = skuMap.get(productVo.getSkuId());
        if (k - dprices >= 0.0 && k - dprices <= 40.0) {
            if (productVo2 != null) {
                productVo2.setQuantity(productVo2.getQuantity() + 1);
            }
            else {
                skuMap.put(productVo.getSkuId(), productVo);
            }
            dprices = 0.0;
            return skuMap;
        }
        Map<String, ProductVo> sku;
        if (k - dprices > 40.0) {
            if (productVo2 != null) {
                productVo2.setQuantity(productVo2.getQuantity() + 1);
            }
            else {
                skuMap.put(productVo.getSkuId(), productVo);
            }
            sku = this.getSku(skuMap, productIdz, keti, dprices);
        }
        else {
            dprices -= dprice;
            sku = this.getSku(skuMap, productIdz, keti, dprices);
        }
        return sku;
    }
    
    public ProductVo getProductVo(final String productId) {
        final String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/commodityInfo/api/v1/mobile/detail", "{\"productId\":\"" + productId + "\"}", XC.headers);
        final JSONObject resultJson = JSON.parseObject(result);
        final JSONObject data = resultJson.getJSONObject("data");
        final JSONObject product = data.getJSONObject("product");
        final JSONObject sku = product.getJSONArray("skus").getJSONObject(0);
        final JSONObject value = sku.getJSONObject("value");
        final String price = value.getString("price");
        final String skuId = value.getString("skuId");
        final String saleType = product.getString("saleType");
        final String shopId = product.getString("shopId");
        final ProductVo productVo = new ProductVo();
        productVo.setProductId(productId);
        productVo.setPrice(new BigDecimal(price));
        productVo.setSkuId(skuId);
        productVo.setSaleType(saleType);
        productVo.setShopId(shopId);
        productVo.setQuantity(1);
        return productVo;
    }
    
    public List<ExpectedKetiVo> getExpectedKeti(final XczyVo xczyVo) {
        final String zhiAccount = xczyVo.getZhiAccount();
        final String[] account = zhiAccount.split(",");
        final List<String> lista = new ArrayList<String>();
        final String mainAccount = xczyVo.getMainAccount();
        lista.add(mainAccount);
        for (final String a : account) {
            lista.add(a);
        }
        double expcteds = 0.0;
        double ketis = 0.0;
        final List<ExpectedKetiVo> list = new ArrayList<ExpectedKetiVo>();
        for (final String s : lista) {
            final Map<String, String> header = (Map<String, String>)XC.headers;
            header.put("sessionId", XC.cache.get(s));
            final String result1 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-mobile-mall/apis/v2/user/index", (String)null, XC.headers);
            SdService.log.info("yuqi result : {}", (Object)result1);
            final JSONObject jsonObject2 = JSON.parseObject(result1);
            final JSONObject data1 = jsonObject2.getJSONObject("data");
            final String expcted = data1.getString("expectedReturn");
            final String keti = data1.getString("totalCash");
            final String userNickName = data1.getString("userNickName");
            expcteds += Double.valueOf(expcted);
            ketis += Double.valueOf(keti);
            final ExpectedKetiVo expectedKetiVo = new ExpectedKetiVo();
            expectedKetiVo.setUserNickName(userNickName);
            expectedKetiVo.setAccount(s);
            expectedKetiVo.setExpcted(expcted);
            expectedKetiVo.setKeti(keti);
            expectedKetiVo.setHeji(String.valueOf(formatDouble(Double.valueOf(expcted) + Double.valueOf(keti))));
            list.add(expectedKetiVo);
        }
        final ExpectedKetiVo expectedKetiVo2 = new ExpectedKetiVo();
        expectedKetiVo2.setUserNickName("");
        expectedKetiVo2.setAccount("\u5408\u8ba1\uff1a");
        expectedKetiVo2.setExpcted(String.valueOf(formatDouble(expcteds)));
        expectedKetiVo2.setKeti(String.valueOf(formatDouble(ketis)));
        expectedKetiVo2.setHeji(String.valueOf(formatDouble(expcteds + ketis)));
        list.add(expectedKetiVo2);
        return list;
    }
    
    public String shouge(final XczyVo xczyVo) {
        if (StringUtils.isEmpty((Object)xczyVo.getPayPassword())) {
            return "\u652f\u4ed8\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a";
        }
        final String userId = XC.cache.get(xczyVo.getMainAccount()).split("@")[0];
        final ShopProductVo shopProductVo = new ShopProductVo();
        shopProductVo.setShopId(userId);
        final String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop", JSON.toJSONString((Object)shopProductVo), XC.headers);
        final JSONObject resultJson = JSON.parseObject(result);
        final JSONObject data = resultJson.getJSONObject("data");
        final JSONArray commodityList = data.getJSONArray("commodityList");
        final List<JSONObject> jsonObjects = (List<JSONObject>)commodityList.toJavaList((Class)JSONObject.class);
        final List<String> productIdz = jsonObjects.stream().map(t -> t.getString("productId")).collect(Collectors.toList());
        final String[] split2;
        final String[] split = split2 = xczyVo.getZhiAccount().split(",");
        for (final String s : split2) {
            final Map<String, String> header = XC.headers;
            final String sessionId = XC.cache.get(s);
            header.put("sessionId", sessionId);
            final String result2 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-mobile-mall/apis/v2/user/index", (String)null, XC.headers);
            SdService.log.info("yuqi result : {}", (Object)result2);
            final JSONObject jsonObject2 = JSON.parseObject(result2);
            final JSONObject data2 = jsonObject2.getJSONObject("data");
            final String keti = data2.getString("totalCash");
            final double k = Double.valueOf(keti);
            if (k > 100.0) {
                final Map<String, ProductVo> map = new HashMap<String, ProductVo>();
                final Double dprices = 0.0;
                final Map<String, ProductVo> skumap = this.getSku(map, productIdz, keti, dprices);
                final List<ProductVo> skus = new ArrayList<ProductVo>();
                skumap.forEach((a, v) -> skus.add(v));
                for (final ProductVo p : skus) {
                    try {
                        Thread.sleep(200L);
                    }
                    catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                    final String result3 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar", JsonUtil.toJson((Object)p), XC.headers);
                    System.out.println(result3);
                }
                final String s2 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-mobile-mall/apis/v1/receive/selectReceiveList", (String)null, (Map)header);
                final JSONObject json_shdz_data = JSON.parseObject(s2);
                final JSONArray shdzList = json_shdz_data.getJSONArray("data");
                final List<JSONObject> shdzs = (List<JSONObject>)shdzList.toJavaList((Class)JSONObject.class);
                if (shdzs.size() < 1) {
                    return "\u6536\u8d27\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a";
                }
                final String receiveId = shdzs.get(0).getString("receiveId");
                System.out.println(receiveId);
                final OrderVo orderVo = new OrderVo();
                orderVo.setReceiveId(receiveId);
                orderVo.setSource(2);
                final String s3 = Md5Util.encoderByMd5(xczyVo.getPayPassword());
                final String pw = "zhidianlife_2016" + s3;
                final byte[] bytes = pw.getBytes();
                final Base64.Encoder encoder = Base64.getEncoder();
                final String s4 = encoder.encodeToString(bytes);
                orderVo.setPayPassword(s4);
                orderVo.setUseECard("0");
                orderVo.setIsUseBalance("0");
                orderVo.setUseVoucher("1");
                final List<OrderVo.OrderShop> orderShops = new ArrayList<OrderVo.OrderShop>();
                SdService.log.info(JSON.toJSONString((Object)skus));
                final OrderVo.OrderShop orderShop = new OrderVo.OrderShop();
                orderShop.setShopId(XC.cache.get(xczyVo.getMainAccount()).split("@")[0]);
                orderShop.setInvoiceRequired("0");
                orderShop.setLogisticsType("1");
                orderShop.setProducts((List)skus);
                orderShops.add(orderShop);
                orderVo.setShopList((List)orderShops);
                SdService.log.info(JsonUtil.toJson((Object)orderVo));
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException var9) {
                    var9.printStackTrace();
                }
                final String result4 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v3/order/add", JsonUtil.toJson((Object)orderVo), XC.headers);
                final JSONObject order = JSON.parseObject(result4);
                if ("000".equals(order.getString("result"))) {
                    SdService.log.info("\u8d26\u6237:{},\u5df2\u7ecf\u6210\u529f\u5c06\u8d44\u91d1\u8f6c\u79fb\u4e3b\u53f7", (Object)s);
                }
                else {
                    final List<String> car = new ArrayList<String>();
                    final String result5 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/listCar", (String)null, XC.headers);
                    final JSONObject result3o = JSON.parseObject(result5);
                    final JSONObject data3 = result3o.getJSONObject("data");
                    final JSONArray shopCarList = data3.getJSONArray("shopCarList");
                    final List<JSONObject> jsonObjectz = (List<JSONObject>)shopCarList.toJavaList((Class)JSONObject.class);
                    for (final JSONObject jsonObject3 : jsonObjectz) {
                        final JSONArray products = jsonObject3.getJSONArray("products");
                        final List<JSONObject> jsonObjects2 = (List<JSONObject>)products.toJavaList((Class)JSONObject.class);
                        for (final JSONObject j : jsonObjects2) {
                            final String carId = j.getString("carId");
                            car.add(carId);
                        }
                    }
                    final CarVo carVo = new CarVo();
                    carVo.setCarId((List)car);
                    if (car.size() > 0) {
                        HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/removeCar", JSON.toJSONString((Object)carVo), XC.headers);
                    }
                }
                SdService.log.info(result4);
            }
        }
        return "\u8d26\u6237\u8d44\u91d1\u6536\u96c6\u6210\u529f";
    }
    
    public String delivery(final XczyVo xczyVo) {
        final String zhiAccount = xczyVo.getZhiAccount();
        final String[] account = zhiAccount.split(",");
        final List<String> lista = new ArrayList<>();
        final String mainAccount = xczyVo.getMainAccount();
        lista.add(mainAccount);
        for (final String a : account) {
            lista.add(a);
        }
        for (final String s : lista) {
            final Map<String, Object> orderParam = CollectionKit.newHashMap();
            orderParam.put("searchOrderStatus", 50);
            orderParam.put("startPage", 1);
            orderParam.put("pageSize", 50);
            orderParam.put("role", 1);
            final Map<String, String> header = XC.headers;
            header.put("sessionId", XC.cache.get(s));
            String result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/orderList", JSON.toJSONString((Object)orderParam), XC.headers);
            SdService.log.info("order result : {}", (Object)result);
            final JSONObject orderResult = JSON.parseObject(result);
            final JSONObject orderData = orderResult.getJSONObject("data");
            final JSONArray orderList = orderData.getJSONArray("orderPage");
            for (int i = 0; i < orderList.size(); ++i) {
                final JSONObject order = orderList.getJSONObject(i);
                final Map<String, Object> deliverParam = CollectionKit.newHashMap();
                deliverParam.put("orderId", order.getString("orderId"));
                deliverParam.put("isLogistics", 1);
                deliverParam.put("role", 1);
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException var16) {
                    var16.printStackTrace();
                }
                result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/order-manage/apis/unity/v1/orderManage/ship", JSON.toJSONString((Object)deliverParam), XC.headers);
                SdService.log.info("deliver result : {}", (Object)result);
            }
        }
        return "\u6279\u91cf\u53d1\u8d27\u5b8c\u6210";
    }
    
    public String finish(final XczyVo xczyVo) {
        final String zhiAccount = xczyVo.getZhiAccount();
        final String[] account = zhiAccount.split(",");
        final List<String> lista = new ArrayList<String>();
        final String mainAccount = xczyVo.getMainAccount();
        lista.add(mainAccount);
        for (final String a : account) {
            lista.add(a);
        }
        for (final String s : lista) {
            final loginVo loginVo = new loginVo();
            if (StringKit.isNotBlank(xczyVo.getLoginPassword())) {
                loginVo.setPassword(Md5Util.encoderByMd5(xczyVo.getLoginPassword()));
            }
            loginVo.setPhone(s);
            String result = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString(loginVo), XC.headers);
            SdService.log.info("login result : {}", result);
            final JSONObject jsonObject1 = JSON.parseObject(result);
            final JSONObject data = jsonObject1.getJSONObject("data");
            SdService.log.info("sessionId : {}", data.getString("sessionId"));
            XC.cache.put(s, data.getString("sessionId"));
            final Map<String, Object> orderParam = CollectionKit.newHashMap();
            orderParam.put("status", 100);
            orderParam.put("startPage", 1);
            orderParam.put("pageSize", 50);
            final Map<String, String> header = XC.headers;
            header.put("sessionId", XC.cache.get(s));
            result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list", JSON.toJSONString(orderParam), XC.headers);
            SdService.log.info("order result : {}", result);
            final JSONObject orderResult = JSON.parseObject(result);
            final JSONArray orderList = orderResult.getJSONArray("data");
            for (int i = 0; i < orderList.size(); ++i) {
                final JSONObject order = orderList.getJSONObject(i);
                final Map<String, Object> deliverParam = CollectionKit.newHashMap();
                deliverParam.put("orderId", order.getString("orderId"));
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException var15) {
                    var15.printStackTrace();
                }
                result = HttpClientUtil.sendHttpsPost("https://s2.zhidianlife.com/life-h2h/order/apis/unity/v1/order/confirmReceipt", JSON.toJSONString(deliverParam), XC.headers);
                SdService.log.info("finish result : {}", result);
            }
        }
        return "\u6279\u91cf\u6536\u8d27\u5b8c\u6210";
    }
    
    public static String formatDouble(final double d) {
        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.DOWN);
        return nf.format(d);
    }
    
    public String loginall(final XczyVo xczyVo) {
        final String zhiAccount = xczyVo.getZhiAccount();
        final String[] account = zhiAccount.split(",");
        final List<String> list = new ArrayList<String>();
        if (StringUtils.isEmpty((Object)xczyVo.getLoginPassword())) {
            return "\u767b\u5f55\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a";
        }
        for (final String a : account) {
            list.add(a);
        }
        final String mainAccount = xczyVo.getMainAccount();
        list.add(mainAccount);
        final String loginPassword = xczyVo.getLoginPassword();
        for (final String s : list) {
            final loginVo loginVo = new loginVo();
            if (StringKit.isNotBlank(loginPassword)) {
                loginVo.setPassword(Md5Util.encoderByMd5(loginPassword));
            }
            loginVo.setPhone(s);
            final String result = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString((Object)loginVo), XC.headers);
            SdService.log.info("login result : {}", (Object)result);
            final JSONObject jsonObject1 = JSON.parseObject(result);
            if (!"000".equals(jsonObject1.getString("result"))) {
                return "\u6279\u91cf\u767b\u5f55\u5931\u8d25,\u8d26\u6237\uff1a" + s + ",\u8fd4\u56de\u53c2\u6570\uff1a" + result;
            }
            final JSONObject data = jsonObject1.getJSONObject("data");
            SdService.log.info("sessionId : {}", (Object)data.getString("sessionId"));
            XC.cache.put(s, data.getString("sessionId"));
        }
        return "\u6279\u91cf\u767b\u5f55\u5b8c\u6210";
    }
    
    static {
        log = LoggerFactory.getLogger((Class)SdService.class);
    }
}
