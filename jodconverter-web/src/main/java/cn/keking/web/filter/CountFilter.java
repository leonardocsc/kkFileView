package cn.keking.web.filter;


import cn.keking.service.count.CountService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 计数器
 *
 * @author leizhi
 */
public class CountFilter implements Filter {


    /**
     * 租户身份标识
     *
     * @link https://support.huaweicloud.com/accessg-marketplace/zh-cn_topic_0093424531.html
     */
    public static final String INSTANCE_ID_HEADER = "X-APIGW-MKT-INSTANCEID";

    private final CountService countService;

    public CountFilter(CountService countService) {
        this.countService = countService;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String instanceId = httpServletRequest.getHeader(INSTANCE_ID_HEADER);
        if (StringUtils.isBlank(instanceId)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "INSTANCE_ID_HEADER NEED");
            return;
        }
        countService.incr(instanceId);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
