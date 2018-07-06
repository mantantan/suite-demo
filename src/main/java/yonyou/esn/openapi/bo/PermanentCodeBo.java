package yonyou.esn.openapi.bo;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.Date;

/**
 * Created by mantantan on 2018/1/19.
 */
public class PermanentCodeBo {
    private int id;
    private Integer qzId;
    private String qzName;
    private String corpId;
    private String corpName;
    private Integer authMemberId;
    private String suiteKey;
    private String permanentCode;
    private Date createTime;

    public PermanentCodeBo(Integer qzId, String suiteKey, String permanentCode,String corpId,String corpName){
        this.qzId = qzId;
        this.suiteKey = suiteKey;
        this.permanentCode = permanentCode;
        this.corpId=corpId;
        this.corpName=corpName;
    }

    public PermanentCodeBo(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getQzId() {
        return qzId;
    }

    public void setQzId(Integer qzId) {
        this.qzId = qzId;
    }

    public String getSuiteKey() {
        return suiteKey;
    }

    public void setSuiteKey(String suiteKey) {
        this.suiteKey = suiteKey;
    }

    public String getPermanentCode() {
        return permanentCode;
    }

    public void setPermanentCode(String permanentCode) {
        this.permanentCode = permanentCode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQzName() {
        return qzName;
    }

    public void setQzName(String qzName) {
        this.qzName = qzName;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public Integer getAuthMemberId() {
        return authMemberId;
    }

    public void setAuthMemberId(Integer authMemberId) {
        this.authMemberId = authMemberId;
    }
}
