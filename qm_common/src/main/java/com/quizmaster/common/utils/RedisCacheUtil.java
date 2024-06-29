package com.quizmaster.common.utils;

import cn.hutool.db.nosql.redis.RedisDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisCacheUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
    }


    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    /**
     * 获得缓存的基本对象。
     *
     * @param key  缓存键值
     * @param type 返回类型的 Class 对象
     * @param <T>  泛型类型
     * @return 缓存键值对应的数据
     * @throws ClassCastException 如果类型不匹配
     *                            Optional使用方法：
     *                            1. 使用 orElse 获取值。如果缓存中没有对应的值，返回默认值：
     *                            String value = getCacheObject("myKey", String.class).orElse("default value");
     *                            2. 使用 orElseGet 获取值。如果缓存中没有对应的值，通过指定的供应商方法获取默认值：
     *                            String value = getCacheObject("myKey", String.class).orElseGet(() -> "default value from supplier");
     *                            3. 使用 orElseThrow 获取值。如果缓存中没有对应的值，抛出指定异常：
     *                            String value = getCacheObject("myKey", String.class).orElseThrow(() -> new RuntimeException("Value not found"));
     *                            4. 使用 ifPresent 执行操作。如果值存在，执行指定的操作：
     *                            getCacheObject("myKey", String.class).ifPresent(value -> System.out.println("Value: " + value));
     *                            5. 使用 map 转换值。如果值存在，应用指定的函数并返回结果的 Optional：
     *                            Optional<Integer> length = getCacheObject("myKey", String.class).map(String::length);
     *                            6. 使用 filter 过滤值。如果值存在且满足指定的条件，返回该值的 Optional，否则返回 Optional.empty()：
     *                            Optional<String> filteredValue = getCacheObject("myKey", String.class).filter(value -> value.startsWith("prefix"));
     */
    public <T> Optional<T> getCacheObject(final String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        } else {
            return Optional.empty();
        }
    }
    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
    /**
     * 删除缓存中的多个对象。
     *
     * @param keys 缓存键值的集合
     * @return 成功删除的键的数量
     */
    public boolean deleteObjects(final Collection<String> keys) {
        Long delete = redisTemplate.delete(keys);
        return delete != null && delete > 0;
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> boolean setCacheList(final String key, final List<Object> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count != null && count > 0;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key, Class<T> elementType) {
        List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
        if (list == null) {
            return null;
        }

        return list.stream()
                .map(elementType::cast) // 显式类型转换
                .collect(Collectors.toList());
    }
    /**
     * 设置缓存中的集合对象。
     *
     * @param key     缓存键
     * @param dataSet 要存入的数据集合
     * @param <T>     数据类型
     * @return boolean 表示是否成功
     */
    public <T> boolean setCacheSet(final String key, final Set<T> dataSet) {

        BoundSetOperations<String, Object> rawSetOperation = redisTemplate.boundSetOps(key);
        boolean success = true;
        for (T t : dataSet) {
            Long result = rawSetOperation.add(t);
            if (result == null || result == 0) {
                return false;
            }
        }
        return success;
    }
//
    /**
     * 获取缓存中的 Set 数据。
     *
     * @param key 缓存键
     * @param <T> 元素类型
     * @return Set<T> 泛型类型的 Set 数据
     */
    public <T> Set<T> getCacheSet(final String key, Class<T> elementType) {
        Set<Object> rawSet = redisTemplate.opsForSet().members(key);
        if (rawSet == null) {
            return null; // 或者根据业务需求返回空集合，这里假设返回 null 表示未找到对应的缓存
        }

        // 使用 stream 进行类型安全的转换
        return rawSet.stream()
                .filter(elementType::isInstance)
                .map(elementType::cast)
                .collect(Collectors.toSet());
    }
//
    /**
     * 缓存Map
     *
     * @param key：键名
     * @param dataMap：键值
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }
//
//
    /**
     * 获取缓存中的 Map 数据。
     *
     * @param key 缓存键
     * @param <T> 值的泛型类型
     * @return Map<String, T> 泛型类型的 Map 数据
     */
    public <T> Map<String, T> getCacheMap(final String key, Class<T> valueType) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        // 创建一个新的 Map 用于存放转换后的数据
        Map<String, T> typedMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            if (entry.getKey() instanceof String typedKey && valueType.isInstance(entry.getValue())) {
                T typedValue = valueType.cast(entry.getValue());
                typedMap.put(typedKey, typedValue);
            }
        }

        return typedMap;
    }
//
    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }
//
    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }
//
    /**
     * 获取多个 Hash 中的数据。
     *
     * @param key   Redis 主键
     * @param hKeys Hash 键集合
     * @param <T>   返回值的泛型类型
     * @return List<T> 泛型类型的对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys, Class<T> valueType) {
        List<Object> rawValues = redisTemplate.opsForHash().multiGet(key, hKeys);

        // 使用 stream 进行类型安全的转换
        return rawValues.stream()
                .filter(valueType::isInstance)
                .map(valueType::cast)
                .collect(Collectors.toList());
    }
//
    /**
     * 删除Hash中的某条数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(final String key, final String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }
//
    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }
}
