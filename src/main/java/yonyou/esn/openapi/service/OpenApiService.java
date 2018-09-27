package yonyou.esn.openapi.service;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yonyou.esn.openapi.bo.BaseUserInfo;
import yonyou.esn.openapi.bo.PermanentCodeBo;
import yonyou.esn.openapi.configrations.SuiteConfig;
import yonyou.esn.openapi.uitils.HttpReq;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.*;

@Service
public class OpenApiService {
    private static final Logger LOG = LoggerFactory.getLogger(OpenApiService.class);

    @Value("${openapi.host}")
    private String openapiHost;
    @Autowired
    private SuiteConfig suiteConfig;
    @Autowired
    private TicketService ticketService;

    /**
     * 获取suiteToken
     * suiteToken有效期为7200s，建议缓存
     */
    public String getSuiteToken() {
        String url = openapiHost + "/suite/get_suite_token";
        String suiteTicket = ticketService.getSuiteTicket();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("suiteKey", suiteConfig.suiteKey);
        jsonObject.put("suiteSecret", suiteConfig.suiteSecret);
        jsonObject.put("suiteTicket", suiteTicket);
        String backData = HttpReq.postBody(url, jsonObject.toJSONString());
        JSONObject backDataObj = JSONObject.parseObject(backData);
        String data = backDataObj.getString("data");
        if (data == null) {
            LOG.error("获得access_token出错,返回结果: {}", backData);
            return null;
        } else {
            String suiteToken = JSONObject.parseObject(data).getString("suite_access_token");
            Long expiresIn = JSONObject.parseObject(data).getLongValue("expires_in");
            LOG.info("已获得套件访问令牌=" + suiteToken + ",有效时间是" + (expiresIn / 60 / 60) + "小时");
            return suiteToken;
        }
    }

    /**
     * 获取租户access_token
     * access_token有效期为7200s，建议缓存
     *
     * @return
     */
    public String getTenantAccessToken(String suiteToken, String permanentCode, String corpId) {
        String url = openapiHost + "/api/rest/token/tenant_access_token?suite_token=" + suiteToken;
        JSONObject jsObj = new JSONObject();
        jsObj.put("suiteKey", suiteConfig.suiteKey);
        jsObj.put("permanentCode", permanentCode);
        jsObj.put("corpId", corpId);
        String backData = HttpReq.postBody(url, jsObj.toJSONString());

        String data = JSONObject.parseObject(backData).getString("data");
        String accessToken = "";
        if (data == null) {
            LOG.error("获得租户访问令牌失败,返回结果:{}", backData);
            return null;
        } else {
            accessToken = JSONObject.parseObject(data).getString("access_token");
            Long expiresIn = JSONObject.parseObject(data).getLongValue("expires_in");
            LOG.info("已获得租户访问令牌=" + accessToken + ",有效时间是" + (expiresIn / 60 / 60) + "小时");
            return accessToken;
        }
    }

    /**
     * 根据code获取基本用户信息
     *
     * @param code
     * @param suiteToken
     */
    public BaseUserInfo getUserBaseInfo(String code, String suiteToken) throws ConnectException, SocketTimeoutException {
        String url = openapiHost + "/api/rest/user/base_info/" + code ;
        Map<String, String> param = new LinkedHashMap<>();
        param.put("suite_token", suiteToken);
        String backData = HttpReq.sendGet(url, param);
        String data = JSONObject.parseObject(backData).getString("data");
        if (data == null) {
            LOG.error("getUserBaseInfo返回失败结果：{}", backData);
            return null;
        } else {
            return JSONObject.parseObject(data, BaseUserInfo.class);
        }
    }

    public String getStaffInfo(String yhtUserId, String accessToken) throws ConnectException, SocketTimeoutException {
        String url = openapiHost + "/api/rest/base/staff/info_by_user_id";
        Map<String, String> param = new LinkedHashMap<>();
        param.put("access_token", accessToken);
        param.put("yhtUserId", yhtUserId);
        String backData = HttpReq.sendGet(url, param);
        return backData;
    }


    public String getUserInfo(String yhtUserId, String accessToken) {
        String url = openapiHost + "/api/rest/user/list_by_ids?access_token=" + accessToken;
        JSONObject jsObj = new JSONObject();
        List<String> userIds = new ArrayList<>();
        userIds.add(yhtUserId);
        jsObj.put("userIds", userIds);
        String backData = HttpReq.postBody(url, jsObj.toJSONString());
        return backData;
    }
    /**
     * 获取PermanentCode， 只能在push消息的时候使用一次
     *
     * @return
     */
    public PermanentCodeBo getPermanentCode(String suiteToken, String tempCode) {
        String url = openapiHost + "/suite/auth/get_permanent_code?suite_token=" + suiteToken;
        JSONObject jsObj = new JSONObject();
        jsObj.put("suiteKey", suiteConfig.suiteKey);
        jsObj.put("tmpAuthCode", tempCode);
        String backData = HttpReq.postBody(url, jsObj.toJSONString());
        JSONObject backDataObj = JSONObject.parseObject(backData);
        String data = backDataObj.getString("data");

        if (data == null) {
            LOG.error("获得永久授权码失败,返回结果:{}", backDataObj);
            return null;
        } else {
            return JSONObject.parseObject(data, PermanentCodeBo.class);
        }
    }
}
