package cn.keking.huawei;


import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectResult;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class ObsService {

    private ObsClient obsClient;
    private String bucketName;

    public void setObsClient(ObsClient obsClient) {
        this.obsClient = obsClient;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String fileUpload(String fileName, InputStream input) {
        PutObjectResult res = obsClient.putObject(bucketName, fileName, input);
        return res.getObjectUrl();
    }

    public String fileUpload(String fileName, File file) {
        PutObjectResult res = obsClient.putObject(bucketName, fileName, file);
        return res.getObjectUrl();
    }

    public boolean doesObjectExist(String fileName) {
        return obsClient.doesObjectExist(bucketName, fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObsService that = (ObsService) o;
        return Objects.equals(obsClient, that.obsClient) &&
                Objects.equals(bucketName, that.bucketName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obsClient, bucketName);
    }
}
