package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisStringUtil {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 设置字符串缓存
     *
     * @param key   键
     * @param value 值
     */
    public void setCacheString(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串缓存并指定过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void setCacheString(final String key, final String value, final long timeout, final TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取字符串缓存
     *
     * @param key 键
     * @return 缓存的值
     */
    public String getCacheString(final String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
