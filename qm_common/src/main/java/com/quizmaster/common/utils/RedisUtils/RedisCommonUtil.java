package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedisCommonUtil<T> {
    @Autowired
    private RedisTemplate<String, T> redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public void setCache(final String key, final T value) {
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
    public void setCache(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 根据键值删除缓存对象
     *
     * @param key 键值
     * @return true 删除成功 false 删除失败
     */
    public boolean deleteCache(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 删除缓存中的多个对象。
     *
     * @param keys 缓存键值的集合
     * @return 成功删除的键的数量
     */
    public boolean deleteCaches(final Collection<String> keys) {
        Long delete = redisTemplate.delete(keys);
        return delete != null && delete > 0;
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false 不存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     *
     * @param key      键值
     * @param timeout  时间
     * @param timeUnit 时间单位
     * @return true 设置成功 false 设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
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
    public Optional<T> getCache(final String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        } else {
            return Optional.empty();
        }
    }

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
