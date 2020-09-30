package cn.keking.huawei;


import com.obs.services.ObsClient;
import com.obs.services.model.ListObjectsRequest;
import com.obs.services.model.ObjectListing;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectResult;
import com.obs.services.model.fs.NewFolderRequest;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObsService {
    private ObsClient obsClient;
    private String bucketName;
    private String endPoint;
    ;

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

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

    public String getObject(String fileName) {
        return obsClient.getObject(bucketName, fileName).getObjectKey();
    }

    /**
     * {参考文档 @see https://support.huaweicloud.com/obs_faq/obs_faq_0031.html}
     *
     * @param fileName 文件名
     */
    public String getObjectUrl(String fileName) {
        return "https://" + bucketName + "." + endPoint + "/" + fileName;
    }

    public void newFolder(String folderName) {
        if (obsClient.doesObjectExist(bucketName, folderName)) {
            return;
        }
        obsClient.newFolder(new NewFolderRequest(bucketName, folderName));
    }

    public List<String> listFolderFiles(String folderName) {
        ListObjectsRequest objectsRequest = new ListObjectsRequest(bucketName);
        String prefix = folderName + "/";
        objectsRequest.setPrefix(prefix);
        ObjectListing result = obsClient.listObjects(objectsRequest);
        List<String> ret = new ArrayList<>();
        for (ObsObject object : result.getObjects()) {
            String objectKey = object.getObjectKey();
            if (Objects.equals(objectKey,prefix)) {
                continue;
            }
            String r = "https://" + bucketName + "." + endPoint + "/"  + objectKey;
            ret.add(r);
        }
        return ret;
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
