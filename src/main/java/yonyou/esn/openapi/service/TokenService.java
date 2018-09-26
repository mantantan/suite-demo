package yonyou.esn.openapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yonyou.esn.openapi.bo.PermanentCodeBo;

@Service
public class TokenService {

    @Autowired
    private OpenApiService openApiService;
    @Autowired
    private PermanentCodeService permanentCodeService;

    /**
     * 获取访问租户接口的访问token
     * TODO: tenant access_token有效期为7200s，建议缓存，每次获取先从缓存取
     */
    public String getTenantAccessToken(String corpId) {
        PermanentCodeBo permanentCodeBo = permanentCodeService.getPermanentCode(corpId);
        return openApiService.getTenantAccessToken(getSuiteToken(), permanentCodeBo.getPermanentCode(), permanentCodeBo.getCorpId());
    }

    /**
     * 获取套件的token
     * TODO: suite token有效期为7200s，建议缓存，每次获取先从缓存取
     */
    public String getSuiteToken() {
        return openApiService.getSuiteToken();
    }


}
