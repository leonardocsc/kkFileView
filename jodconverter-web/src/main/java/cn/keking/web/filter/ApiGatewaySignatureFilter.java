package cn.keking.web.filter;

import com.cloud.apigateway.sdk.utils.Request;
import com.cloud.sdk.auth.signer.Signer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验请求是否来自华为云网关
 *
 * @author leizhi
 * @link https://support.huaweicloud.com/devg-apig/apig-zh-dev-180307037.html#section3
 * @link https://support.huaweicloud.com/usermanual-apig/apig-zh-ug-180307041.html
 */
public class ApiGatewaySignatureFilter implements Filter {

    public static final int DURATION = 15 * 60 * 1000;

    /** 秘钥对*/
    private static Map<String, String> SECRETS = new HashMap<>();

    /** 授权字符串格式匹配正则 */
    private static final Pattern AUTHORIZATION_PATTERN =
            Pattern.compile("SDK-HMAC-SHA256\\s+Access=([^,]+),\\s?SignedHeaders=([^,]+),\\s?Signature=(\\w+)");

    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");


    @Override
    public void init(FilterConfig filterConfig) {
        SECRETS.put("ba17a5b7ef5a4ac5a810351cfd49b4be", "fede7372ab514ec2945f639f0e01a7a1");
        TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            RequestWrapper request = new RequestWrapper((HttpServletRequest) servletRequest);
            String authorization = request.getHeader("Authorization");
            if (authorization == null || authorization.length() == 0) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization not found.");
                return;
            }

            Matcher m = AUTHORIZATION_PATTERN.matcher(authorization);
            if (!m.find()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization format incorrect.");
                return;
            }

            String signingKey = m.group(1);
            String signingSecret = SECRETS.get(signingKey);
            if (signingSecret == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Signing key not found.");
                return;
            }

            Request apiRequest = new Request();
            apiRequest.setMethod(request.getMethod());
            String url = request.getRequestURL().toString();
            String queryString = request.getQueryString();
            if (null != queryString && !"".equals(queryString)) {
                url = url + "?" + queryString;
            }
            apiRequest.setUrl(url);
            boolean needBody = true;
            String dateHeader = null;
            String[] signedHeaders = m.group(2).split(";");
            for (String signedHeader : signedHeaders) {
                String headerValue = request.getHeader(signedHeader);
                if (headerValue == null || headerValue.length() == 0) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Signed header" + signedHeader + " not found.");
                } else {
                    // set header
                    apiRequest.addHeader(signedHeader, headerValue);
                    if ("x-sdk-content-sha256".equals(signedHeader.toLowerCase()) && "UNSIGNED-PAYLOAD".equals(headerValue)) {
                        needBody = false;
                    }
                    if ("x-sdk-date".equals(signedHeader.toLowerCase())) {
                        dateHeader = headerValue;
                    }
                }
            }

            if (dateHeader == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Header x-sdk-date not found.");
                return;
            }

            // verify date
            Date date = TIME_FORMATTER.parse(dateHeader);
            long duration = Math.abs(System.currentTimeMillis() - date.getTime());
            if (duration > DURATION) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Signature expired.");
                return;
            }

            // set body
            if (needBody) {
                apiRequest.setBody(new String(request.getBody(), StandardCharsets.UTF_8));
            } else {
                apiRequest.setBody("");
            }
            apiRequest.addHeader("authorization", authorization);
            apiRequest.setKey(signingKey);
            apiRequest.setSecret(signingSecret);
            Signer signer = new Signer();
            boolean verify = signer.verify(apiRequest);

            if (!verify) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Verify authroization failed.");
                return;
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {

    }
}
