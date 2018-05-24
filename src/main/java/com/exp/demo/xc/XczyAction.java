package com.exp.demo.xc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.exp.demo.service.SdService;
import com.exp.demo.utils.HttpClientUtil;
import com.exp.demo.utils.Md5Util;
import com.exp.demo.utils.ThreadPoolKit;
import com.exp.demo.vo.*;
import lombok.Data;
import net.jhelp.mass.utils.CollectionKit;
import net.jhelp.mass.utils.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@ConfigurationProperties(prefix = "sd.xc")
@RequestMapping({"/xc"})
@Scope("prototype")
public class XczyAction {
    static Random random;
    private static final Logger log;
    private Map<String, String> accountMap;
    private Map<String, String> lxzMap;
    private Map<String, String> csjMap;
    @Autowired
    private SdService sdService;

    @RequestMapping(value = {"index"}, method = {RequestMethod.GET})
    public String index(final HttpServletRequest request, final Model model) {
        final HttpSession session = request.getSession();
        final Object lxz = session.getAttribute("username");
        final List<String> list = new ArrayList<String>();
        list.add("==请选择==");
        if ("lxz".equals(String.valueOf(lxz))) {
            this.lxzMap.forEach((k, v) -> list.add(k));
            model.addAttribute("list", list);
        } else if ("csj".equals(String.valueOf(lxz))) {
            this.csjMap.forEach((k, v) -> list.add(k));
            model.addAttribute("list", list);
        } else {
            this.accountMap.forEach((k, v) -> list.add(k));
            model.addAttribute("list", list);
        }
        return "xc";
    }

    @ResponseBody
    @RequestMapping(value = {"getZaccount"}, method = {RequestMethod.POST})
    public String getZaccount(final String mainAccount) {
        String s = this.accountMap.get(mainAccount);
        if (StringUtils.isEmpty(s)) {
            s = this.lxzMap.get(mainAccount);
        }
        if (StringUtils.isEmpty(s)) {
            s = this.csjMap.get(mainAccount);
        }
        return s;
    }

    @RequestMapping({"loginxc"})
    @ResponseBody
    public String loginall(@RequestBody final XczyVo xczyVo) {
        return this.sdService.loginall(xczyVo);
    }

    @ResponseBody
    @RequestMapping(value = {"expectedKeti"}, method = {RequestMethod.POST})
    public List<ExpectedKetiVo> getExpectedKeti(@RequestBody final XczyVo xczyVo) {
        return this.sdService.getExpectedKeti(xczyVo);
    }

    @ResponseBody
    @RequestMapping(value = {"shouge"}, method = {RequestMethod.POST})
    public String shouge(@RequestBody final XczyVo xczyVo) {
        return this.sdService.shouge(xczyVo);
    }

    private ProductVo getSku(final String pid) {
        final String result = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/commodityInfo/api/v1/mobile/detail", "{\"productId\":\"" + pid + "\"}", XC.headers);
        final JSONObject resultJson = JSON.parseObject(result);
        final JSONObject data = resultJson.getJSONObject("data");
        final JSONObject product = data.getJSONObject("product");
        if (product == null) {
            return null;
        }
        final JSONObject sku = product.getJSONArray("skus").getJSONObject(0);
        final JSONObject value = sku.getJSONObject("value");
        final String price = value.getString("price");
        final String skuId = value.getString("skuId");
        XczyAction.log.info("pid : {}, skuId:{}, price:{}", new Object[]{pid, skuId, price});
        if (Double.parseDouble(price) > 350.0) {
            return null;
        }
        final ProductVo productVo = new ProductVo();
        productVo.setProductId(pid);
        productVo.setPrice(new BigDecimal(price));
        productVo.setSkuId(skuId);
        return productVo;
    }

    @RequestMapping(value = {"shouyi"}, method = {RequestMethod.POST})
    @ResponseBody
    public List<ShouYiVo> shouyi(@RequestBody final XczyVo xczyVo) {
        final String zhiAccount = xczyVo.getZhiAccount();
        final String[] account = zhiAccount.split(",");
        final List<String> lista = new ArrayList<String>();
        final String mainAccount = xczyVo.getMainAccount();
        lista.add(mainAccount);
        for (final String a : account) {
            lista.add(a);
        }
        final List<ShouYiVo> list = new ArrayList<ShouYiVo>();
        for (final String a2 : lista) {
            final Map<String, String> map = new HashMap<String, String>();
            map.put("phone", a2);
            final String url = "https://app.zhidianlife.com/mobile-account-service/ownerEarning/verifyWhite?phone=" + a2;
            final String s = HttpClientUtil.get(url, XC.headers);
            try {
                Thread.sleep(200L);
            } catch (InterruptedException var8) {
                var8.printStackTrace();
            }
            final ShouYiVo shouYiVo = (ShouYiVo) JSON.parseObject(s, ShouYiVo.class);
            shouYiVo.setAccount(a2);
            list.add(shouYiVo);
        }
        return list;
    }

