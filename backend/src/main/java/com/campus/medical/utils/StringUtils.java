package com.campus.medical.utils;

import java.util.Collection;

/**
 * 字符串处理工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否为空
     *
     * @param str 待判断字符串
     * @return 如果字符串为null或空字符串则返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 待判断字符串
     * @return 如果字符串不为null且不为空字符串则返回true，否则返回false
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空白
     *
     * @param str 待判断字符串
     * @return 如果字符串为null或空字符串或只包含空白字符则返回true，否则返回false
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否不为空白
     *
     * @param str 待判断字符串
     * @return 如果字符串不为null且不为空白字符串则返回true，否则返回false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 去除字符串两端空白
     *
     * @param str 待处理字符串
     * @return 去除两端空白后的字符串
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 将集合元素用指定分隔符连接成字符串
     *
     * @param collection 待连接集合
     * @param separator  分隔符
     * @return 连接后的字符串
     */
    public static String join(Collection<?> collection, String separator) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : collection) {
            if (!first && separator != null) {
                sb.append(separator);
            }
            if (item != null) {
                sb.append(item.toString());
            }
            first = false;
        }
        return sb.toString();
    }

    /**
     * 将字符串按指定分隔符分割成数组
     *
     * @param str       待分割字符串
     * @param separator 分隔符
     * @return 分割后的字符串数组
     */
    public static String[] split(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return new String[0];
        }
        return str.split(java.util.regex.Pattern.quote(separator));
    }
}