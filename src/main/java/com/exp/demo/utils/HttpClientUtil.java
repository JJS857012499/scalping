package com.exp.demo.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class HttpClientUtil {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static RestTemplate restTemplate = new RestTemplate();
    private static Logger logger = LogManager.getLogger(HttpClientUtil.class);

    static {
        // 反序列化时，忽略不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);

            conn.setConnectTimeout(5000); //连接超时
            conn.setReadTimeout(10000);   //读取超时           

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("连接失败，请稍候再试");
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String sendHttpPost(String httpUrl, String param) {
        BufferedReader in = null;
        String result = "";
        PrintWriter out = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);

            conn.setConnectTimeout(10 * 1000); //连接超时
            conn.setReadTimeout(30 * 1000);   //读取超时

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接失败，请稍候再试");
        } finally {
            if (in != null) {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    public static String sendHttpsPost(String httpUrl, String param, Map<String, String> headers) {
        BufferedReader in = null;
        String result = "";
        PrintWriter out = null;
        HttpsURLConnection conn = null;
        InputStream is = null;
        try {
            TrustManager[] tm = {new MyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(httpUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
            if(headers != null && headers.size() > 0){
                for(String key : headers.keySet()){
                    conn.setRequestProperty(key, headers.get(key));
                }
            }
            conn.setConnectTimeout(10 * 1000); //连接超时
            conn.setReadTimeout(30 * 1000);   //读取超时

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("连接失败，请稍候再试");
        } finally {
            if (in != null) {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return result;
    }

    public static <T> T sendPost(String host, String uri, Object params, Class<T> clazz) {
        return sendPost(host + uri, params, clazz);
    }

    public static <T> T sendPost(String host, String uri, Object params, TypeReference<T> type) {
        return sendPost(host + uri, params, type);
    }

    public static <T> T sendPost(String url, Object params, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> requestEntity = null;
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(params);
            requestEntity = new HttpEntity<>(requestBody, headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("request-url: " + url);
        logger.info("request-body: " + requestBody);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseBody = responseEntity.getBody();

        logger.info("response-body: " + responseBody);

        try {
            return objectMapper.readValue(responseBody, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T sendPost(String url, Object params, TypeReference<T> type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<String> requestEntity = null;
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(params);
            requestEntity = new HttpEntity<>(requestBody, headers);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("request-url: " + url);
        logger.info("request-body: " + requestBody);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        String responseBody = responseEntity.getBody();

        logger.info("response-body: " + responseBody);

        try {
            return objectMapper.readValue(responseBody, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * call apix service
     *
     * @param httpUrl
     * @param headerValues
     * @return
     */
    public static String get(String httpUrl, Map<String, String> headerValues) {
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 填入apix-key到HTTP header
            if (null != headerValues && headerValues.size() > 0) {
                for (String key : headerValues.keySet()) {
                    //connection.setRequestProperty("apix-key", apixKey);
                    connection.setRequestProperty(key, headerValues.get(key));
                }
            }
            connection.connect();

            is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

}
