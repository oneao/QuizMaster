package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
@Component
public class RedisSetUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
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
}
