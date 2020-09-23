package cn.keking.service.count.impl;

import cn.keking.service.count.CountService;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

/**
 * redis存储计数信息
 *
 * @author leizhi
 */
@ConditionalOnExpression("'${count.type:default}'.equals('redis')")
@Service
public class CountServiceRedisImpl implements CountService {

    private static final String COUNT_NUM = "count_num";

    private final RedissonClient redissonClient;

    public CountServiceRedisImpl(Config config) {
        this.redissonClient = Redisson.create(config);
    }

    @Override
    public long incr(String accountId) {
        RAtomicLong a = redissonClient.getAtomicLong(getAccountCountKey(accountId));
        return a.getAndAdd(1);
    }

    @Override
    public long queryCount(String accountId) {
        RAtomicLong a = redissonClient.getAtomicLong(getAccountCountKey(accountId));
        return a.get();
    }

    private static String getAccountCountKey(String accountId) {
        return COUNT_NUM + ":" + accountId;
    }
}
