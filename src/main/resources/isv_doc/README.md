#### ISV轻应用通过开放平台接入企业空间文档说明(主要对于ISV开发人员)：
1. 在开放平台官网登录(如果没有账号请注册并登录)，在登录用户姓名处，选择服务商开发，进入开放平台后台管理界面。
    ```
    开放平台官网地址：https://open.diwork.com
    ```
2. 在基本信息中注册企业并认证。（如果企业资质信息未认证，会影响后续的套件开发流程）
3. 在"我的套件"页签下，点击"添加套件"，按要求填写完善后；保存信息，并记录套件的suiteKey、suiteSecret、token、aesKey。主要参数说明如下：
    ```
    token：用于票据、临时授权码推送时的数据加密解密。
    EncodingAESKey：用于票据、临时授权码推送时的数据加密解密。
    回调url：套件服务端用于接收票据、临时授权码推送的接口地址。
    套件key：即suiteKey，套件成功创建后，生成的套件唯一标识。
    套件secret：即suiteSecret，套件成功创建后，生成的套件标识，与suiteKey对应。
    ```
4. 编码开发工作如下：ISV套件后台主要完成接收ticket和临时授权码部分的编码开发(解密ticket流程参考开放平台官方文档，接入代码参考Isv授权Demo)。
    ```
    ticket解密后格式：
    <xml>
        <InfoType>suite_ticket</InfoType>
        <SuiteKey>0561ad88-6278-44e2-a125-40698cb1716d</SuiteKey>
        <SuiteTicket>d63064feaf9057ea037e33f63c4760a3a887c7520b26cbdf27cd991e1be65c43</SuiteTicket>
        <TimeStamp>1538028279095</TimeStamp>
    </xml>
    临时授权码解密后格式：
    <xml>
        <AuthCorpId>umah4j7z</AuthCorpId>
        <InfoType>authorized</InfoType>
        <SuitedKey>0561ad88-6278-44e2-a125-40698cb1716d</SuitedKey>
        <AuthorizationCode>540b95c16c25f114ca20302b1bbac98e4e502b6f652f701cc409e6b6e41e847c</AuthorizationCode>
        <TimeStamp>1538028434878</TimeStamp>
    </xml>
    ```
5. 在"测试"中新建测试企业可以测试套件授权流程，新建测试企业-授权管理-点击相应应用的授权按钮（模仿购买流程），授权给该企业，在套件服务端，查看是否有相应的永久授权码生成。完成授权后，点击进入测试企业，在全部应用中可以看到测试企业刚刚购买的应用。
6. 授权完成后，使用suiteKey+租户Id+永久授权码获取访问令牌Access_Token，Access_Token是套件通过开放平台接口访问数据的唯一凭证信息。
    ```
    Access_Token获取格式：
    {
        "code": 0,
        "msg": "success",
        "data": {
            "access_token": "5d6b8c217223ac40e869b18bf726892ef5b23412b2f95ac7ff02f58e5e9525fd",
            "expires_in": 7200
        }
    }
    ```
7. 添加应用，在套件编辑页面，点击创建应用，按要求填写完善后保存信息。主要参数说明如下：
    ```
    客户端：支持应用在网页、移动端、PC端打开，需要应用针对不同的客户端设置对应的主页地址。
    ```
    应用通过该地址打开时，平台会在地址后拼接请求参数?code=xxx，服务商可以在对应的接口中获取code参数，并调用平台相关接口，获取用户信息，实现免登。
    ```
    应用地址拼接code参数示例：
    https://b9175c64.ngrok.io/user_base_info?code=fc20414f73c51e4a919b87f84c5bb5d27a7b34e6f2c5b245fb47acdf6440&qzId=119521&groupname=%20%20%20%20%20%204&appId=142543&serviceCode=e73fb944-2c14-4910-bb3a-df4671f37822
    其中，应用地址是：https://b9175c64.ngrok.io/user_base_info
    拼接的code参数是：code=fc20414f73c51e4a919b87f84c5bb5d27a7b34e6f2c5b245fb47acdf6440
    ```
8. 测试通过,在"我的套件"中，点击上线，待开放平台管理员审核通过后，该套件下的应用才可以申请应用上架。
9. 应用申请上架后，可以在diwork应用市场中看到对应应用。截止这里，套件应用已经可以供diwork平台上企业用户购买并使用。

##### 特别说明:
1. 在添加套件过程中填写的"回调url"<br/> 示例:"http://具有外网的ip:port/项目路径(如果有)/open/push"
2. 开放平台[线上地址]：https://open.diwork.com
3. 解密消息异常java.security.InvalidKeyException:illegal Key Size的解决方案<br/>
    ```
    * 
    * 在官方网站下载JCE无限制权限策略文件（JDK7的下载地址：
    * http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html
    * 下载后解压，可以看到local_policy.jar和US_export_policy.jar以及readme.txt
    * 如果安装了JRE，将两个jar文件放到%JRE_HOME%\lib\security目录下覆盖原来的文件
    * 如果安装了JDK，将两个jar文件放到%JDK_HOME%\jre\lib\security目录下覆盖原来文件
    * 
    ```

4. properties文件说明:

    ```
    # 开放平台openapi host
    openapi.host=https://openapi.yonyoucloud.com
    # suite配置，下列信息均能配置在开放平台套件管理的套件中
    suite_config.token  => 创建套件的时候输入的token
    suite_config.suiteKey => 套件suiteKey
    suite_config.suiteSecret => 套件suiteSecret
    suite_config.EncodingAESKey => 加密解密的AESKey，创建套件的时候输入的key
    ```

5. **特别注意的是** <br/> 强烈建议您把接收到的"suiteKey"、"租户Id"和"永久授权码"持久化到数据库中用于标识 "哪个套件被授权给哪个企业!"，demo中用于持久化suiteKey+租户Id+永久授权码的数据表结构见文件：建表sql。

详细文档可参考： [开放平台文档中心](https://open.diwork.com/#/docs/character/isv)

