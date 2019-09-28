package ykk.cb.com.cbwms.model;

import java.io.Serializable;

/**
 * 工资类型
 */
public class WageType implements Serializable {
	private int id;
	private String wtName; // 名称

	public WageType() {
		super();
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getWtName() {
		return wtName;
	}

	public void setWtName(String wtName) {
		this.wtName = wtName;
	}


}
