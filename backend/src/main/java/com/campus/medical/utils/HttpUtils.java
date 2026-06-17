package com.campus.medical.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP 请求工具类
 */
@Component
public class HttpUtils {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @return 响应结果
     */
    public static String doGet(String url) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送带参数的 GET 请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应结果
     */
    public static String doGet(String url, Map<String, String> params) {
        try {
            // 构建带参数的URL
            StringBuilder urlBuilder = new StringBuilder(url);
            if (params != null && !params.isEmpty()) {
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (first) {
                        urlBuilder.append("?");
                        first = false;
                    } else {
                        urlBuilder.append("&");
                    }
                    urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
                }
            }
            
            ResponseEntity<String> response = restTemplate.exchange(
                urlBuilder.toString(), HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送 POST 请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应结果
     */
    public static String doPost(String url, Map<String, String> params) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    map.add(entry.getKey(), entry.getValue());
                }
            }
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送 JSON 格式的 POST 请求
     *
     * @param url 请求地址
     * @param json JSON 请求体
     * @return 响应结果
     */
    public static String doPostJson(String url, String json) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            
            HttpEntity<String> request = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送 PUT 请求
     *
     * @param url 请求地址
     * @param json JSON 请求体
     * @return 响应结果
     */
    public static String doPut(String url, String json) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            
            HttpEntity<String> request = new HttpEntity<>(json, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 发送 DELETE 请求
     *
     * @param url 请求地址
     * @return 响应结果
     */
    public static String doDelete(String url) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}