package yonyou.esn.openapi.uitils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yonyou.esn.openapi.common.CodeEnum;
import yonyou.esn.openapi.exception.BizException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 提供接收和推送给开放平台消息的加解密接口(UTF8编码的字符串).
 * <ol>
 * <li>第三方回复加密消息给公众平台</li>
 * <li>第三方收到平台发送的消息，验证消息的安全性，并对消息进行解密。</li>
 * </ol>
 * 说明：异常java.security.InvalidKeyException:illegal Key Size的解决方案
 * <ol>
 * <li>在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
 * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-
 * 432124.html</li>
 * <li>下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt</li>
 * <li>如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件</li>
 * <li>如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件</li>
 * </ol>
 */
public class BizMsgCrypt {
    static Charset CHARSET = Charset.forName("utf-8");

    Base64 base64 = new Base64();

    byte[] aesKey;

    String token;

    String suiteKey;

    private static final Logger logger = LoggerFactory.getLogger(BizMsgCrypt.class);

    /**
     * 构造函数
     *
     * @param token 开发者设置的token
     * @param encodingAesKey 开发者设置的EncodingAESKey
     * @param suiteKey
     */
    public BizMsgCrypt(String token, String encodingAesKey, String suiteKey)
            throws BizException {
        if (encodingAesKey.length() != 43) {
            throw new BizException(CodeEnum.C_91004);
        }

        this.token = token;
        this.suiteKey = suiteKey;
        aesKey = Base64.decodeBase64(encodingAesKey + "=");
    }

    // 还原4个字节的网络字节序
    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    /**
     * 对密文进行解密.
     * 
     * @param text 需要解密的密文
     * @return 解密得到的明文
     */
    String decrypt(String text) throws BizException {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv =
                    new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.decodeBase64(text);

            // 解密
            original = cipher.doFinal(encrypted);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BizException(CodeEnum.C_91007);
        }

        String xmlContent, from_suiteKey;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和suiteKey
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            xmlContent =
                    new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength),
                            CHARSET);
            from_suiteKey =
                    new String(Arrays.copyOfRange(bytes, 20 + xmlLength,
                            bytes.length), CHARSET);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BizException(CodeEnum.C_91004);
        }

        // suiteKey不相同的情况
        if (!from_suiteKey.equals(suiteKey)) {
            throw new BizException(CodeEnum.C_91005);
        }
        return xmlContent;

    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取xml中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     * 
     * @param msgSignature 签名串，对应URL参数的msg_signature
     * @param timeStamp 时间戳，对应URL参数的timestamp
     * @param nonce 随机串，对应URL参数的nonce
     * @param postData 密文，对应POST请求的数据
     * 
     * @return 解密后的原文
     */
    public String DecryptMsg(String msgSignature, String timeStamp,
            String nonce, String postData) throws BizException {
    	String signature = SHA1.getSHA1(token, timeStamp, nonce, postData);
        if (!signature.equals(msgSignature)) {
            throw new BizException(CodeEnum.C_91005);
        }
        // 解密
        String result = decrypt(postData);
        return result;
    }
}
