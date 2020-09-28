package cn.keking.huawei;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObsServiceContext {

    private static final Map<String, ObsService> OBS_SERVICE_MAP = new ConcurrentHashMap<>();

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
