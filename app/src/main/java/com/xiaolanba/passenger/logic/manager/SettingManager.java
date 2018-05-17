package com.xiaolanba.passenger.logic.manager;


/**
 * 配置信息设置
 *
 * @author xutingz
 * @company xiaolanba.com
 */
public class SettingManager {

    /**
     * 是否打开开发者选项 true 为打开
     */
    public static final boolean DEBUG = true;
    /**
     * 是否要创建桌面快捷方式
     */
    public static final boolean INIT_SHORTCUT = true;
    /**
     * APP默认联网控制 false 默认不允许联网， true默认允许联网
     */
    public static final boolean APP_DEFAULT_NET_ENABLED = true;

    /**
     * 服务器环境地址
     */
//	public static ServiceAddress ADDRESS = ServiceAddress.DEV;			// 169开发环境
//	public static ServiceAddress ADDRESS = ServiceAddress.TEST;			// 204测试环境
//	public static ServiceAddress ADDRESS = ServiceAddress.DEMO;			// 预发布环境
    public static ServiceAddress ADDRESS = ServiceAddress.RELEASE;        // 正式环境

    /**
     * 基线地址
     */
//	public static UmsAddress UMSADDRESS = UmsAddress.DEV;				// 测试地址
    public static UmsAddress UMSADDRESS = UmsAddress.RELEASE;            // 正式地址

    /**
     * 服务器地址空间
     */
    public static final String ADDRESS_NAMESPACE = "/router";
    /**
     * 头像上传地址空间
     */
    public static final String HEAD_UPLOAD_NAMESPACE = "/file/headimagupload";
    /**
     * 文件上传地址空间
     */
    public static final String FILE_UPLOAD_NAMESPACE = "/file/upload";
    /**
     * HTTP
     */
    public static final String HTTP = "http://";

    /**
     * 服务器地址
     */
    public static String SERVICE_ADDRESS = ADDRESS.SERVICE_ADDRESS;
    /**
     * 头像上传地址
     */
    public static String SERVICE_HEAD_UPLOAD_ADDRESS = ADDRESS.HEAD_UPLOAD_ADDRESS;
    /**
     * 文件上传地址
     */
    public static String SERVICE_FILE_UPLOAD_ADDRESS = ADDRESS.FILE_UPLOAD_ADDRESS;
    /**
     * 寻址
     */
    public static final String URL_FIND_ADDRESS = "http://api.9zhiad.com/router";

    /**
     * 服务器地址
     */
    public enum ServiceAddress {
        DEV("dev", "192.168.1.169", "192.168.1.169"),
        TEST("test", "192.168.1.204", "192.168.1.204"),
        DEMO("demo", "demoapi.9zhiad.com", "demoupload.9zhiad.com"),
        RELEASE("release", "api.9zhiad.com", "upload.9zhiad.com");

        public String ENVIRONMENT;
        public String HOST;
        public String UPLOADHOST;

        String SERVICE_ADDRESS;
        String HEAD_UPLOAD_ADDRESS;
        String FILE_UPLOAD_ADDRESS;

        ServiceAddress(String environment, String host, String uploadHost) {
            this.ENVIRONMENT = environment;
            this.HOST = host;
            this.UPLOADHOST = uploadHost;

            this.SERVICE_ADDRESS = HTTP + HOST + ADDRESS_NAMESPACE;
            this.HEAD_UPLOAD_ADDRESS = HTTP + UPLOADHOST + HEAD_UPLOAD_NAMESPACE;
            this.FILE_UPLOAD_ADDRESS = HTTP + UPLOADHOST + FILE_UPLOAD_NAMESPACE;
        }
    }

    /**
     * 基线地址
     */
    public enum UmsAddress {
        DEV("http://192.168.1.226/router?method="),
        RELEASE("http://ums.9zhiad.com/router?method=");

        public String URL;

        UmsAddress(String url) {
            this.URL = url;
        }
    }

    /**
     * 更新地址
     */
    public static void changeAddress() {
        SERVICE_ADDRESS = HTTP + ADDRESS.HOST + ADDRESS_NAMESPACE;
        SERVICE_HEAD_UPLOAD_ADDRESS = HTTP + ADDRESS.UPLOADHOST + HEAD_UPLOAD_NAMESPACE;
        SERVICE_FILE_UPLOAD_ADDRESS = HTTP + ADDRESS.UPLOADHOST + FILE_UPLOAD_NAMESPACE;
    }

    /**
     * 牙牙关注使用协议
     */
    public static final String URL_AGREEMENT = "http://www.21yaya.com/agreement.htm";

    /**
     * 版权声明
     */
    public static final String URL_COPYRIGHT = "http://www.21yaya.com/copyright.htm";

    /**
     * 牙牙关注注册协议
     */
    public static final String URL_REG = "http://file.yygz.9zhiad.com/reg.html";

    /**
     * 牙牙关注微博
     */
    public static final String URL_YAYA_WEIBO = "http://weibo.cn/yayaguanzhu";

    /**
     * 牙牙关注官网
     */
    public static final String URL_YAYA_OFFICIAL = "http://www.21yaya.com";

}
