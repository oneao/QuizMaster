package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RedisHashUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

    /**
     * 获取多个 Hash 中的数据。
     *
     * @param key   Redis 主键
     * @param hKeys Hash 键集合
     * @param <T>   返回值的泛型类型
     * @return List<T> 泛型类型的对象集合
     */
    public <T> List<T> getMultiCacheHashValue(final String key, final Collection<Object> hKeys, Class<T> valueType) {
        List<Object> rawValues = redisTemplate.opsForHash().multiGet(key, hKeys);

        // 使用 stream 进行类型安全的转换
        return rawValues.stream()
                .filter(valueType::isInstance)
                .map(valueType::cast)
                .collect(Collectors.toList());
    }

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

}