    @RequestMapping(value = {"delivery"}, method = {RequestMethod.POST})
    @ResponseBody
    public String delivery(@RequestBody final XczyVo xczyVo) {
        return this.sdService.delivery(xczyVo);
    }

    @RequestMapping(value = {"finish"}, method = {RequestMethod.POST})
    @ResponseBody
    public String finish(@RequestBody final XczyVo xczyVo) {
        return this.sdService.finish(xczyVo);
    }

    public Map<String, String> getAccountMap() {
        return this.accountMap;
    }

    public void setAccountMap(final Map<String, String> accountMap) {
        this.accountMap = accountMap;
    }

    @RequestMapping({"order"})
    @ResponseBody
    public String order(@RequestBody final XczyVo xczyVo) throws Exception {
        final String accounts = xczyVo.getZhiAccount().replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("\n", "");
        final List<String> list = Arrays.asList(accounts.split(","));
        final List<String> tmp = new ArrayList<>();
        tmp.addAll(list);
        tmp.add(xczyVo.getMainAccount());
        final Vector<ProductVo> firstList = new Vector<>();
        final Vector<ProductVo> secList = new Vector<>();
        final Vector<ProductVo> thirdList_1 = new Vector<>();
        final Vector<ProductVo> thirdList_2 = new Vector<>();
        final Vector<ProductVo> thirdList_3 = new Vector<>();
        final String password = xczyVo.getLoginPassword();
        if (StringUtils.isEmpty(password)) {
            return "登录密码不能为空";
        }
        final List<String> re = CollectionKit.newArrayList();
        if (tmp.size() > 2) {
            String phone0 = "";
            String phone2 = "";
            String phone3 = "";
            phone0 = tmp.get(0);
            phone2 = tmp.get(1);
            phone3 = tmp.get(2);
            final long t1 = System.currentTimeMillis();
            this.sale(tmp, password, firstList, secList, thirdList_1, thirdList_2, thirdList_3);
            double total = 0.0;
            while (true) {
                final double total2 = firstList.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                final double total3 = secList.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                final double total4 = thirdList_1.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                final double total5 = thirdList_2.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                final double total6 = thirdList_3.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                total = total2 + total3 + total4 + total5 + total6;
                if (total >= 10000.0) {
                    break;
                }
                Thread.sleep(1000L);
            }
            XczyAction.log.info("计算时长：{}", System.currentTimeMillis() - t1);
            if (total > 10100.0) {
                return this.order(xczyVo);
            }
            Thread.sleep(3000L);
            double totalAmount = 0.0;
            final String receviceId0 = this.receviceId(phone2);
            String userId = XC.cache.get(phone2).split("@")[0];
            final Map<String, List<ProductVo>> listMap = this.listMap(firstList);
            String payResult = this.createOrder(phone2, receviceId0, userId, listMap, xczyVo);
            re.add(payResult + "<br/>");
            XczyAction.PayResult payResult2 = JSON.parseObject(payResult, PayResult.class);
            totalAmount += Double.parseDouble(payResult2.getAmount());
            final String receviceId2 = this.receviceId(phone3);
            userId = XC.cache.get(phone3).split("@")[0];
            payResult = this.createOrder(phone3, receviceId2, userId, this.listMap(secList), xczyVo);
            re.add(payResult + "<br/>");
            payResult2 = JSON.parseObject(payResult, PayResult.class);
            totalAmount += Double.parseDouble(payResult2.getAmount());
            final String receviceId3 = this.receviceId(phone0);
            userId = XC.cache.get(phone0).split("@")[0];
            payResult = this.createOrder(phone0, receviceId3, userId, this.listMap(thirdList_1), xczyVo);
            re.add(payResult + "<br/>");
            payResult2 = JSON.parseObject(payResult, PayResult.class);
            totalAmount += Double.parseDouble(payResult2.getAmount());
            final String receviceId4 = this.receviceId(phone0);
            userId = XC.cache.get(phone0).split("@")[0];
            payResult = this.createOrder(phone0, receviceId4, userId, this.listMap(thirdList_2), xczyVo);
            re.add(payResult + "<br/>");
            payResult2 = JSON.parseObject(payResult, PayResult.class);
            totalAmount += Double.parseDouble(payResult2.getAmount());
            final String receviceId5 = this.receviceId(phone0);
            userId = XC.cache.get(phone0).split("@")[0];
            payResult = this.createOrder(phone0, receviceId5, userId, this.listMap(thirdList_3), xczyVo);
            re.add(payResult + "<br/>");
            payResult2 = JSON.parseObject(payResult, PayResult.class);
            totalAmount += Double.parseDouble(payResult2.getAmount());
            re.add(String.format("计算下单总金额：%s <br/>", this.totalDouble(firstList) + this.totalDouble(secList) + this.totalDouble(thirdList_1) + this.totalDouble(thirdList_2) + this.totalDouble(thirdList_3)));
            re.add(String.format("最终下单总额：%s <br/>", totalAmount));
        }
        return re.toString();
    }

