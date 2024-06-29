package com.quizmaster.common.utils.RedisUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisZSetUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 向有序集合添加成员和对应的分数
     *
     * @param key    键
     * @param member 成员
     * @param score  分数
     * @return true 添加成功，false 添加失败
     */
    public boolean addToZSet(final String key, final Object member, final double score) {
        return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, member, score));
    }

    /**
     * 获取有序集合中指定成员的分数
     *
     * @param key    键
     * @param member 成员
     * @return 成员的分数
     */
    public Double getScoreFromZSet(final String key, final Object member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    /**
     * 获取有序集合中指定范围内的成员
     *
     * @param key   键
     * @param start 起始索引
     * @param end   结束索引
     * @return 指定范围内的成员集合
     */
    public Set<Object> rangeFromZSet(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 删除有序集合中的指定成员
     *
     * @param key    键
     * @param member 成员
     * @return 删除的成员数量
     */
    public Long removeFromZSet(final String key, final Object member) {
        return redisTemplate.opsForZSet().remove(key, member);
    }
}
