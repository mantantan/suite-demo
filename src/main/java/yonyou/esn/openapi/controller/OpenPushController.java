package yonyou.esn.openapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import yonyou.esn.openapi.bo.PermanentCodeBo;
import yonyou.esn.openapi.configrations.SuiteConfig;
import yonyou.esn.openapi.enums.PushType;
import yonyou.esn.openapi.service.OpenApiService;
import yonyou.esn.openapi.service.PermanentCodeService;
import yonyou.esn.openapi.service.TicketService;
import yonyou.esn.openapi.service.TokenService;
import yonyou.esn.openapi.uitils.MapUtil;
import yonyou.esn.openapi.uitils.WXBizMsgCrypt;

import java.util.Map;

import static yonyou.esn.openapi.common.ConstantValue.*;

/**
 * isv套件接入demo的controller，主要演示：
 * 1.接受推送消息获取ticket和临时code处理过程
 * 2.获取suite_token和access_token的过程
 * Created by mantantan on 2018/1/19.
 */
@Controller
@RequestMapping("/open")
public class OpenPushController {
    private static Logger logger = LoggerFactory.getLogger(OpenPushController.class);

    @Autowired
    private SuiteConfig suiteConfig;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private PermanentCodeService permanentCodeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private OpenApiService openApiService;

    /**
     * 服务端接收ticket/临时授权码
     * @param msgSignature       签名
     * @param timestamp          时间戳
     * @param nonce              随机数
     * @param encrypt            加密数据
     * @return
     */
    @PostMapping("/push")
    @ResponseBody
    public String push(@RequestParam("msg_signature") String msgSignature,
                               @RequestParam("timestamp") String timestamp,
                               @RequestParam("nonce") String nonce,
                               @RequestParam("encrypt") String encrypt) {
        encrypt = encrypt.replace("%2B", "+");
        Map<String, String> dataMap = decodeData(msgSignature, timestamp, nonce, encrypt);
        String infoType = dataMap.get(KEY_INFO_TYPE);

        // 推送的是ticket
        if (PushType.TICKET.getMessage().equals(infoType)) {
            String suiteTicket = dataMap.get(KEY_SUITE_TICKET);
            ticketService.saveSuiteTicket(suiteTicket);
        } else { // 推送的是临时码
            String tempCode = dataMap.get(KEY_AUTH_CODE);
            String corpId = dataMap.get(KEY_AUTH_CROP_ID);
            //如果推送的是临时授权码则进行授权动作
            String suiteToken = tokenService.getSuiteToken();
            PermanentCodeBo permanentCodeBo = openApiService.getPermanentCode(suiteToken, tempCode);
            permanentCodeService.savePermanentCode(permanentCodeBo);
        }
        return "success";
    }

    // 推送数据解码
    private Map<String, String> decodeData(String msgSignature, String timestamp, String nonce, String encrypt) {
        WXBizMsgCrypt msgCrypt = new WXBizMsgCrypt(suiteConfig.token, suiteConfig.EncodingAESKey, suiteConfig.suiteKey);
        String xmlString = msgCrypt.DecryptMsg(msgSignature, timestamp, nonce, encrypt);
        logger.info(xmlString);
        return MapUtil.xmlToMap(xmlString);
    }
}
