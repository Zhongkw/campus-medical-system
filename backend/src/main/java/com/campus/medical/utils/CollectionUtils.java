package com.campus.medical.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 集合处理工具类
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空
     *
     * @param collection 待判断集合
     * @return 如果集合为null或空集合则返回true，否则返回false
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     *
     * @param collection 待判断集合
     * @return 如果集合不为null且不为空集合则返回true，否则返回false
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map是否为空
     *
     * @param map 待判断Map
     * @return 如果Map为null或空Map则返回true，否则返回false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断Map是否不为空
     *
     * @param map 待判断Map
     * @return 如果Map不为null且不为空Map则返回true，否则返回false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 获取集合第一个元素
     *
     * @param collection 待获取集合
     * @return 集合的第一个元素，如果集合为空则返回null
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.iterator().next();
    }

    /**
     * 获取集合最后一个元素
     *
     * @param collection 待获取集合
     * @return 集合的最后一个元素，如果集合为空则返回null
     */
    public static <T> T getLast(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        Object[] array = collection.toArray();
        return (T) array[array.length - 1];
    }
}