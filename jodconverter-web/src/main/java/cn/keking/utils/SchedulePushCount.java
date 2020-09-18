package cn.keking.utils;

import cn.keking.config.ConfigConstants;
import cn.keking.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时推送调用次数到华为云
 *
 * @author leizhi
 */
@Component
@ConditionalOnExpression("'${huwei.push.enabled:false}'.equals('true')")
public class SchedulePushCount {

    private final Logger logger = LoggerFactory.getLogger(SchedulePushCount.class);

    private final CacheService cacheService;

    public SchedulePushCount(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    private final String fileDir = ConfigConstants.getFileDir();

    //默认每晚3点执行一次
    @Scheduled(cron = "${huawei.push.cron:0 0 3 * * ?}")
    public void clean() {
        logger.info("Cache clean start");
        cacheService.cleanCache();
        DeleteFileUtil.deleteDirectory(fileDir);
        logger.info("Cache clean end");
    }
}
