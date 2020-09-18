package cn.keking.huawei;

import com.cloud.apigateway.sdk.utils.AccessServiceOkhttp;
import com.cloud.apigateway.sdk.utils.AccessServiceOkhttpImpl;
import com.cloud.sdk.http.HttpMethodName;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 华为上报数据方法
 */
@Component
public class HuaweiReport {

    private final Logger logger = LoggerFactory.getLogger(HuaweiReport.class);

    private String ak = "BVY6PXOU7MLRPXOZBTMJ";
    private String sk = "OvHEoiL2sBCD4RoUHwMOFxow62gXO2q6sjGprF7H";
    private String partnerId = "hw16238094";
    private String mktUrl = "https://mkt.myhuaweicloud.com/v1.0/" + partnerId + "/billing/bill-mgr/push-usage-data";

    public Map<String, Object> queryUsage() {
        Map<String, Object> usageRecords = new HashMap<>();

        // 封装第一条业务计量数据(根据实际情况，将此段写在循环中处理)
        Map<String, Object> usageRecordData1 = new HashMap<String, Object>();
        // 产品实例ID
        usageRecordData1.put("instanceId", "455be31e-6771-4d6f-b8c1-7e8bdb10c21f");
        // 使用量记录生成时间（UTC），格式为：yyyyMMdd'T'HHmmss'Z
        usageRecordData1.put("recordTime", "20180117T151020Z");
        // 计量开始时间（UTC），格式为：yyyyMMdd'T'HHmmss'Z'
        usageRecordData1.put("beginTime", "20180117T150000Z");
        // 计量结束时间（UTC），格式为：yyyyMMdd'T'HHmmss'Z'
        usageRecordData1.put("endTime", "20180117T160000Z");

        // 计费实体集合
        Map<String, Object> usageEntity = new HashMap<>();
        // 使用次数（单位：次）：
        usageEntity.put("times", "3524");
        usageRecordData1.put("usageEntity", usageEntity);
        List<Map<String, Object>> usageRecordDataList = new ArrayList<>();
        usageRecordDataList.add(usageRecordData1);
        usageRecords.put("usageRecords", usageRecordDataList);

        return usageRecords;
    }

    public void push() {
        Map<String, Object> usage = queryUsage();
        if (usage == null) {
            logger.info("暂无需要业务使用量的数据推送");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestMsgBody = mapper.writeValueAsString(usage);
            AccessServiceOkhttp accessService = new AccessServiceOkhttpImpl(ak, sk);
            Request request = accessService.access(mktUrl, requestMsgBody, HttpMethodName.POST);
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() != 200) {
                throw new RuntimeException("调用推送接口异常");
            }
        } catch (Exception e) {
            logger.error("推送异常", e);
        }
    }

}
