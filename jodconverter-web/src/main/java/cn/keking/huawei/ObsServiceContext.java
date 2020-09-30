package cn.keking.huawei;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObsServiceContext {

    private static final Map<String, ObsService> OBS_SERVICE_MAP = new ConcurrentHashMap<>();

    /**
     * 仅限web容器存在时使用
     */
    public static ObsService getObsService() {
        RequestAttributes req = RequestContextHolder.currentRequestAttributes();
        Object bucketNameObj = req.getAttribute("bucketName", 0);
        Object accessKeyObj = req.getAttribute("accessKey", 0);
        Object secretKeyObj = req.getAttribute("secretKey", 0);
        Object endPointObj = req.getAttribute("endPoint", 0);
        String bucketName = Convert.toStr(bucketNameObj, "pre");
        String accessKey = Convert.toStr(accessKeyObj, "FIGOLTWBOHLWUCET7BYH");
        String secretKey = Convert.toStr(secretKeyObj, "HBWwhFN9EX6yfHjY8j7Pxl9H4yLIDyTlDHrVpBvP");
        String endPoint = Convert.toStr(endPointObj, "obs.cn-north-4.myhuaweicloud.com");
        return getObsService(bucketName, accessKey, secretKey, endPoint);
    }

    public static ObsService getObsService(String bucketName, String accessKey, String secretKey, String endPoint) {
        String str = StrUtil.join(",", bucketName, accessKey, secretKey, endPoint);
        String key = SecureUtil.md5(str);

        synchronized (ObsServiceContext.class) {
            if (OBS_SERVICE_MAP.get(key) == null) {
                ObsService obsService = ObsServiceBuilder.anObsService()
                        .withAccessKey(accessKey)
                        .withEndPoint(endPoint)
                        .withBucketName(bucketName)
                        .withSecretKey(secretKey)
                        .build();
                OBS_SERVICE_MAP.put(key, obsService);
                return obsService;
            } else {
                return OBS_SERVICE_MAP.get(key);
            }
        }
    }

}
