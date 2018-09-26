package yonyou.esn.openapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yonyou.esn.openapi.service.OpenApiService;
import yonyou.esn.openapi.service.TokenService;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@RestController
@RequestMapping("/")
public class ApiTestController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private OpenApiService openApiService;

    @RequestMapping("user_base_info")
    public Object getBaseInfo(String code) throws SocketTimeoutException, ConnectException {
        String suiteToken = tokenService.getSuiteToken();
        return openApiService.getUserBaseInfo(code, suiteToken);
    }

    @RequestMapping("suite_token")
    public String getSuiteToken(){
        return tokenService.getSuiteToken();
    }
    @RequestMapping("access_token")
    public String getAccessToken(String corpId){
        return tokenService.getTenantAccessToken(corpId);
    }

    @RequestMapping("staff_info")
    public String getStaffInfo(String accessToken, String yhtUserId) throws SocketTimeoutException, ConnectException {
        return openApiService.getStaffInfo(yhtUserId, accessToken);
    }
}
