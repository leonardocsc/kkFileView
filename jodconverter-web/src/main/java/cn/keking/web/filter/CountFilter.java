package cn.keking.web.filter;


import cn.keking.service.count.CountService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * 计数器
 */
public class CountFilter implements Filter {

    private final CountService countService;

    public CountFilter(CountService countService) {
        this.countService = countService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accountId = request.getParameter("accountId");
        if (StringUtils.isBlank(accountId)) {
            accountId = "test";
        }
        countService.incr(accountId);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
