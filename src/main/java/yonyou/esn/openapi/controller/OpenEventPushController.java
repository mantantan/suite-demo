package yonyou.esn.openapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yonyou.esn.openapi.bo.EncryptionHolder;
import yonyou.esn.openapi.bo.EventContent;
import yonyou.esn.openapi.configrations.SuiteConfig;
import yonyou.esn.openapi.uitils.BizMsgCrypt;

import java.io.IOException;
import java.util.Arrays;

/**
 * 演示接收企业数据变更事件推送处理过程
 */
@RestController
@RequestMapping("/open")
public class OpenEventPushController {

    private static final Logger logger = LoggerFactory.getLogger(OpenEventPushController.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SuiteConfig suiteConfig;

    @PostMapping("/event_push")
    public String eventPush(@RequestBody EncryptionHolder holder) throws IOException {

        // 验签解密，获得事件推送消息体
        EventContent content = decryptEvent(holder);

        // 根据事件类型进行相关处理操作，耗时操作推荐异步处理，平台默认推送超时时间为 5 秒，超时视为推送失败。
        processEvent(content);

        // 处理完毕，返回 "success" ，告知开放平台，否则该事件视为推送失败，平台会持续推送，直到 24 小时。
        return "success";
    }

    private EventContent decryptEvent(EncryptionHolder holder) throws IOException {
        BizMsgCrypt msgCrypt = new BizMsgCrypt(suiteConfig.token, suiteConfig.EncodingAESKey, suiteConfig.suiteKey);
        String jsonString = msgCrypt.DecryptMsg(holder.getMsgSignature(), String.valueOf(holder.getTimestamp()),
                holder.getNonce(), holder.getEncrypt());

        logger.info("新的企业数据变更事件，验签解密后的消息体：{}", jsonString);

        return mapper.readValue(jsonString, EventContent.class);
    }

    /**
     * 处理企业数据变更事件
     * 此处作为演示仅打印了数据变更 id，实际开发可以异步调用相关业务接口，拉取最新数据。
     *
     * @param content 变更的事件数据
     */
    private void processEvent(EventContent content) {

        switch (content.getType()) {
            case CHECK_URL:
                logger.info("事件类型: {}, 说明: 检查事件推送回调地址", content.getType());
                break;

            case STAFF_ADD:
                logger.info("事件类型: {}, 说明: 员工增加, 员工变更 id: {}", content.getType(), Arrays.toString(content.getStaffId()));
                break;
            case STAFF_UPDATE:
                logger.info("事件类型: {}, 说明: 员工更改, 员工变更 id: {}", content.getType(), Arrays.toString(content.getStaffId()));
                break;
            case STAFF_ENABLE:
                logger.info("事件类型: {}, 说明: 员工启用, 员工变更 id: {}", content.getType(), Arrays.toString(content.getStaffId()));
                break;
            case STAFF_DISABLE:
                logger.info("事件类型: {}, 说明: 员工停用, 员工变更 id: {}", content.getType(), Arrays.toString(content.getStaffId()));
                break;
            case STAFF_DELETE:
                logger.info("事件类型: {}, 说明: 员工删除, 员工变更 id: {}", content.getType(), Arrays.toString(content.getStaffId()));
                break;

            case DEPT_ADD:
                logger.info("事件类型: {}, 说明: 部门创建, 部门变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;
            case DEPT_UPDATE:
                logger.info("事件类型: {}, 说明: 部门修改, 部门变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;
            case DEPT_ENABLE:
                logger.info("事件类型: {}, 说明: 部门启用, 部门变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;
            case DEPT_DISABLE:
                logger.info("事件类型: {}, 说明: 部门停用, 部门变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;
            case DEPT_DELETE:
                logger.info("事件类型: {}, 说明: 部门删除, 部门变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;

            case USER_ADD:
                logger.info("事件类型: {}, 说明: 用户增加, 用户变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;
            case USER_DELETE:
                logger.info("事件类型: {}, 说明: 用户删除, 用户变更 id: {}", content.getType(), Arrays.toString(content.getDeptId()));
                break;

            case UNKNOWN:
                logger.info("未知事件");
                break;
        }
    }
}
