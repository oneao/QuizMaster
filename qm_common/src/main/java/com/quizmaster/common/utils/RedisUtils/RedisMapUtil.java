package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisMapUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
}
