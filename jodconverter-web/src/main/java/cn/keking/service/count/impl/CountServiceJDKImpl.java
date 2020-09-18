package cn.keking.service.count.impl;

import cn.keking.service.count.CountService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jdk缓存计数器
 */
@Service
@ConditionalOnExpression("'${cache.type:default}'.equals('jdk')")
public class CountServiceJDKImpl implements CountService {

    private static final Map<String, Long> map = new ConcurrentHashMap<>();

    @Override
    public long incr(String accountId) {
        synchronized (CountServiceJDKImpl.class) {
            Long num = map.get(accountId);
            num = num == null ? 1 : (num + 1);
            map.put(accountId, num);
            return num;
        }
    }

    @Override
    public long queryCount(String accountId) {
        Long num = map.get(accountId);
        return num == null ? 0 : num;
    }
}
