package cn.keking.utils;

import cn.keking.config.ConfigConstants;
import cn.keking.huawei.HuaweiReport;
import cn.keking.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final HuaweiReport huaweiReport;

    public SchedulePushCount(HuaweiReport huaweiReport) {
        this.huaweiReport = huaweiReport;
    }

    /**
     * 默认每晚1点执行一次
     */
    @Scheduled(cron = "${huawei.push.cron:0 0 1 * * ?}")
    public void clean() {
        logger.info("begin push usage to huawei");
        huaweiReport.push();
        logger.info("push usage to huawei end");
    }
}