    private String receviceId(final String phone) {
        final String sessionId = XC.cache.get(phone);
        XC.headers.put("sessionId", sessionId);
        final String result = HttpClientUtil.sendHttpsPost(XC.receive_api_url, "", XC.headers);
        final JSONObject resultJson = JSON.parseObject(result);
        final JSONArray data = resultJson.getJSONArray("data");
        if (data.size() == 0) {
            return data.getJSONObject(0).getString("receiveId");
        }
        return data.getJSONObject(XczyAction.random.nextInt(data.size())).getString("receiveId");
    }

    //todo
    private String createOrder(final String phone, final String receiveId, final String userId, final Map<String, List<ProductVo>> listMap, final XczyVo xczyVo) {
        final XczyAction.OrderReq orderReq = new XczyAction.OrderReq();
        orderReq.setReceiveId(receiveId);
        orderReq.setUserId(userId);
        listMap.forEach((shopId, productVoList) -> {
            XczyAction.OrderReq.ShopVo shopVo = new XczyAction.OrderReq.ShopVo();
            shopVo.setShopId(shopId);
            orderReq.getShopList().add(shopVo);
            productVoList.forEach(productVo -> {
                XczyAction.OrderReq.ShopVo.Product product = new XczyAction.OrderReq.ShopVo.Product();
                product.setSkuId(productVo.getSkuId());
                product.setQuantity(productVo.getQuantity());
                shopVo.getProducts().add(product);
            });
        });
        final String result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v3/order/add", JSON.toJSONString(orderReq), XC.headers);
        final JSONObject jsonObject = JSON.parseObject(result);
        final String result2 = jsonObject.getString("result");
        if (!"000".equals(result2)) {
            XczyAction.log.info("下单失败 = {}", jsonObject.toJSONString());
            return this.canelOrder(xczyVo);
        }
        final JSONObject data = jsonObject.getJSONObject("data");
        final String orderId = data.getString("orderId");
        final BigDecimal payAmount = data.getBigDecimal("needPayAmount");
        final String payResult = String.format("用户ID：%s,订单号：%s,需要支付金额：%s! <br/>", userId, data.getString("orderId"), data.getBigDecimal("needPayAmount"));
        XczyAction.log.info(payResult);
        final XczyAction.PayResult payResult2 = new XczyAction.PayResult();
        payResult2.setPhone(phone);
        payResult2.setUserId(userId);
        payResult2.setOrderId(orderId);
        payResult2.setAmount(payAmount.toPlainString());
        return JSON.toJSONString(payResult2);
    }

