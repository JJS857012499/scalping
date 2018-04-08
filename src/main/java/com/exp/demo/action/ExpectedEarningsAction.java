package com.exp.demo.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exp.demo.ST;
import com.exp.demo.queue.*;
import com.exp.demo.queue.Queue;
import com.exp.demo.utils.HttpClientUtil;
import com.exp.demo.utils.JsonUtil;
import com.exp.demo.utils.Md5Util;
import com.exp.demo.vo.*;
import net.jhelp.mass.utils.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author JianChow
 * @date 2018-03-31
 */
@Controller
@Scope("prototype")
public class ExpectedEarningsAction {
    @Value("#{'${sd.zhidian.account}'.split(',')}")
    private List<String> account;
    @Value("${sd_zhiidan_password}")
    private String password;
    @Value("${sd_main_zhifuaccount}")
    private String mainAccount;
    @Value("#{'${sd_account}'.split(',')}")
    private List<String> ziAccount;
    @Value("${sd_paypassword}")
    private String payPassword;
    private static final Logger log = LoggerFactory.getLogger(ExpectedEarningsAction.class);
    Double dprices=0D;

    @ResponseBody
    @RequestMapping(value = "expectedKeti",method = RequestMethod.POST)
    public List<ExpectedKetiVo> getExpectedKeti(){
        //设置合计
        double expcteds=0;
        double ketis=0;
        List<ExpectedKetiVo> list=new ArrayList<>();
        for (String s:account){
            loginVo loginVo = new loginVo();
            if(StringKit.isNotBlank(this.password)) {
                loginVo.setPassword(Md5Util.encoderByMd5(this.password));
            }

            loginVo.setPhone(s);
            Map<String, String> header = ST.headers;
            header.put("sessionId",ST.cache.get(s) );
            String result1 = HttpClientUtil.sendHttpsPost(ST.tixian,null, ST.headers);
            log.info("yuqi result : {}", result1);
            JSONObject jsonObject2 = JSON.parseObject(result1);
            JSONObject data1 = jsonObject2.getJSONObject("data");
            //预期收益
            String expcted = data1.getString("expectedReturn");
            //可提
            String keti = data1.getString("totalCash");
            String userNickName = data1.getString("userNickName");
            expcteds=Double.valueOf(expcted)+expcteds;
            ketis=Double.valueOf(keti)+ketis;
            ExpectedKetiVo expectedKetiVo = new ExpectedKetiVo();
            expectedKetiVo.setUserNickName(userNickName);
            expectedKetiVo.setAccount(s);
            expectedKetiVo.setExpcted(expcted);
            expectedKetiVo.setKeti(keti);
            expectedKetiVo.setHeji(String.valueOf(formatDouble(Double.valueOf(expcted)+Double.valueOf(keti))));
            list.add(expectedKetiVo);
        }
        ExpectedKetiVo expectedKetiVo = new ExpectedKetiVo();
        expectedKetiVo.setUserNickName("");
        expectedKetiVo.setAccount("合计：");
        expectedKetiVo.setExpcted(String.valueOf(formatDouble(expcteds)));
        expectedKetiVo.setKeti(String.valueOf(formatDouble(ketis)));
        expectedKetiVo.setHeji(String.valueOf(formatDouble(expcteds+ketis)));
        list.add(expectedKetiVo);
        return list;
    }

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN  RoundingMode.UP->四舍五入
        nf.setRoundingMode(RoundingMode.DOWN);
        return nf.format(d);
    }

    @ResponseBody
    @RequestMapping(value = "shouge",method = RequestMethod.POST)
    public String shouge(){
            String userId = ((String) ST.cache.get(mainAccount)).split("@")[0];
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
            //运算商品
            for (String s:ziAccount){
                Map<String, String> header = ST.headers;
                String sessionId = ST.cache.get(s);
                header.put("sessionId", sessionId);
                String result1 = HttpClientUtil.sendHttpsPost(ST.tixian,null, ST.headers);
                log.info("yuqi result : {}", result1);
                JSONObject jsonObject2 = JSON.parseObject(result1);
                JSONObject data1 = jsonObject2.getJSONObject("data");
                //可提
                String keti = data1.getString("totalCash");
                double k=Double.valueOf(keti);
                if(k<=40D){
                    continue;
                }
                //计算商品
                Map<String,ProductVo> map=new HashMap<>();
                Map<String, ProductVo> skumap = getSku(map, productIdz, keti);
                List<ProductVo> skus = new ArrayList<>();
                skumap.forEach((a,v)->{
                    skus.add(v);
                });
                //加入购物车
                for(ProductVo p:skus){
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException var8) {
                        var8.printStackTrace();
                    }
                    String result2 = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/car/addCar", JsonUtil.toJson(p), ST.headers);
                    System.out.println(result2);
                }
                //获取账户信息->收货地址
                String s1 = HttpClientUtil.sendHttpsPost(ST.shdz,null, header);
                JSONObject json_shdz_data = JSON.parseObject(s1);
                String receiveId;
                JSONArray shdzList = json_shdz_data.getJSONArray("data");
                List<JSONObject> shdzs = shdzList.toJavaList(JSONObject.class);
                if(shdzs.size()<1){
                    return "收货地址不能为空";
                }else{
                    receiveId=shdzs.get(0).getString("receiveId");
                }
                System.out.println(receiveId);
                //下单
                OrderVo orderVo = new OrderVo();
                orderVo.setReceiveId(receiveId);
                orderVo.setSource(2);
                String s2 = Md5Util.encoderByMd5(payPassword);
                String pw="zhidianlife_2016"+s2;
                byte[] bytes = pw.getBytes();
                Base64.Encoder encoder = Base64.getEncoder();
                String s3 = encoder.encodeToString(bytes);
                orderVo.setPayPassword(s3);
                orderVo.setUseECard("0");
                orderVo.setIsUseBalance("0");
                orderVo.setUseVoucher("1");
                List<OrderVo.OrderShop> orderShops = new ArrayList<>();
                log.info(JSON.toJSONString(skus));
                OrderVo.OrderShop orderShop = new OrderVo.OrderShop();
                orderShop.setShopId(ST.cache.get(mainAccount).split("@")[0]);
                orderShop.setInvoiceRequired("0");
                orderShop.setLogisticsType("1");
                orderShop.setProducts(skus);
                orderShops.add(orderShop);
                orderVo.setShopList(orderShops);
                log.info(JsonUtil.toJson(orderVo));
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException var8) {
                    var8.printStackTrace();
                }
                String result2 = HttpClientUtil.sendHttpsPost(ST.addOrder, JsonUtil.toJson(orderVo), ST.headers);
                JSONObject order = JSON.parseObject(result2);
                if("000".equals(order.getString("result"))){
                    log.info("账户:{},已经成功将资金转移主号",s);
                }
                log.info(result2);
            }
            return "账户资金收集成功";

    }

    private Map<String,ProductVo> getSku(Map<String,ProductVo> skuMap,List<String> productIdz,String keti) {
        Map<String,ProductVo> sku;
        Double k=Double.valueOf(keti);
        Random rand = new Random();
        int size = productIdz.size();
        int myRand = rand.nextInt(size);
        String pid = (String) productIdz.get(myRand);
        ProductVo productVo = getProductVo(pid);
        Double dprice = productVo.getPrice().doubleValue();
        dprices=dprices+dprice;
        ProductVo productVo1 = skuMap.get(productVo.getSkuId());

        if(k-dprices>=0 && k-dprices<=40){

            if(productVo1 !=null){
                productVo1.setQuantity(productVo1.getQuantity()+1);
            }else {
                skuMap.put(productVo.getSkuId(),productVo);
            }
            dprices=0D;
            return skuMap;
        }else if(k-dprices>40){

            if(productVo1 !=null){
                productVo1.setQuantity(productVo1.getQuantity()+1);
            }else {
                skuMap.put(productVo.getSkuId(),productVo);
            }
            sku = getSku(skuMap, productIdz, keti);
        }else {
            dprices=dprices-dprice;
            sku = getSku(skuMap, productIdz, keti);
        }
        return sku;
    }

    //根据productId取得 ProductVo
    public ProductVo getProductVo(String productId){
        String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/commodityInfo/api/v1/mobile/detail", "{\"productId\":\"" + productId + "\"}", ST.headers);
        JSONObject resultJson = JSON.parseObject(result);
        JSONObject data = resultJson.getJSONObject("data");
        JSONObject product = data.getJSONObject("product");
        JSONObject sku = product.getJSONArray("skus").getJSONObject(0);
        JSONObject value = sku.getJSONObject("value");
        String price = value.getString("price");
        String skuId = value.getString("skuId");
        String saleType = product.getString("saleType");
        String shopId = product.getString("shopId");
        //Double dprice = Double.valueOf(price);
        ProductVo productVo = new ProductVo();
        productVo.setProductId(productId);
        productVo.setPrice(new BigDecimal(price));
        productVo.setSkuId(skuId);
        productVo.setSaleType(saleType);
        productVo.setShopId(shopId);
        productVo.setQuantity(1);
        return productVo;
    }
}
