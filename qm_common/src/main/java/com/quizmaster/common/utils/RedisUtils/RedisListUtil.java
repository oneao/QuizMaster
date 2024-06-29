package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisListUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

}
