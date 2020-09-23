package cn.keking.service.count;

/**
 * @author leizhi
 */
public interface CountService {

    /**
     * 增加计数
     *
     * @param accountId 账户id
     * @return 当前调用次数
     */
    long incr(String accountId);

    /**
     * 调用次数
     *
     * @param accountId 账户id
     * @return 已调用次数
     */
    long queryCount(String accountId);
}