    @RequestMapping(value = {"cancel"}, method = {RequestMethod.POST})
    @ResponseBody
    public String canelOrder(@RequestBody final XczyVo xczyVo) {
        XczyAction.log.info("取消订单开始。。。");
        final String accounts = xczyVo.getZhiAccount().replaceAll(" ", "").replaceAll("\r\n", "").replaceAll("\n", "");
        final List<String> list = Arrays.asList(accounts.split(","));
        final List<String> tmp = new ArrayList<>();
        tmp.addAll(list);
        tmp.add(xczyVo.getMainAccount());
        final String password = xczyVo.getLoginPassword();
        String result = "";
        final List<String> cancelList = new ArrayList<>();
        if (StringKit.isNotBlank(accounts)) {
            for (final String s : tmp) {
                final loginVo loginVo = new loginVo();
                if (StringKit.isNotBlank(password)) {
                    loginVo.setPassword(Md5Util.encoderByMd5(password));
                }
                loginVo.setPhone(s);
                result = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString(loginVo), XC.headers);
                XczyAction.log.info("phone = %s,login result : {}", s, result);
                final JSONObject jsonObject1 = JSON.parseObject(result);
                final JSONObject data = jsonObject1.getJSONObject("data");
                XczyAction.log.info("sessionId : {}", data.getString("sessionId"));
                XC.cache.put(s, data.getString("sessionId"));
                final Map<String, Object> orderParam = CollectionKit.newHashMap();
                orderParam.put("status", 0);
                orderParam.put("startPage", 1);
                orderParam.put("pageSize", 150);
                final Map<String, String> header = XC.headers;
                header.put("sessionId", XC.cache.get(s));
                result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/list", JSON.toJSONString(orderParam), XC.headers);
                XczyAction.log.info("order result : {}", result);
                final JSONObject orderResult = JSON.parseObject(result);
                final JSONArray orderList = orderResult.getJSONArray("data");
                for (int i = 0; i < orderList.size(); ++i) {
                    final JSONObject order = orderList.getJSONObject(i);
                    final Map<String, Object> deliverParam = CollectionKit.newHashMap();
                    deliverParam.put("orderId", order.getString("orderId"));
                    result = HttpClientUtil.sendHttpsPost("https://app.zhidianlife.com/life-h2h/order/apis/unity/v1/order/cancel", JSON.toJSONString(deliverParam), XC.headers);
                    result = result.concat(result);
                    XczyAction.log.info("deliver result : {}", result);
                }
                cancelList.add(String.format("phone = %s,订单数量= %s 取消成功！<br/>", s, orderList.size()));
            }
        }
        return cancelList.toString();
    }

    private Map<String, List<ProductVo>> listMap(final List<ProductVo> productVoList) {
        return productVoList.stream().collect(Collectors.groupingBy(ProductVo::getShopId));
    }

    private double totalDouble(final List<ProductVo> voList) {
        return voList.stream().mapToDouble(value -> value.getQuantity() * value.getPrice().doubleValue()).sum();
    }

    private void sale(final List<String> tmp, final String password, final Vector<ProductVo> firstList, final Vector<ProductVo> secList, final Vector<ProductVo> thirdList_1, final Vector<ProductVo> thirdList_2, final Vector<ProductVo> thirdList_3) {
        for (final String s : tmp) {
            final String phone = s;
            ThreadPoolKit.get().execute(() -> {
                JSONObject jsonObject1;
                JSONObject data;
                String userId;
                ShopProductVo shopProductVo;
                String result3;
                JSONObject resultJson;
                JSONObject data2;
                JSONArray commodityList;
                List<ProductVo> productIds;

                JSONObject p;
                String pid;
                ProductVo productVo;
                final List<ProductVo> innerFirstList;
                final List<ProductVo> innerSecList;
                final List<ProductVo> innerThirdList_1;
                final List<ProductVo> innerThirdList_2;
                final List<ProductVo> innerThirdList_3;
                final BigDecimal first;
                final BigDecimal sec;
                final BigDecimal totalPrice;
                final double pricce2;
                final BigDecimal totalPrice2;
                double pricce3;
                final BigDecimal totalPrice3;
                final double total;
                final double realTotal;
                final double all;


                String result = "";
                loginVo loginVo = new loginVo();
                if (StringKit.isNotBlank(password)) {
                    loginVo.setPassword(Md5Util.encoderByMd5(password));
                }
                loginVo.setPhone(phone);
                String result2 = HttpClientUtil.sendHttpsPost("https://account.zhidianlife.com/passport/api/mobile/login", JSON.toJSONString(loginVo), XC.headers);
                XczyAction.log.info("phone = %s,login result : {}", phone, result2);
                jsonObject1 = JSON.parseObject(result2);
                data = jsonObject1.getJSONObject("data");
                XczyAction.log.info("sessionId : {}", (Object) data.getString("sessionId"));
                XC.cache.put(phone, data.getString("sessionId"));
                userId = XC.cache.get(phone).split("@")[0];
                shopProductVo = new ShopProductVo();
                shopProductVo.setShopId(userId);
                result3 = HttpClientUtil.sendHttpsPost("https://s1.zhidianlife.com/search/api/v2/commodity/searchByShop", JSON.toJSONString(shopProductVo), XC.headers);
                resultJson = JSON.parseObject(result3);
                data2 = resultJson.getJSONObject("data");
                commodityList = data2.getJSONArray("commodityList");
                productIds = CollectionKit.newArrayList();
                for (int i = 0; i < commodityList.size(); ++i) {
                    p = commodityList.getJSONObject(i);
                    pid = p.getString("productId");
                    productVo = this.getSku(pid);
                    if (productVo != null) {
                        productIds.add(productVo);
                    }
                }
                productIds.sort(Comparator.comparing(ProductVo::getPrice));
                innerFirstList = new ArrayList<>();
                innerSecList = new ArrayList<>();
                innerThirdList_1 = new ArrayList<>();
                innerThirdList_2 = new ArrayList<>();
                innerThirdList_3 = new ArrayList<>();
                first = this.choicePrice(userId, this.deepCopy(productIds), innerFirstList);
                sec = this.choicePrice(userId, this.deepCopy(productIds), innerSecList);
                totalPrice = new BigDecimal(200).add(first.add(sec));
                this.choicePrice(totalPrice, userId, this.deepCopy(productIds), innerThirdList_1, first.add(sec));
                pricce2 = first.doubleValue() + sec.doubleValue() + innerThirdList_1.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                totalPrice2 = new BigDecimal(180);
                this.choicePrice(totalPrice2, userId, this.deepCopy(productIds), innerThirdList_2, new BigDecimal(0));
                pricce3 = pricce2 + innerThirdList_2.stream().mapToDouble(value -> value.getPrice().doubleValue() * value.getQuantity()).sum();
                totalPrice3 = new BigDecimal(1000.0 - pricce3);
                if (totalPrice3.doubleValue() < 0.0) {
                    pricce3 = 1 / 0;
                }
                this.choicePrice(totalPrice3, userId, this.deepCopy(productIds), innerThirdList_3, new BigDecimal(0));
                total = pricce3 + this.totalDouble(innerThirdList_3);
                realTotal = first.doubleValue() + sec.doubleValue() + this.totalDouble(innerThirdList_1) + this.totalDouble(innerThirdList_2) + this.totalDouble(innerThirdList_3);
                XczyAction.log.info(String.format("\u624b\u673a\u53f7%s\u4e0b\u5355\u603b\u91d1\u989d\uff1a%s -- %s", phone, total, realTotal));
                firstList.addAll(innerFirstList);
                secList.addAll(innerSecList);
                thirdList_1.addAll(innerThirdList_1);
                thirdList_2.addAll(innerThirdList_2);
                thirdList_3.addAll(innerThirdList_3);
                all = this.totalDouble(firstList) + this.totalDouble(secList) + this.totalDouble(thirdList_1) + this.totalDouble(thirdList_2) + this.totalDouble(thirdList_3);
                XczyAction.log.info("总金额 = {}", all);
            });
        }
    }

    private BigDecimal choicePrice(final String shopId, final List<ProductVo> productInfoList, final List<ProductVo> resultList) {
        final int a = XczyAction.random.nextInt(2);
        final ProductVo productInfo1 = productInfoList.get(a);
        productInfo1.setShopId(shopId);
        resultList.add(productInfo1);
        return productInfo1.getPrice();
    }

    private List<ProductVo> deepCopy(final List<ProductVo> productVoList) {
        final List<ProductVo> list = CollectionKit.newArrayList();
        final ProductVo productVo2;
        final List<ProductVo> list2;
        productVoList.forEach(productVo -> {
            productVo2 = new ProductVo();
            BeanUtils.copyProperties((Object) productVo, (Object) productVo2);
            list2.add(productVo2);
            return;
        });
        return list;
    }

    private void choicePrice(BigDecimal totalPrice, final String shopId, final List<ProductVo> productInfoList, final List<ProductVo> resultList, BigDecimal price) {
        final Iterator<ProductVo> iterable = productInfoList.iterator();
        while (productInfoList.size() > 2 && iterable.hasNext()) {
            if (iterable.next().getPrice().doubleValue() - totalPrice.doubleValue() > 1.0) {
                iterable.remove();
            }
        }
        final ProductVo productInfo2 = this.genProductInfo(productInfoList, resultList);
        productInfo2.setShopId(shopId);
        totalPrice = totalPrice.subtract(price);
        final BigDecimal yue = totalPrice.subtract(productInfo2.getPrice());
        if (yue.doubleValue() < 0.0) {
            if (resultList.contains(productInfo2)) {
                productInfo2.setQuantity(productInfo2.getQuantity() + 1);
            } else {
                final ProductVo productVo = this.getMinData(totalPrice, productInfoList);
                productVo.setShopId(shopId);
                resultList.add(productVo);
            }
            return;
        }
        final int maxSize = totalPrice.divide(productInfo2.getPrice(), 2, 4).intValue();
        int butQty = XczyAction.random.nextInt((maxSize > 4) ? (maxSize / 2) : maxSize);
        butQty = ((butQty == 0) ? 1 : butQty);
        final BigDecimal fege = price = productInfo2.getPrice().multiply(new BigDecimal(butQty));
        XczyAction.log.info("\u9700\u8981\u5206\u5272\u7684\u94b1:{}\uff0c\u5355\u4ef7:{},qty:{},\u5206\u5272\u7684\u94b1\uff1a{},\u5269\u4e0b\u7684\u94b1:{}", new Object[]{totalPrice.toPlainString(), productInfo2.getPrice(), butQty, fege.toPlainString(), totalPrice.subtract(fege)});
        if (resultList.contains(productInfo2)) {
            productInfo2.setQuantity(productInfo2.getQuantity() + butQty);
        } else {
            productInfo2.setQuantity(butQty);
            resultList.add(productInfo2);
        }
        if (totalPrice.subtract(fege).doubleValue() > 0.0) {
            this.choicePrice(totalPrice, shopId, productInfoList, resultList, price);
        }
    }

    private ProductVo getMinData(final BigDecimal totalPrice, final List<ProductVo> productInfoList) {
        for (final ProductVo productVo : productInfoList) {
            if (productVo.getPrice().compareTo(totalPrice) > 0) {
                return productVo;
            }
        }
        return null;
    }

    private ProductVo genProductInfo(final List<ProductVo> productInfoList, final List<ProductVo> resultList) {
        final ProductVo ProductVo = productInfoList.get(XczyAction.random.nextInt(productInfoList.size()));
        if (productInfoList.size() <= resultList.size()) {
            return productInfoList.get(0);
        }
        if (resultList.contains(ProductVo)) {
            return this.genProductInfo(productInfoList, resultList);
        }
        return ProductVo;
    }

    public Map<String, String> getLxzMap() {
        return this.lxzMap;
    }

    public void setLxzMap(final Map<String, String> lxzMap) {
        this.lxzMap = lxzMap;
    }

    public Map<String, String> getCsjMap() {
        return this.csjMap;
    }

    public void setCsjMap(final Map<String, String> csjMap) {
        this.csjMap = csjMap;
    }

    static {
        XczyAction.random = new Random();
        log = LoggerFactory.getLogger(XczyAction.class);
    }

    @Data
    public static class PayResult {
        private String phone;
        private String userId;
        private String orderId;
        private String amount;
    }

    @Data
    public static class OrderReq {
        private String appKey;
        private String appVersion;
        private String clientType;
        private String distributorId;
        private String isUseBalance;
        private String receiveId;
        private List<ShopVo> shopList;
        private int source;
        private String useECard;
        private String useVoucher;
        private String userId;
        private String userLocationProvinceCode;
        private String[] vouchers;
        private String payPassword;

        public OrderReq() {
            this.appKey = "android";
            this.appVersion = "appVersion";
            this.clientType = "clientType";
            this.distributorId = "";
            this.isUseBalance = "1";
            this.shopList = new ArrayList<ShopVo>();
            this.source = 1;
            this.useECard = "0";
            this.useVoucher = "0";
            this.userLocationProvinceCode = "440000";
        }


        @Data
        public static class ShopVo {
            private String drawTimes;
            private String invoiceRequired;
            private String logisticsType;
            private String message;
            private List<ShopVo.Product> products;
            private String shopId;

            public ShopVo() {
                this.drawTimes = "";
                this.invoiceRequired = "0";
                this.logisticsType = "1";
                this.message = "";
                this.products = CollectionKit.newArrayList();
            }

            @Data
            static class Product {
                private Integer quantity;
                private String saleType;
                private String skuId;

                public Product() {
                    this.saleType = "1";
                }
            }

        }
    }
}
