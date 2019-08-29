package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 调用k3返回的多种结果
 * @author Administrator
 *
 */
public class ReturnMsg implements Serializable{
	private int retId;
	private String retMsg; // 返回执行过程中发生的错误提示
	private String retObj; // 返回的对象

	public ReturnMsg() {
		super();
	}

	public int getRetId() {
		return retId;
	}
	public void setRetId(int retId) {
		this.retId = retId;
	}
	public String getRetMsg() {
		return retMsg;
	}
	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}
	public String getRetObj() {
		return retObj;
	}

	public void setRetObj(String retObj) {
		this.retObj = retObj;
	}


}
