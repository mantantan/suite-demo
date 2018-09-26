package yonyou.esn.openapi.service;

import org.springframework.stereotype.Service;

@Service
public class TicketService {
    // 模拟缓冲存储suitetTicket,建议缓存持久化存储
    private String suiteTicket = null;

    /**
     * 票据保存更新
     * TODO: 建议使用缓存，持久化存储
     * @param ticket
     */
    public void saveSuiteTicket(String ticket) {
        suiteTicket = ticket;
    }

    /**
     * 票据获取
     * @return
     */
    public String getSuiteTicket() {
        return suiteTicket;
    }
}
