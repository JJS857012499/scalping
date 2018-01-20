package com.exp.demo.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.jhelp.mass.utils.StringKit;

import java.util.Map;


/**
 * JSON 工具类
 *
 * @author xianyongjie
 */
public class JsonUtil {

    public static ObjectMapper mapper = createObjectMapper();

    /**
     * 初始化ObjectMapper
     * @return
     */
    private static ObjectMapper createObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);

        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    /**
     * 将json转成java bean
     *
     * @param <T>   -- 多态类型
     * @param json  -- json字符串
     * @param clazz -- java bean类型(Class)
     * @return -- java bean对象
     */
    public static <T> T toBean(String json, Class<T> clazz) {

        if (StringKit.isBlank(json)) {
            return null;
        }
        T rtv = null;
        try {
            rtv = mapper.readValue(json, clazz);
        } catch (Exception ex) {
            throw new IllegalArgumentException("json字符串转成java bean异常", ex);
        }
        return rtv;
    }

    /**
     * JSON串转换为Java泛型对象（其中如果要转化为的是内部类，该内部类必须是static的，否则会出现异常）
     *
     * @param json JSON字符串
     * @param tr   例如: new TypeReference<List<FamousUser> >(){}
     * @return
     */
    public static <T> T toBean(String json, TypeReference<T> tr) {

        if (StringKit.isBlank(json)) {
            return null;
        }
        T rtv = null;
        try {
            rtv = mapper.readValue(json, tr);
        } catch (Exception ex) {
            throw new IllegalArgumentException("json将json字符串转化成对象出错", ex);
        }
        return rtv;
    }

    /**
     * 将java bean转成json
     *
     * @param bean -- java bean
     * @return -- json 字符串
     */
    public static String toJson(Object bean) {

        if (bean == null) {
            return null;
        }
        String rtv = null;
        try {
            rtv = mapper.writeValueAsString(bean);
        } catch (Exception ex) {
            throw new IllegalArgumentException("java bean转成json字符串异常", ex);
        }
        return rtv;
    }

    /**
     * 将java bean转成json
     *
     * @param obj
     * @param filterFields 需要排除的属性
     * @return
     */
    public static String toJsonWithSerializeAllExcept(Object obj,
                                                      String... filterFields) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            FilterProvider filters = new SimpleFilterProvider().addFilter(obj
                    .getClass().getName(), SimpleBeanPropertyFilter
                    .serializeAllExcept(filterFields)).setFailOnUnknownId(false);
            mapper.setFilterProvider(filters);
            //mapper.setFilters(filters);
            mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                public Object findFilterId(AnnotatedClass ac) {
                    return ac.getName();
                }
            });
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("java bean转成json字符串异常", e);
        }
    }

    /**
     * 将java bean转成json
     *
     * @param obj
     * @param filterFields 需要留下的属性
     * @return
     */
    public static String toJsonWithFilterOutAllExcept(Object obj,
                                                      String... filterFields) {
        if (obj == null) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            FilterProvider filters = new SimpleFilterProvider().addFilter(obj
                    .getClass().getName(), SimpleBeanPropertyFilter
                    .filterOutAllExcept(filterFields)).setFailOnUnknownId(false);
            mapper.setFilterProvider(filters);
            mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
                public Object findFilterId(AnnotatedClass ac) {
                    return ac.getName();
                }
            });
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new IllegalArgumentException("java bean转成json字符串异常", e);
        }
    }

    public static Map<String, Object> object2Map(Object o) {
        return mapper.convertValue(o,Map.class);
    }
}
