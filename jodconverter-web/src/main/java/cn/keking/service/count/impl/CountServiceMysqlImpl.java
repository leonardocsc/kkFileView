package cn.keking.service.count.impl;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.db.ds.GlobalDSFactory;
import cn.hutool.db.ds.druid.DruidDSFactory;
import cn.hutool.setting.Setting;
import cn.keking.service.count.CountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * 简单的mysql持久化工具，参考文档见链接
 *
 * @author leizhi
 * @link https://hutool.cn/docs/#/db/%E6%95%B0%E6%8D%AE%E5%BA%93%E7%AE%80%E5%8D%95%E6%93%8D%E4%BD%9C-Db
 */
@Service
@ConditionalOnExpression("'${count.type:default}'.equals('mysql')")
public class CountServiceMysqlImpl implements CountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountServiceMysqlImpl.class);

    static {
        Setting setting = new Setting()
                .set("url", "jdbc:mysql://localhost:3306/file_preview")
                .set("username", "root").set("password", "r01ah3Tun7Lo")
                .set("driver", "com.mysql.jdbc.Driver").set("showSql", "true")
                .set("formatSql", "true").set("sqlLevel", "info");
        DSFactory dsFactory = new DruidDSFactory(setting);
        GlobalDSFactory.set(dsFactory);
    }

    @Override
    public long incr(String instanceId) {
        try {
            Entity record = Entity.create("t_request_statistics")
                    .set("instance_id", instanceId);

            return Db.use().insert(record);
        } catch (SQLException throwables) {
            LOGGER.error("插入数据异常", throwables);
        }
        return 0;
    }

    @Override
    public long queryCount(String accountId) {

        return 0;
    }
}
