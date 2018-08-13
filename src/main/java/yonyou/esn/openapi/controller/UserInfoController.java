package yonyou.esn.openapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yonyou.esn.openapi.service.AuthAppSuiteService;
import yonyou.esn.openapi.uitils.HttpReq;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @Value("${auth_base_path}")
    private String openapiHost;
    @Autowired
    private AuthAppSuiteService authAppSuiteService;

    @RequestMapping("/user_info")
    public String userInfo(@RequestParam String code, @RequestParam String qzId) throws ConnectException, SocketTimeoutException {
        String suiteAccessToken = authAppSuiteService.getSuiteAccessToken(authAppSuiteService.getSuiteTicket());
        String permanentCode = authAppSuiteService.getNativePermanentCode(qzId);
        String accessToken = authAppSuiteService.getAccessToken(suiteAccessToken,  permanentCode);
        String url = openapiHost + "/openapi/certified/userInfo/" + code;
        Map<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        return HttpReq.sendGet(url, map);
    }
}
