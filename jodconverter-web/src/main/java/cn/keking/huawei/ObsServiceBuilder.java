package cn.keking.huawei;

import cn.hutool.core.util.StrUtil;
import com.obs.services.ObsClient;

public final class ObsServiceBuilder {
    private String bucketName;
    private String accessKey;
    private String secretKey;
    private String endPoint;

    private ObsServiceBuilder() {
    }

    public static ObsServiceBuilder anObsService() {
        return new ObsServiceBuilder();
    }

    public ObsServiceBuilder withBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public ObsServiceBuilder withAccessKey(String accessKey) {
        this.accessKey = accessKey;
        return this;
    }

    public ObsServiceBuilder withSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public ObsServiceBuilder withEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public ObsService build() {
        if (StrUtil.isBlank(bucketName) ||
                StrUtil.isBlank(accessKey) ||
                StrUtil.isBlank(secretKey) ||
                StrUtil.isBlank(endPoint)) {
            throw new RuntimeException("obs参数错误");
        }
        ObsService obsService = new ObsService();
        obsService.setBucketName(bucketName);
        obsService.setObsClient(new ObsClient(accessKey, secretKey, endPoint));
        return obsService;
    }
}
