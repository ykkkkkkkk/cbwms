package ykk.cb.com.cbwms.comm;

import okhttp3.MediaType;


/**
 * 网络访问地址类
 * @author ykk
 *
 */
public class Consts {
//	/**
//	 * WebService
//	 * 基于soap协议
//	 * webService uri
// 	 */
//	public static final String WEB_URI = "http://183.60.183.21:8009/cbsw.asmx";
//	/**
//	 * WebService
//	 * 基于soap协议
//	 * XML的命名空间
//	 */
//	public static final String XMLNS = "http://tempuri.org/";

	public static String mIp;// 服务器ip地址
	public static String mPort;// 服务器端口

	/**
	 * 服务器的地址
	 */
	public static final String getURL(String param) {
		return "http://"+mIp+":"+mPort+"/xcwms/"+param;
	}
	public static final String getApkURL() {	return "http://"+mIp+":"+mPort+"/apks/xcwms.apk"; }

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


	/**
	 * 构造(get,set)
	 * ip：192.168.3.214
	 * 端口：8080
	 */
	public static void setIp(String ip) {
		mIp = ip;
	}

	public static String getIp() {
		return mIp;
	}

	public static void setPort(String port) {
		mPort = port;
	}

	public static String getPort() {
		return mPort;
	}

}